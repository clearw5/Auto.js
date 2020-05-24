package org.autojs.autojs.timing;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.App;
import org.autojs.autojs.storage.database.IntentTaskDatabase;
import org.autojs.autojs.storage.database.ModelChange;
import org.autojs.autojs.storage.database.TimedTaskDatabase;
import org.autojs.autojs.tool.Observers;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by Stardust on 2017/11/27.
 */
//TODO rx
public class TimedTaskManager {

    private static TimedTaskManager sInstance;
    private Context mContext;
    private TimedTaskDatabase mTimedTaskDatabase;
    private IntentTaskDatabase mIntentTaskDatabase;

    public static TimedTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new TimedTaskManager(GlobalAppContext.get());
        }
        return sInstance;
    }

    @SuppressLint("CheckResult")
    public TimedTaskManager(Context context) {
        mContext = context;
        mTimedTaskDatabase = new TimedTaskDatabase(context);
        mIntentTaskDatabase = new IntentTaskDatabase(context);
    }

    @SuppressLint("CheckResult")
    public void notifyTaskFinished(long id) {
        TimedTask task = getTimedTask(id);
        if (task == null)
            return;
        if (task.isDisposable()) {
            mTimedTaskDatabase.delete(task)
                    .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);
        } else {
            task.setScheduled(false);
            mTimedTaskDatabase.update(task)
                    .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);
        }
    }

    @SuppressLint("CheckResult")
    public void removeTask(TimedTask timedTask) {
        TimedTaskScheduler.cancel(timedTask);
        mTimedTaskDatabase.delete(timedTask)
                .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    public void addTask(TimedTask timedTask) {
        mTimedTaskDatabase.insert(timedTask)
                .subscribe(id -> {
                    timedTask.setId(id);
                    TimedTaskScheduler.scheduleTaskIfNeeded(mContext, timedTask, false);
                }, Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    public void addTask(IntentTask intentTask) {
        mIntentTaskDatabase.insert(intentTask)
                .subscribe(i -> {
                    if (!TextUtils.isEmpty(intentTask.getAction())) {
                        App.Companion.getApp().getDynamicBroadcastReceivers()
                                .register(intentTask);
                    }
                }, Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    public void removeTask(IntentTask intentTask) {
        mIntentTaskDatabase.delete(intentTask)
                .subscribe(i -> {
                    if (!TextUtils.isEmpty(intentTask.getAction())) {
                        App.Companion.getApp().getDynamicBroadcastReceivers()
                                .unregister(intentTask.getAction());
                    }
                }, Throwable::printStackTrace);
    }

    public Flowable<TimedTask> getAllTasks() {
        return mTimedTaskDatabase.queryAllAsFlowable();
    }

    public Flowable<IntentTask> getIntentTaskOfAction(String action) {
        return mIntentTaskDatabase.query("action = ?", action);
    }


    public Observable<ModelChange<TimedTask>> getTimeTaskChanges() {
        return mTimedTaskDatabase.getModelChange();
    }

    @SuppressLint("CheckResult")
    public void notifyTaskScheduled(TimedTask timedTask) {
        timedTask.setScheduled(true);
        mTimedTaskDatabase.update(timedTask)
                .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);

    }

    public List<TimedTask> getAllTasksAsList() {
        return mTimedTaskDatabase.queryAll();
    }

    public TimedTask getTimedTask(long taskId) {
        return mTimedTaskDatabase.queryById(taskId);
    }

    @SuppressLint("CheckResult")
    public void updateTask(TimedTask task) {
        mTimedTaskDatabase.update(task)
                .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);
        TimedTaskScheduler.cancel(task);
        TimedTaskScheduler.scheduleTaskIfNeeded(mContext, task, false);
    }

    @SuppressLint("CheckResult")
    public void updateTaskWithoutReScheduling(TimedTask task) {
        mTimedTaskDatabase.update(task)
                .subscribe(Observers.emptyConsumer(), Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    public void updateTask(IntentTask task) {
        mIntentTaskDatabase.update(task)
                .subscribe(i -> {
                    if (i > 0 && !TextUtils.isEmpty(task.getAction())) {
                        App.Companion.getApp().getDynamicBroadcastReceivers()
                                .register(task);
                    }
                }, Throwable::printStackTrace);
    }

    public long countTasks() {
        return mTimedTaskDatabase.count();
    }

    public List<IntentTask> getAllIntentTasksAsList() {
        return mIntentTaskDatabase.queryAll();
    }

    public Observable<ModelChange<IntentTask>> getIntentTaskChanges() {
        return mIntentTaskDatabase.getModelChange();
    }

    public IntentTask getIntentTask(long intentTaskId) {
        return mIntentTaskDatabase.queryById(intentTaskId);
    }

    public Flowable<IntentTask> getAllIntentTasks() {
        return mIntentTaskDatabase.queryAllAsFlowable();
    }
}
