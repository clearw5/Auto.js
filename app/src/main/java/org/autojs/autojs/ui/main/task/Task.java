package org.autojs.autojs.ui.main.task;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;

import org.joda.time.format.DateTimeFormat;

/**
 * Created by Stardust on 2017/11/28.
 */

public abstract class Task {

    public abstract String getName();

    public abstract String getDesc();

    public abstract void cancel();

    public abstract String getEngineName();

    public static class PendingTask extends Task {

        private TimedTask mTimedTask;


        public PendingTask(TimedTask timedTask) {
            mTimedTask = timedTask;
        }

        public TimedTask getTimedTask() {
            return mTimedTask;
        }

        @Override
        public String getName() {
            return PFiles.getSimplifiedPath(mTimedTask.getScriptPath());
        }

        @Override
        public String getDesc() {
            long nextTime = mTimedTask.getNextTime();
            return GlobalAppContext.getString(R.string.text_next_run_time) + ": " +
                    DateTimeFormat.shortDateTime().print(nextTime);
        }

        @Override
        public void cancel() {
            TimedTaskManager.getInstance().removeTask(mTimedTask);
        }

        @Override
        public String getEngineName() {
            if (mTimedTask.getScriptPath().endsWith(".js")) {
                return JavaScriptSource.ENGINE;
            } else {
                return AutoFileSource.ENGINE;
            }
        }

        public void setTimedTask(TimedTask timedTask) {
            mTimedTask = timedTask;
        }
    }

    public static class RunningTask extends Task {
        private final ScriptExecution mScriptExecution;

        public RunningTask(ScriptExecution scriptExecution) {
            mScriptExecution = scriptExecution;
        }

        public ScriptExecution getScriptExecution() {
            return mScriptExecution;
        }

        @Override
        public String getName() {
            return mScriptExecution.getSource().getName();
        }

        @Override
        public String getDesc() {
            return mScriptExecution.getSource().toString();
        }

        @Override
        public void cancel() {
            ScriptEngine engine = mScriptExecution.getEngine();
            if (engine != null) {
                engine.forceStop();
            }
        }

        @Override
        public String getEngineName() {
            return mScriptExecution.getSource().getEngineName();
        }
    }
}
