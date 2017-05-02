package com.stardust.autojs;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/3.
 */

public interface ScriptExecution {

    JavaScriptEngine getEngine();

    ScriptRuntime getRuntime();

    ScriptSource getSource();

    ScriptExecutionListener getListener();

    ExecutionConfig getConfig();

    abstract class AbstarctScriptExecution implements ScriptExecution {

        protected ScriptExecutionTask mScriptExecutionTask;

        public AbstarctScriptExecution(ScriptExecutionTask task) {
            mScriptExecutionTask = task;
        }

        @Override
        public abstract JavaScriptEngine getEngine();

        @Override
        public abstract ScriptRuntime getRuntime();

        @Override
        public ScriptSource getSource() {
            return mScriptExecutionTask.getSource();
        }

        @Override
        public ScriptExecutionListener getListener() {
            return mScriptExecutionTask.getListener();
        }

        @Override
        public ExecutionConfig getConfig() {
            return mScriptExecutionTask.getConfig();
        }
    }
}
