package org.autojs.autojs.timing;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.autojs.autojs.App;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.storage.database.TimedTaskDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskScheduler {

    private static final String LOG_TAG = "TimedTaskScheduler";
    private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);

    private static final String JOB_TAG_CHECK_TASKS = "checkTasks";


    @SuppressLint("CheckResult")
    public static void checkTasks(Context context) {
        Log.d(LOG_TAG, "check tasks");
        TimedTaskManager.getInstance().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTaskIfNeeded(context, timedTask));
    }

    public static void scheduleTaskIfNeeded(Context context, TimedTask timedTask) {
        long millis = timedTask.getNextTime();
        if (timedTask.isScheduled() || millis - System.currentTimeMillis() > ONE_HOUR) {
            return;
        }
        scheduleTask(context, timedTask, millis);
        TimedTaskManager.getInstance()
                .notifyTaskScheduled(timedTask);
    }

    private synchronized static void scheduleTask(Context context, TimedTask timedTask, long millis) {
        if (timedTask.isScheduled()) {
            return;
        }
        long timeWindow = millis - System.currentTimeMillis();
        Log.d(LOG_TAG, "schedule task: task = " + timedTask + ", millis = " + millis + ", timeWindow = " + timeWindow);
        timedTask.setScheduled(true);
        TimedTaskManager.getInstance().updateTaskWithoutReScheduling(timedTask);
        if (timeWindow <= 0) {
            runTask(context, timedTask);
            return;
        }
        new JobRequest.Builder(String.valueOf(timedTask.getId()))
                .setExact(timeWindow)
                .build()
                .schedule();
    }

    public static void cancel(Context context, TimedTask timedTask) {
        Log.d(LOG_TAG, "cancel task: task = " + timedTask);
        JobManager.instance().cancelAllForTag(String.valueOf(timedTask.getId()));
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
        checkTasks(context);
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
            checkTasks(mContext);
            return Result.SUCCESS;
        }
    }


}
