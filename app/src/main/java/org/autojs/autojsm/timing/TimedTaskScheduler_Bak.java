package org.autojs.autojsm.timing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.autojs.autojsm.external.ScriptIntents;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskScheduler_Bak {

    private static final String LOG_TAG = "TimedTaskScheduler";
    private static final long SCHEDULE_TASK_MIN_TIME = TimeUnit.DAYS.toMillis(2);

    protected static final String JOB_TAG_CHECK_TASKS = "checkTasks";


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

        Log.d(LOG_TAG, "schedule task: task = " + timedTask + ", millis = " + millis + ", timeWindow = " + timeWindow);

        WorkManager.getInstance(context).enqueueUniqueWork(String.valueOf(timedTask.getId()),
                ExistingWorkPolicy.KEEP,
                new OneTimeWorkRequest
                        .Builder(TimedTaskWorker.class)
                        .addTag(String.valueOf(timedTask.getId()))
                        .setInputData(new Data.Builder().putLong("taskId", timedTask.getId()).build())
                        .setInitialDelay(timeWindow, TimeUnit.MILLISECONDS)
                        .build()
        );
    }

    public static void cancel(TimedTask timedTask, Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(timedTask.getId()));
        Log.d(LOG_TAG, "cancel task: task = " + timedTask);
    }

    public static void init(@NotNull Context context) {
        createCheckWorker(context, 20);
        checkTasks(context, true);
    }

    private static void createCheckWorker(Context context, int delay) {
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest
                .Builder(CheckTaskWorker.class, 20, TimeUnit.MINUTES);
        if (delay > 0) {
            builder.setInitialDelay(delay, TimeUnit.MINUTES);
        }

        Log.d(LOG_TAG, "创建定期检测任务");
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(JOB_TAG_CHECK_TASKS, ExistingPeriodicWorkPolicy.REPLACE, builder.build());
    }

    protected static void runTask(Context context, TimedTask task) {
        Log.d(LOG_TAG, "run task: task = " + task);
        Intent intent = task.createIntent();
        ScriptIntents.handleIntent(context, intent);
        TimedTaskManager.getInstance().notifyTaskFinished(task.getId());
        ensureCheckTaskWorks(context);
    }

    public static void ensureCheckTaskWorks(Context context) {
        try {
            List<WorkInfo> workInfoList = WorkManager.getInstance(context).getWorkInfosForUniqueWork(JOB_TAG_CHECK_TASKS).get();
            boolean workFine = false;
            if (workInfoList != null && workInfoList.size() > 0) {
                for (WorkInfo workInfo : workInfoList) {
                    if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                        workFine = true;
                    }
                }
            }
            // 校验是否有超时未执行的
            boolean anyLost = TimedTaskManager.getInstance().getAllTasks().any(task -> task.getMillis() < System.currentTimeMillis()).blockingGet();
            if (!workFine || anyLost) {
                createCheckWorker(context, 0);
                checkTasks(context, true);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "获取定时校验任务失败");
        }
    }

    public static class TimedTaskWorker extends Worker {

        private static final String DONE_TIME = "DONE_TIME";

        public TimedTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            long id = this.getInputData().getLong("taskId", -1);
            if (id > -1) {
                TimedTask task = TimedTaskManager.getInstance().getTimedTask(id);
                Log.d(LOG_TAG, "onRunJob: id = " + id + ", task = " + task + ", currentMillis=" + System.currentTimeMillis());
                if (task == null) {
                    return Result.failure();
                }
                runTask(getApplicationContext(), task);
                return Result.success(
                        new Data.Builder()
                                .putString(DONE_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()))
                                .build()
                );
            }
            return Result.failure();
        }
    }

    public static class CheckTaskWorker extends Worker {

        public CheckTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            checkTasks(getApplicationContext(), true);
            return Result.success();
        }
    }


}
