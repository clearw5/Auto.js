package org.autojs.autojs.timing.work;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.timing.TimedTaskScheduler;

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

public class WorkManagerProvider extends TimedTaskScheduler implements WorkProvider {

    private static final String LOG_TAG = "WorkManagerProvider";

    private Context context;

    @SuppressLint("StaticFieldLeak")
    private volatile static WorkManagerProvider instance = null;

    public static WorkProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (WorkManagerProvider.class) {
                if (instance == null) {
                    instance = new WorkManagerProvider(context);
                }
            }
        }
        return instance;
    }

    private WorkManagerProvider(Context context) {
        this.context = context;
    }

    @Override
    public void enqueueWork(TimedTask timedTask, long timeWindow) {
        autoJsLog( "enqueue task:" + timedTask.toString());
        WorkManager.getInstance(context).enqueueUniqueWork(String.valueOf(timedTask.getId()),
                ExistingWorkPolicy.KEEP,
                new OneTimeWorkRequest
                        .Builder(TimedTaskWorker.class)
                        .addTag(String.valueOf(timedTask.getId()))
                        .setInputData(new Data.Builder().putLong("taskId", timedTask.getId()).build())
                        .setInitialDelay(timeWindow, TimeUnit.MILLISECONDS)
                        .build());

    }

    @Override
    public void enqueuePeriodicWork(int delay) {
        autoJsLog( "enqueueUniquePeriodicWork");
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest
                .Builder(CheckTaskWorker.class, 20, TimeUnit.MINUTES);
        if (delay > 0) {
            builder.setInitialDelay(delay, TimeUnit.MINUTES);
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(JOB_TAG_CHECK_TASKS, ExistingPeriodicWorkPolicy.REPLACE, builder.build());
    }

    @Override
    public void cancel(TimedTask timedTask) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(timedTask.getId())).getResult();
        autoJsLog("cancel task: task = " + timedTask);
    }

    @Override
    @SuppressLint("CheckResult")
    public void cancelAllWorks() {
        autoJsLog("cancel all tasks");
        WorkManager.getInstance(context).cancelAllWork().getResult();
        TimedTaskManager.getInstance()
                .getAllTasks()
                .filter(TimedTask::isScheduled)
                .forEach(timedTask -> {
                    timedTask.setScheduled(false);
                    timedTask.setExecuted(false);
                    TimedTaskManager.getInstance().updateTaskWithoutReScheduling(timedTask);
                });
    }

    @Override
    public boolean isCheckWorkFine() {
        boolean workFine = false;
        List<WorkInfo> workInfoList = null;
        try {
            workInfoList = WorkManager.getInstance(context).getWorkInfosForUniqueWork(JOB_TAG_CHECK_TASKS).get();
        } catch (Exception e){
            Log.d(LOG_TAG, "获取校验线程失败");
        }

        if (workInfoList != null && workInfoList.size() > 0) {
            for (WorkInfo workInfo : workInfoList) {
                if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                    workFine = true;
                    break;
                }
            }
        }
        return workFine;
    }

    private void autoJsLog(String content) {
        Log.d(LOG_TAG, content);
        AutoJs.getInstance().debugInfo(content);
    }

    public class TimedTaskWorker extends Worker {

        private static final String DONE_TIME = "DONE_TIME";

        public TimedTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            if (isStopped()) {
                return Result.success();
            }
            long id = this.getInputData().getLong("taskId", -1);
            if (id > -1) {
                TimedTask task = TimedTaskManager.getInstance().getTimedTask(id);
                autoJsLog("onRunJob: id = " + id + ", task = " + task + ", currentMillis=" + System.currentTimeMillis());
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
            if (isStopped()) {
                return Result.success();
            }
            Log.d(LOG_TAG, "定期检测任务运行中");
            AutoJs.getInstance().debugInfo("定期检测任务运行中");
            TimedTaskScheduler.getWorkProvider(GlobalAppContext.get()).checkTasks(getApplicationContext(), false);
            return Result.success();
        }
    }


}
