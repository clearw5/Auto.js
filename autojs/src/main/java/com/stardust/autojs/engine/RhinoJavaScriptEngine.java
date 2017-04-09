package com.stardust.autojs.engine;

import android.util.Log;

import com.stardust.autojs.rhino_android.AndroidContextFactory;
import com.stardust.autojs.rhino_android.RhinoAndroidHelper;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine implements JavaScriptEngine {

    private static final String LOG_TAG = "RhinoJavaScriptEngine";

    private Context mContext;
    private Scriptable mScriptable;
    private Thread mThread;
    private RhinoJavaScriptEngineManager mEngineManager;
    private Map<String, Object> mTags = new Hashtable<>();

    public RhinoJavaScriptEngine(RhinoJavaScriptEngineManager engineManager) {
        mEngineManager = engineManager;
        mThread = Thread.currentThread();
        mContext = createContext();
        mScriptable = createScope(mContext);
        setTag("create-traces", Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    @Override
    public void put(String name, Object value) {
        ScriptableObject.putProperty(mScriptable, name, Context.javaToJS(value, mScriptable));
    }

    @Override
    public Object execute(ScriptSource source) {
        setTag("execute-traces", Arrays.toString(Thread.currentThread().getStackTrace()));
        setTag("execute-source", source);
        return mContext.evaluateString(mScriptable, source.getScript(), "<script>", 1, null);
    }

    @Override
    public void forceStop() {
        mThread.interrupt();
    }

    public RhinoJavaScriptEngineManager getEngineManager() {
        return mEngineManager;
    }

    @Override
    public void destroy() {
        Context.exit();
        // TODO: 2017/4/6 XXX :在这里回收内存池并不好
        final AccessibilityNodeInfoAllocator allocator = (AccessibilityNodeInfoAllocator) getTag("allocator");
        if (allocator != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //allocator.recycleAll();
                }
            }, 1000);
        }
        mEngineManager.removeEngine(this);
    }

    @Override
    public synchronized void setTag(String key, Object value) {
        mTags.put(key, value);
    }

    @Override
    public synchronized Object getTag(String key) {
        Object tag = mTags.get(key);
        if (tag == null && key.equals("script")) {
            Log.i(LOG_TAG, mTags.entrySet().toString());
        }
        return mTags.get(key);
    }

    @Override
    public void init() {
        ScriptableObject.putProperty(mScriptable, "__engine_name__", "rhino");
        ScriptableObject.putProperty(mScriptable, "__engine__", this);
        mContext.evaluateString(mScriptable, mEngineManager.getInitScript().getScript(), "<init>", 1, null);
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
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        return context;
    }

    private static class InterruptibleAndroidContextFactory extends AndroidContextFactory {

        public InterruptibleAndroidContextFactory(File cacheDirectory) {
            super(cacheDirectory);
        }

        @Override
        protected void observeInstructionCount(Context cx, int instructionCount) {
            if (Thread.currentThread().isInterrupted()) {

                throw new ScriptStopException(new InterruptedException());
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
