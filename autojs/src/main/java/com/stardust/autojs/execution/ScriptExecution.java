package com.stardust.autojs.execution;

import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/4/3.
 */

public interface ScriptExecution {

    int NO_ID = -1;

    ScriptEngine getEngine();

    ScriptSource getSource();

    ScriptExecutionListener getListener();

    ExecutionConfig getConfig();

    int getId();

    abstract class AbstractScriptExecution implements ScriptExecution {

        private static AtomicInteger sMaxId = new AtomicInteger(0);

        protected ScriptExecutionTask mScriptExecutionTask;
        protected int mId;

        public AbstractScriptExecution(ScriptExecutionTask task) {
            mScriptExecutionTask = task;
            mId = sMaxId.getAndIncrement();
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

        @Override
        public int getId() {
            return mId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AbstractScriptExecution that = (AbstractScriptExecution) o;
            return mId == that.mId;
        }

        @Override
        public int hashCode() {
            return mId;
        }
    }
}
