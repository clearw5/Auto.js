package org.autojs.autojs.timing.work;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.timing.TimedTaskScheduler;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class AndroidJobProvider extends TimedTaskScheduler implements WorkProvider {

    private static final String LOG_TAG = "AndroidJobProvider";

    private volatile static AndroidJobProvider instance = null;

    public static WorkProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (AndroidJobProvider.class) {
                if (instance == null) {
                    instance = new AndroidJobProvider(context);
                }
            }
        }
        return instance;
    }

    private AndroidJobProvider(Context context) {
        JobManager.create(context).addJobCreator(tag -> {
            if (tag.equals(JOB_TAG_CHECK_TASKS)) {
                return new CheckTasksJob(context);
            } else {
                return new TimedTaskJob(context);
            }
        });
    }

    @Override
    public void enqueueWork(TimedTask timedTask, long timeWindow) {
        new JobRequest.Builder(String.valueOf(timedTask.getId()))
                .setExact(timeWindow)
                .build()
                .schedule();
    }

    @Override
    public void enqueuePeriodicWork(int delay) {
        new JobRequest.Builder(JOB_TAG_CHECK_TASKS)
                .setPeriodic(TimeUnit.MINUTES.toMillis(20))
                .build()
                .scheduleAsync();
    }

    @Override
    public void cancel(TimedTask timedTask) {
        int cancelCount = JobManager.instance().cancelAllForTag(String.valueOf(timedTask.getId()));
        Log.d(LOG_TAG, "cancel task: task = " + timedTask + ", cancel = " + cancelCount);
    }

    @Override
    @SuppressLint("CheckResult")
    public void cancelAllWorks() {
        JobManager.instance().cancelAll();
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
        Set<Job> jobSet = JobManager.instance().getAllJobsForTag(JOB_TAG_CHECK_TASKS);
        if (jobSet.isEmpty()) {
            return false;
        }
        boolean workFine = false;
        for (Job job : jobSet) {
            if (!job.isFinished()) {
                workFine = true;
                break;
            }
        }
        return workFine;
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
            TimedTaskScheduler.getWorkProvider(GlobalAppContext.get()).checkTasks(mContext, false);
            return Result.SUCCESS;
        }
    }
}
