package com.stardust.autojs.execution;

import android.util.Log;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptInterruptedException;
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
        execute();
    }

    public Object execute() {
        mScriptEngine = mScriptEngineService.createScriptEngine();
        mScriptRuntime = mScriptEngineService.createScriptRuntime();
        return execute(mScriptRuntime, mScriptEngine);
    }

    private Object execute(ScriptRuntime runtime, ScriptEngine engine) {
        try {
            prepare(runtime, engine);
            return doExecution(engine);
        } catch (Exception e) {
            e.printStackTrace();
            getListener().onException(this, e);
        } finally {
            Log.d(TAG, "Engine destroy");
            engine.destroy();
        }
        return null;
    }

    private void prepare(ScriptRuntime runtime, ScriptEngine engine) {
        if ((getSource().getExecutionMode() & ScriptSource.EXECUTION_MODE_AUTO) != 0) {
            runtime.ensureAccessibilityServiceEnabled();
        }
        engine.put("__runtime__", runtime);
        engine.setTag("__require_path__", getConfig().getRequirePath());
        engine.init();
    }

    private Object doExecution(ScriptEngine engine) {
        engine.setTag("script", getSource());
        getListener().onStart(this);
        Object result = null;
        long delay = getConfig().delay;
        int times = getConfig().loopTimes;
        if (times == 0) {
            times = Integer.MAX_VALUE;
        }
        long interval = getConfig().interval;
        sleep(delay);
        ScriptSource source = getSource();
        for (int i = 0; i < times; i++) {
            result = engine.execute(source);
            sleep(interval);
        }
        getListener().onSuccess(this, result);
        return result;
    }

    private void sleep(long i) {
        if (i <= 0) {
            return;
        }
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
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