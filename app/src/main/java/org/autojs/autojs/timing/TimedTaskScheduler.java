package org.autojs.autojs.timing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.autojs.autojs.external.ScriptIntents;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskScheduler {

    private static final String LOG_TAG = "TimedTaskScheduler";
    private static final long SCHEDULE_TASK_MIN_TIME = TimeUnit.DAYS.toMillis(2);

    private static final String JOB_TAG_CHECK_TASKS = "checkTasks";


    @SuppressLint("CheckResult")
    public static void checkTasks(Context context, boolean force) {
        Log.d(LOG_TAG, "check tasks: force = " + force);
        TimedTaskManager.getInstance().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTaskIfNeeded(context, timedTask, force));
    }

    public static void scheduleTaskIfNeeded(Context context, TimedTask timedTask, boolean force) {
        long millis = timedTask.getNextTime();
        if ((!force && timedTask.isScheduled()) || millis - System.currentTimeMillis() > SCHEDULE_TASK_MIN_TIME) {
            return;
        }
        scheduleTask(context, timedTask, millis, force);
        TimedTaskManager.getInstance()
                .notifyTaskScheduled(timedTask);
    }

    private synchronized static void scheduleTask(Context context, TimedTask timedTask, long millis, boolean force) {
        if (!force && timedTask.isScheduled()) {
            return;
        }
        long timeWindow = millis - System.currentTimeMillis();
        timedTask.setScheduled(true);
        TimedTaskManager.getInstance().updateTaskWithoutReScheduling(timedTask);
        if (timeWindow <= 0) {
            runTask(context, timedTask);
            return;
        }
        cancel(timedTask);
        Log.d(LOG_TAG, "schedule task: task = " + timedTask + ", millis = " + millis + ", timeWindow = " + timeWindow);
        new JobRequest.Builder(String.valueOf(timedTask.getId()))
                .setExact(timeWindow)
                .build()
                .schedule();
    }

    public static void cancel(TimedTask timedTask) {
        int cancelCount = JobManager.instance().cancelAllForTag(String.valueOf(timedTask.getId()));
        Log.d(LOG_TAG, "cancel task: task = " + timedTask + ", cancel = " + cancelCount);
    }

    public static void init(@NotNull Context context) {
        JobManager.create(context).addJobCreator(tag -> {
            if (tag.equals(JOB_TAG_CHECK_TASKS)) {
                return new CheckTasksJob(context);
            } else {
                return new TimedTaskJob(context);
            }
        });
        new JobRequest.Builder(JOB_TAG_CHECK_TASKS)
                .setPeriodic(TimeUnit.MINUTES.toMillis(20))
                .build()
                .scheduleAsync();
        checkTasks(context, true);
    }

    private static void runTask(Context context, TimedTask task) {
        Log.d(LOG_TAG, "run task: task = " + task);
        Intent intent = task.createIntent();
        ScriptIntents.handleIntent(context, intent);
        TimedTaskManager.getInstance().notifyTaskFinished(task.getId());
    }

    private static class TimedTaskJob extends Job {

        private final Context mContext;

        TimedTaskJob(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        protected Result onRunJob(@NonNull Params params) {
            long id = Long.parseLong(params.getTag());
            TimedTask task = TimedTaskManager.getInstance().getTimedTask(id);
            Log.d(LOG_TAG, "onRunJob: id = " + id + ", task = " + task);
            if (task == null) {
                return Result.FAILURE;
            }
            runTask(mContext, task);
            return Result.SUCCESS;
        }
    }

    private static class CheckTasksJob extends Job {
        private final Context mContext;

        CheckTasksJob(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        protected Result onRunJob(@NonNull Params params) {
            checkTasks(mContext, false);
            return Result.SUCCESS;
        }
    }


}
