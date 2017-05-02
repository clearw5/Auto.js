package com.stardust.autojs;

import android.util.Log;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptExecutionTask implements Serializable {

    private static final String TAG = "ScriptExecutionTask";

    private ScriptSource mScriptSource;
    private ScriptExecutionListener mExecutionListener;
    private ExecutionConfig mExecutionConfig;

    public ScriptExecutionTask(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
        mScriptSource = source;
        mExecutionListener = listener;
        mExecutionConfig = config;
    }

    public ScriptSource getSource() {
        return mScriptSource;
    }

    public ScriptExecutionListener getListener() {
        return mExecutionListener;
    }

    public ExecutionConfig getConfig() {
        return mExecutionConfig;
    }



    public void execute(ScriptRuntime runtime, JavaScriptEngine engine) {
        try {
            prepare(runtime, engine);
            doExecution(engine);
        } catch (Exception e) {
            e.printStackTrace();
            mExecutionListener.onException(engine, mScriptSource, e);
        } finally {
            Log.d(TAG, "Engine destroy");
            engine.destroy();
        }
    }

    private void prepare(ScriptRuntime runtime, JavaScriptEngine engine) {
        if ((mScriptSource.getExecutionMode() & ScriptSource.EXECUTION_MODE_AUTO) != 0) {
            runtime.ensureAccessibilityServiceEnabled();
        }
        engine.put("__runtime__", runtime);
        engine.init();
    }

    private void doExecution(JavaScriptEngine engine) {
        engine.setTag("script", mScriptSource);
        mExecutionListener.onStart(engine, mScriptSource);
        mExecutionListener.onSuccess(engine, mScriptSource, engine.execute(mScriptSource));
    }

    public ScriptExecutionListener getExecutionListenerOrDefault(ScriptExecutionListener defaultListener) {
        if (mExecutionListener == null)
            return defaultListener;
        return mExecutionListener;
    }

    public void setDefaultListenerIfNeeded(ScriptExecutionListener defaultScriptExecutionListener) {
        if (mExecutionListener == null) {
            mExecutionListener = defaultScriptExecutionListener;
        }
    }
}
