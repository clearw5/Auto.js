package com.stardust.autojs;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;

/**
 * Created by Stardust on 2017/5/1.
 */

public class RunnableScriptExecution extends ScriptExecution.AbstarctScriptExecution implements ScriptExecution, Runnable {

    private JavaScriptEngine mJavaScriptEngine;
    private ScriptRuntime mScriptRuntime;
    private ScriptEngineService mScriptEngineService;

    public RunnableScriptExecution(ScriptEngineService service, ScriptExecutionTask task) {
        super(task);
        mScriptEngineService = service;
    }

    @Override
    public void run() {
        mJavaScriptEngine = mScriptEngineService.createScriptEngine();
        mScriptRuntime = mScriptEngineService.createScriptRuntime();
        mScriptExecutionTask.execute(mScriptRuntime, mJavaScriptEngine);
    }

    @Override
    public JavaScriptEngine getEngine() {
        return mJavaScriptEngine;
    }

    @Override
    public ScriptRuntime getRuntime() {
        return mScriptRuntime;
    }
}