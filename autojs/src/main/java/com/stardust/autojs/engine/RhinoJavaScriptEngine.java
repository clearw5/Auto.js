package com.stardust.autojs.engine;

import android.os.Looper;
import android.util.Log;

import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.rhino.AndroidContextFactory;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.automator.UiObjectCollection;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine extends JavaScriptEngine {

    private static final String LOG_TAG = "RhinoJavaScriptEngine";

    private static int contextCount = 0;
    private static StringScriptSource sInitScript;

    private Context mContext;
    private Scriptable mScriptable;
    private Thread mThread;
    private android.content.Context mAndroidContext;
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

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

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        mThread = Thread.currentThread();
        ScriptableObject.putProperty(mScriptable, "__engine__", this);
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
            return PFiles.read(mAndroidContext.getAssets().open("init.js"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void initRequireBuilder(Context context, Scriptable scope) {
        AssetAndUrlModuleSourceProvider provider = new AssetAndUrlModuleSourceProvider(mAndroidContext,
                Collections.singletonList(new File("/").toURI()));
        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(provider))
                .setSandboxed(false)
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

    public Context createContext() {
        //  if (!ContextFactory.hasExplicitGlobal()) {
        //    ContextFactory.initGlobal(new InterruptibleAndroidContextFactory(new File(mAndroidContext.getCacheDir(), "classes")));
        //}
        Context context = new InterruptibleAndroidContextFactory(new File(mAndroidContext.getCacheDir(), "classes")).enterContext();//new RhinoAndroidHelper(mAndroidContext).enterContext();
        contextCount++;
        setupContext(context);
        return context;
    }

    protected void setupContext(Context context) {
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        context.setLocale(Locale.getDefault());
        context.setWrapFactory(new WrapFactory());
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uiThreadExceptionHandler) {
        mUncaughtExceptionHandler = uiThreadExceptionHandler;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return mUncaughtExceptionHandler;
    }

    private class WrapFactory extends org.mozilla.javascript.WrapFactory {

        @Override
        public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
            if (obj instanceof String) {
                return getRuntime().bridges.toString(obj.toString());
            }
            if (staticType == UiObjectCollection.class) {
                return getRuntime().bridges.toArray(obj);

            }
            return super.wrap(cx, scope, obj, staticType);
        }

    }

    private class InterruptibleAndroidContextFactory extends AndroidContextFactory {

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

        @Override
        protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                try {
                    return super.doTopCall(callable, cx, scope, thisObj, args);
                } catch (Exception e) {
                    mUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
                    return null;
                }
            }
            return super.doTopCall(callable, cx, scope, thisObj, args);
        }
    }


}
