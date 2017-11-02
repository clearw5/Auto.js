package com.stardust.autojs.engine;

import android.util.Log;

import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.core.accessibility.UiCollection;
import com.stardust.autojs.rhino.AndroidContextFactory;
import com.stardust.autojs.rhino.RhinoAndroidHelper;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.automator.UiObjectCollection;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.tools.debugger.Dim;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine extends JavaScriptEngine {

    public interface TypeWrapper {

        Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType);
    }

    private static final String LOG_TAG = "RhinoJavaScriptEngine";
    private static Constructor<?> nativeStringConstructor;

    private static int contextCount = 0;
    private static StringScriptSource sInitScript;
    private String[] mRequirePath = new String[0];

    private Context mContext;
    private Scriptable mScriptable;
    private Thread mThread;
    private android.content.Context mAndroidContext;

    public RhinoJavaScriptEngine(android.content.Context context) {
        mAndroidContext = context;
        mContext = createContext();
        mScriptable = createScope(mContext);
    }

    @Override
    public void put(String name, Object value) {
        ScriptableObject.putProperty(mScriptable, name, Context.javaToJS(value, mScriptable));
    }

    @Override
    public Object doExecution(JavaScriptSource source) {
        Reader reader = source.getNonNullScriptReader();
        try {
            reader = preprocess(reader);
            return mContext.evaluateReader(mScriptable, reader, "<" + source.getName() + ">", 1, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected Reader preprocess(Reader script) throws IOException {
        return script;
    }

    @Override
    public void forceStop() {
        Log.d(LOG_TAG, "forceStop: interrupt Thread: " + mThread);
        mThread.interrupt();
    }


    @Override
    public synchronized void destroy() {
        super.destroy();
        Log.d(LOG_TAG, "on destroy");
        Context.exit();
        contextCount--;
        Log.d(LOG_TAG, "contextCount = " + contextCount);
    }

    public Thread getThread() {
        return mThread;
    }

    @Override
    public void init() {
        mThread = Thread.currentThread();
        ScriptableObject.putProperty(mScriptable, "__engine__", this);
        mRequirePath = (String[]) getTag(TAG_PATH);
        initRequireBuilder(mContext, mScriptable);
        mContext.evaluateString(mScriptable, getInitScript().getScript(), "<init>", 1, null);
    }

    private JavaScriptSource getInitScript() {
        if (sInitScript == null || BuildConfig.DEBUG)
            sInitScript = new StringScriptSource(readInitScript());
        return sInitScript;
    }

    private String readInitScript() {
        try {
            return PFiles.read(mAndroidContext.getAssets().open("javascript_engine_init.js"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void initRequireBuilder(Context context, Scriptable scope) {
        List<URI> list = new ArrayList<>();
        for (String path : mRequirePath) {
            list.add(new File(path).toURI());
        }
        AssetAndUrlModuleSourceProvider provider = new AssetAndUrlModuleSourceProvider(mAndroidContext, list);
        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(provider))
                .setSandboxed(true)
                .createRequire(context, scope)
                .install(scope);

    }

    public Context getContext() {
        return mContext;
    }

    public Scriptable getScriptable() {
        return mScriptable;
    }

    protected Scriptable createScope(Context context) {
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        importerTopLevel.initStandardObjects(context, false);
        return importerTopLevel;
    }

    protected Context createContext() {
        if (!ContextFactory.hasExplicitGlobal()) {
            ContextFactory.initGlobal(new InterruptibleAndroidContextFactory(new File(mAndroidContext.getCacheDir(), "classes")));
        }
        Context context = new RhinoAndroidHelper(mAndroidContext).enterContext();
        contextCount++;
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        context.setLocale(Locale.getDefault());
        context.setWrapFactory(new WrapFactory());
        return context;
    }

    private Object createNativeString(Object obj) {
        if (nativeStringConstructor == null)
            return obj;
        try {
            return nativeStringConstructor.newInstance(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return obj;
        }
    }

    private class WrapFactory extends org.mozilla.javascript.WrapFactory {
        @Override
        public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
            if (staticType == String.class) {
                return getRuntime().bridges.toString(obj);
            }
            if (staticType == UiObjectCollection.class ) {
                return getRuntime().bridges.toArray(obj);

            }
            return super.wrap(cx, scope, obj, staticType);
        }
    }

    private static class InterruptibleAndroidContextFactory extends AndroidContextFactory {

        public InterruptibleAndroidContextFactory(File cacheDirectory) {
            super(cacheDirectory);
        }

        @Override
        protected void observeInstructionCount(Context cx, int instructionCount) {
            if (Thread.currentThread().isInterrupted()) {
                throw new ScriptInterruptedException();
            }
        }

        @Override
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setInstructionObserverThreshold(10000);
            return cx;
        }
    }


    static {
        try {
            Class c = Class.forName("org.mozilla.javascript.NativeString");
            nativeStringConstructor = c.getDeclaredConstructor(CharSequence.class);
            nativeStringConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
