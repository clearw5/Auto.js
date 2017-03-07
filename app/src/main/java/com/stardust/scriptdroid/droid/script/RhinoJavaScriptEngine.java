package com.stardust.scriptdroid.droid.script;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.ScriptStopException;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2017/3/1.
 */

public class RhinoJavaScriptEngine extends JavaScriptEngine {

    static {
        ContextFactory.initGlobal(new InterruptibleContextFactory());
    }

    private final Set<Thread> mThreads = new HashSet<>();

    public RhinoJavaScriptEngine(IDroidRuntime runtime) {
        set("droid", runtime);
    }

    @Override
    public Object execute(String script) {
        Context context = createContext();
        Scriptable scope = createScope(context);
        init(context, scope);
        Object result = context.evaluateString(scope, script, "<script>", 1, null);
        if (!script.startsWith(Droid.UI) && !script.startsWith(Droid.STAY))
            removeAndDestroy();
        return result;
    }

    private Scriptable createScope(Context context) {
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        importerTopLevel.initStandardObjects(context, false);
        return importerTopLevel;
    }

    private Context createContext() {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        context.setInstructionObserverThreshold(10000);
        return context;
    }

    private void init(Context context, Scriptable scope) {
        ScriptableObject.putProperty(scope, "context", App.getApp());
        ScriptableObject.putProperty(scope, "__engine__", "rhino");
        context.evaluateString(scope, Init.getInitScript(), "<init>", 1, null);
        for (Map.Entry<String, Object> variable : mVariableMap.entrySet()) {
            ScriptableObject.putProperty(scope, variable.getKey(), variable.getValue());
        }
        mThreads.add(Thread.currentThread());

    }


    @Override
    public int stopAll() {
        int n;
        synchronized (mThreads) {
            for (Thread thread : mThreads) {
                thread.interrupt();
            }
            n = mThreads.size();
            mThreads.clear();
        }
        return n;
    }

    @Override
    public void removeAndDestroy() {
        synchronized (mThreads) {
            mThreads.remove(Thread.currentThread());
        }
    }

    public static class InterruptibleContextFactory extends ContextFactory {

        @Override
        protected void observeInstructionCount(Context cx, int instructionCount) {
            if (Thread.currentThread().isInterrupted()) {
                Context.exit();
                throw new ScriptStopException();
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
