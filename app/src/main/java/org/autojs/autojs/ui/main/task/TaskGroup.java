package org.autojs.autojs.ui.main.task;

import android.content.Context;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;

import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.timing.IntentTask;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Stardust on 2017/11/28.
 */

public abstract class TaskGroup implements Parent<Task> {

    protected List<Task> mTasks = new ArrayList<>();
    private String mTitle;

    protected TaskGroup(String title) {
        mTitle = title;
    }


    @Override
    public List<Task> getChildList() {
        return mTasks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }

    public String getTitle() {
        return mTitle;
    }

    public abstract void refresh();

    public static class PendingTaskGroup extends TaskGroup {

        public PendingTaskGroup(Context context) {
            super(context.getString(R.string.text_timed_task));
            refresh();
        }

        @Override
        public void refresh() {
            mTasks.clear();
            for (TimedTask timedTask : TimedTaskManager.getInstance().getAllTasksAsList()) {
                mTasks.add(new Task.PendingTask(timedTask));
            }
            for (IntentTask intentTask : TimedTaskManager.getInstance().getAllIntentTasksAsList()) {
                mTasks.add(new Task.PendingTask(intentTask));
            }
        }

        public int addTask(Object task) {
            int pos = mTasks.size();
            if (task instanceof TimedTask) {
                mTasks.add(new Task.PendingTask((TimedTask) task));
            } else if (task instanceof IntentTask) {
                mTasks.add(new Task.PendingTask((IntentTask) task));
            } else {
                throw new IllegalArgumentException("task = " + task);
            }
            return pos;
        }

        public int removeTask(Object data) {
            int i = indexOf(data);
            if (i >= 0)
                mTasks.remove(i);
            return i;
        }

        private int indexOf(Object data) {
            for (int i = 0; i < mTasks.size(); i++) {
                Task.PendingTask task = (Task.PendingTask) mTasks.get(i);
                if (task.taskEquals(data)) {
                    return i;
                }
            }
            return -1;
        }


        public int updateTask(Object task) {
            int i = indexOf(task);
            if (i >= 0) {
                if (task instanceof TimedTask) {
                    ((Task.PendingTask) mTasks.get(i)).setTimedTask((TimedTask) task);
                } else if (task instanceof IntentTask) {
                    ((Task.PendingTask) mTasks.get(i)).setIntentTask((IntentTask) task);
                } else {
                    throw new IllegalArgumentException("task = " + task);
                }
            }
            return i;
        }
    }

    public static class RunningTaskGroup extends TaskGroup {

        public RunningTaskGroup(Context context) {
            super(context.getString(R.string.text_running_task));
            refresh();
        }

        @Override
        public void refresh() {
            Collection<ScriptExecution> executions = AutoJs.getInstance().getScriptEngineService().getScriptExecutions();
            mTasks.clear();
            for (ScriptExecution execution : executions) {
                mTasks.add(new Task.RunningTask(execution));
            }
        }

        public int addTask(ScriptExecution engine) {
            int pos = mTasks.size();
            mTasks.add(new Task.RunningTask(engine));
            return pos;
        }

        public int removeTask(ScriptExecution engine) {
            int i = indexOf(engine);
            if (i >= 0) {
                mTasks.remove(i);
            }
            return i;
        }

        public int indexOf(ScriptExecution engine) {
            for (int i = 0; i < mTasks.size(); i++) {
                if (((Task.RunningTask) mTasks.get(i)).getScriptExecution().equals(engine)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
