package com.stardust.autojs.execution;

import android.util.Log;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/5/1.
 */

public class RunnableScriptExecution extends ScriptExecution.AbstractScriptExecution implements ScriptExecution, Runnable {

    private static final String TAG = "RunnableScriptExecution";
    private ScriptEngine mScriptEngine;
    private ScriptRuntime mScriptRuntime;
    private ScriptEngineService mScriptEngineService;

    public RunnableScriptExecution(ScriptEngineService service, ScriptExecutionTask task) {
        super(task);
        mScriptEngineService = service;
    }

    @Override
    public void run() {
        mScriptEngine = mScriptEngineService.createScriptEngine();
        mScriptRuntime = mScriptEngineService.createScriptRuntime();
        execute(mScriptRuntime, mScriptEngine);
    }

    private void execute(ScriptRuntime runtime, ScriptEngine engine) {
        try {
            prepare(runtime, engine);
            doExecution(engine);
        } catch (Exception e) {
            e.printStackTrace();
            getListener().onException(this, e);
        } finally {
            Log.d(TAG, "Engine destroy");
            engine.destroy();
        }
    }

    private void prepare(ScriptRuntime runtime, ScriptEngine engine) {
        if ((getSource().getExecutionMode() & ScriptSource.EXECUTION_MODE_AUTO) != 0) {
            runtime.ensureAccessibilityServiceEnabled();
        }
        engine.put("__runtime__", runtime);
        engine.init();
    }

    private void doExecution(ScriptEngine engine) {
        engine.setTag("script", getSource());
        getListener().onStart(this);
        getListener().onSuccess(this, engine.execute(getSource()));
    }

    @Override
    public ScriptEngine getEngine() {
        return mScriptEngine;
    }

    @Override
    public ScriptRuntime getRuntime() {
        return mScriptRuntime;
    }
}