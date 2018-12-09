package com.stardust.autojs.engine;

import android.util.Log;
import android.view.View;

import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.core.ui.ViewExtras;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;
import com.stardust.autojs.rhino.RhinoAndroidHelper;
import com.stardust.autojs.rhino.TopLevelScope;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.automator.UiObjectCollection;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine extends JavaScriptEngine {

    public static final String SOURCE_NAME_INIT = "<init>";

    private static final String LOG_TAG = "RhinoJavaScriptEngine";

    private static final String MODULES_PATH = "modules";
    private static Script sInitScript;
    private static final ConcurrentHashMap<Context, RhinoJavaScriptEngine> sContextEngineMap = new ConcurrentHashMap<>();

    private Context mContext;
    private TopLevelScope mScriptable;
    private Thread mThread;
    private android.content.Context mAndroidContext;

    public RhinoJavaScriptEngine(android.content.Context context) {
        mAndroidContext = context;
        mContext = enterContext();
        mScriptable = createScope(mContext);
    }

    @Override
    public void put(String name, Object value) {
        ScriptableObject.putProperty(mScriptable, name, Context.javaToJS(value, mScriptable));
    }

    @Override
    public void setRuntime(ScriptRuntime runtime) {
        super.setRuntime(runtime);
        runtime.setTopLevelScope(mScriptable);
    }

    @Override
    public Object doExecution(JavaScriptSource source) {
        Reader reader = source.getNonNullScriptReader();
        try {
            reader = preprocess(reader);
            Script script = mContext.compileReader(reader, source.toString(), 1, null);
            return mContext.executeScriptWithContinuations(script, mScriptable);
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
        sContextEngineMap.remove(getContext());
        Context.exit();
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
        mContext.executeScriptWithContinuations(getInitScript(), mScriptable);
    }

    private Script getInitScript() {
        if (sInitScript == null || BuildConfig.DEBUG) {
            try {
                Reader reader = new InputStreamReader(mAndroidContext.getAssets().open("init.js"));
                sInitScript = mContext.compileReader(reader, SOURCE_NAME_INIT, 1, null);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return sInitScript;
    }

    void initRequireBuilder(Context context, Scriptable scope) {
        AssetAndUrlModuleSourceProvider provider = new AssetAndUrlModuleSourceProvider(mAndroidContext, MODULES_PATH,
                Collections.singletonList(new File("/").toURI()));
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

    protected TopLevelScope createScope(Context context) {
        TopLevelScope topLevelScope = new TopLevelScope();
        topLevelScope.initStandardObjects(context, false);
        return topLevelScope;
    }

    public Context enterContext() {
        Context context = new RhinoAndroidHelper(mAndroidContext).enterContext();
        setupContext(context);
        sContextEngineMap.put(context, this);
        return context;
    }

    protected void setupContext(Context context) {
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        context.setLocale(Locale.getDefault());
        context.setWrapFactory(new WrapFactory());
    }


    public static RhinoJavaScriptEngine getEngineOfContext(Context context) {
        return sContextEngineMap.get(context);
    }

    private class WrapFactory extends org.mozilla.javascript.WrapFactory {

        @Override
        public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
            Object result;
            if (obj instanceof String) {
                result = getRuntime().bridges.toString(obj.toString());
            } else if (staticType == UiObjectCollection.class) {
                result = getRuntime().bridges.asArray(obj);
            } else {
                result = super.wrap(cx, scope, obj, staticType);
            }
            return result;
        }

        @Override
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
            Scriptable result;
            if (javaObject instanceof View) {
                result = ViewExtras.getNativeView(scope, (View) javaObject, staticType, getRuntime());
            } else {
                result = super.wrapAsJavaObject(cx, scope, javaObject, staticType);
            }
            //Log.d(LOG_TAG, "wrapAsJavaObject: java = " + javaObject + ", result = " + result + ", scope = " + scope);
            return result;
        }

    }


}
