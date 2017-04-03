package com.stardust.autojs.engine;

import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.autojs.script.ScriptSource;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created by Stardust on 2017/4/2.
 */

public class RhinoJavaScriptEngine implements JavaScriptEngine {

    static {
        ContextFactory.initGlobal(new InterruptibleContextFactory());
    }

    private Context mContext;
    private Scriptable mScriptable;
    private Thread mThread;
    private RhinoJavaScriptEngineManager mEngineManager;
    private ScriptSource mScriptSource;

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
        mScriptSource = source;
        return mContext.evaluateString(mScriptable, source.getScript(), "<script>", 1, null);
    }

    @Override
    public ScriptSource getExecutedScript() {
        return mScriptSource;
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
        mEngineManager.removeEngine(this);
    }

    @Override
    public void init() {
        ScriptableObject.putProperty(mScriptable, "__engine__", "rhino");
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
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        return context;
    }

    private static class InterruptibleContextFactory extends ContextFactory {

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
