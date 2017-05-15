package com.stardust.autojs.engine;

import android.util.Log;

import com.stardust.autojs.engine.preprocess.Preprocessor;
import com.stardust.autojs.rhino_android.AndroidContextFactory;
import com.stardust.autojs.rhino_android.RhinoAndroidHelper;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine implements ScriptEngine {

    private static final String LOG_TAG = "RhinoJavaScriptEngine";
    private static final Preprocessor PREPROCESSOR = new MultiLinePreprocessor();

    private static int contextCount = 0;
    private String[] mRequirePath = new String[0];

    private Context mContext;
    private Scriptable mScriptable;
    private Thread mThread;
    private RhinoJavaScriptEngineManager mEngineManager;
    private Map<String, Object> mTags = new ConcurrentHashMap<>();

    public RhinoJavaScriptEngine(RhinoJavaScriptEngineManager engineManager) {
        mEngineManager = engineManager;
        mThread = Thread.currentThread();
        mContext = createContext();
        mScriptable = createScope(mContext);
    }

    @Override
    public void put(String name, Object value) {
        ScriptableObject.putProperty(mScriptable, name, Context.javaToJS(value, mScriptable));
    }

    @Override
    public Object execute(ScriptSource source) {
        Reader reader = source.getNonNullScriptReader();
        try {
            reader = preprocess(reader);
            return mContext.evaluateReader(mScriptable, reader, "<script>", 1, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Reader preprocess(Reader script) throws IOException {
        return PREPROCESSOR.preprocess(script);
    }

    @Override
    public void forceStop() {
        Log.d(LOG_TAG, "forceStop: interrupt Thread: " + mThread);
        mThread.interrupt();
    }

    public RhinoJavaScriptEngineManager getEngineManager() {
        return mEngineManager;
    }

    @Override
    public synchronized void destroy() {
        Log.d(LOG_TAG, "on destroy");
        Context.exit();
        contextCount--;
        Log.d(LOG_TAG, "contextCount = " + contextCount);
        mEngineManager.removeEngine(this);
    }

    @Override
    public synchronized void setTag(String key, Object value) {
        mTags.put(key, value);
    }

    @Override
    public synchronized Object getTag(String key) {
        return mTags.get(key);
    }

    @Override
    public void init() {
        ScriptableObject.putProperty(mScriptable, "__engine_name__", "rhino");
        ScriptableObject.putProperty(mScriptable, "__engine__", this);
        mRequirePath = (String[]) getTag("__require_path__");
        initRequireBuilder(mContext, mScriptable);
        mContext.evaluateString(mScriptable, mEngineManager.getInitScript().getScript(), "<init>", 1, null);
    }

    public void setRequirePath(String... requirePath) {
        setTag("__require_path__", requirePath);
    }

    void initRequireBuilder(Context context, Scriptable scope) {
        List<URI> list = new ArrayList<>();
        for (String path : mRequirePath) {
            list.add(new File(path).toURI());
        }
        AssetAndUrlModuleSourceProvider provider = new AssetAndUrlModuleSourceProvider(getEngineManager().getContext(), list);
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

    protected Context createContext() {
        if (!ContextFactory.hasExplicitGlobal()) {
            ContextFactory.initGlobal(new InterruptibleAndroidContextFactory(new File(mEngineManager.getContext().getCacheDir(), "classes")));
        }
        Context context = new RhinoAndroidHelper(mEngineManager.getContext()).enterContext();
        contextCount++;
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        return context;
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

}
