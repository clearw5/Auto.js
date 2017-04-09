package com.stardust.autojs;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptExecutionTask implements Serializable {

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
            if ((mScriptSource.getExecutionMode() & ScriptSource.EXECUTION_MODE_AUTO) != 0) {
                runtime.ensureAccessibilityServiceEnabled();
            }
            mExecutionListener.onStart(engine, mScriptSource);
            engine.setTag("script", mScriptSource);
            mExecutionListener.onSuccess(engine, mScriptSource, engine.execute(mScriptSource));
        } catch (Exception e) {
            mExecutionListener.onException(engine, mScriptSource, e);
        } finally {
            engine.destroy();
        }
    }

    public ScriptExecutionListener getExecutionListenerOrDefault(ScriptExecutionListener defaultListener) {
        if (mExecutionListener == null)
            return defaultListener;
        return mExecutionListener;
    }
}
