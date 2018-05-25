package org.autojs.autojs.ui.main.task;

import android.content.Context;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.stardust.autojs.engine.ScriptEngine;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;

import java.util.ArrayList;
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
            List<TimedTask> timedTasks = TimedTaskManager.getInstance().getAllTasksAsList();
            mTasks.clear();
            for (TimedTask timedTask : timedTasks) {
                mTasks.add(new Task.PendingTask(timedTask));
            }
        }

        public int addTask(TimedTask timedTask) {
            int pos = mTasks.size();
            mTasks.add(new Task.PendingTask(timedTask));
            return pos;
        }

        public int removeTask(TimedTask data) {
            int i = indexOf(data);
            if (i >= 0)
                mTasks.remove(i);
            return i;
        }

        private int indexOf(TimedTask data) {
            for (int i = 0; i < mTasks.size(); i++) {
                if (((Task.PendingTask) mTasks.get(i)).getTimedTask().equals(data)) {
                    return i;
                }
            }
            return -1;
        }

        public int updateTask(TimedTask task) {
            int i = indexOf(task);
            if (i >= 0)
                ((Task.PendingTask) mTasks.get(i)).setTimedTask(task);
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
            Set<ScriptEngine> scriptEngines = AutoJs.getInstance().getScriptEngineService().getEngines();
            mTasks.clear();
            for (ScriptEngine engine : scriptEngines) {
                mTasks.add(new Task.RunningTask(engine));
            }
        }

        public int addTask(ScriptEngine engine) {
            int pos = mTasks.size();
            mTasks.add(new Task.RunningTask(engine));
            return pos;
        }

        public int removeTask(ScriptEngine engine) {
            int i = indexOf(engine);
            if (i >= 0) {
                mTasks.remove(i);
            }
            return i;
        }

        public int indexOf(ScriptEngine engine) {
            for (int i = 0; i < mTasks.size(); i++) {
                if (((Task.RunningTask) mTasks.get(i)).getScriptEngine().equals(engine)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
