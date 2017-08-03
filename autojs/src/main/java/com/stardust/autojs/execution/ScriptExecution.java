package com.stardust.autojs.execution;

import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/3.
 */

public interface ScriptExecution {

    ScriptEngine getEngine();

    ScriptSource getSource();

    ScriptExecutionListener getListener();

    ExecutionConfig getConfig();

    abstract class AbstractScriptExecution implements ScriptExecution {

        protected ScriptExecutionTask mScriptExecutionTask;

        public AbstractScriptExecution(ScriptExecutionTask task) {
            mScriptExecutionTask = task;
        }

        @Override
        public abstract ScriptEngine getEngine();

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
