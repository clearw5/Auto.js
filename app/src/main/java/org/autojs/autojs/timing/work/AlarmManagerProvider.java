package org.autojs.autojs.timing.work;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmManagerProvider extends BroadcastReceiver implements WorkProvider {

    private Context context;

    private static final String ACTION_CHECK_TASK = "org.autojs.autojs.action.check_task";

    private static final String LOG_TAG = "AlarmManagerProvider";
    private static final int REQUEST_CODE_CHECK_TASK_REPEATEDLY = 4000;
    private static final long INTERVAL = TimeUnit.MINUTES.toMillis(15);
    private static final long MIN_INTERVAL_GAP = TimeUnit.MINUTES.toMillis(5);

    private static final long SCHEDULE_TASK_MIN_TIME = TimeUnit.DAYS.toMillis(2);

    private volatile static AlarmManagerProvider instance = null;

    private static PendingIntent sCheckTasksPendingIntent;

    public AlarmManagerProvider() {
        this.context = GlobalAppContext.get();
    }

    public AlarmManagerProvider(Context context) {
        this.context = context;
    }

    public static WorkProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (AndroidJobProvider.class) {
                if (instance == null) {
                    instance = new AlarmManagerProvider(context);
                }
            }
        }
        return instance;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        autoJsLog( "onReceiveRtcWakeUp");
        checkTasks(context, false);
        setupNextRtcWakeup(context, System.currentTimeMillis() + INTERVAL);
    }


    @Override
    public void enqueueWork(TimedTask timedTask, long timeWindow) {
        autoJsLog( "enqueue task:" + timedTask.toString());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent op = timedTask.createPendingIntent(context);
        setExactCompat(alarmManager, op, System.currentTimeMillis() + timeWindow);
    }

    @Override
    public void enqueuePeriodicWork(int delay) {
        autoJsLog( "checkTasksRepeatedlyIfNeeded");
        checkTasksRepeatedlyIfNeeded(context);
    }

    @Override
    public void cancel(TimedTask timedTask) {
        autoJsLog( "cancel task:" + timedTask);
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(timedTask.createPendingIntent(context));
    }

    @Override
    @SuppressLint("CheckResult")
    public void cancelAllWorks() {
        autoJsLog( "cancel all tasks");
        stopRtcRepeating(context);
        TimedTaskManager.getInstance()
                .getAllTasks()
                .filter(TimedTask::isScheduled)
                .forEach(timedTask -> {
                    cancel(timedTask);
                    timedTask.setScheduled(false);
                    timedTask.setExecuted(false);
                    TimedTaskManager.getInstance().updateTaskWithoutReScheduling(timedTask);
                });
    }

    @Override
    public boolean isCheckWorkFine() {
        return true;
    }

    @SuppressLint("CheckResult")
    public void checkTasks(Context context, boolean force) {
        autoJsLog( "check tasks: force = " + force);
        TimedTaskManager.getInstance().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTaskIfNeeded(context, timedTask, force));
    }

    public void scheduleTaskIfNeeded(Context context, TimedTask timedTask, boolean force) {
        long millis = timedTask.getNextTime();
        if (millis <= System.currentTimeMillis()) {
            autoJsLog( "task out date run:" + timedTask);
            runTask(context, timedTask);
            return;
        }
        if (!force && timedTask.isScheduled() || millis - System.currentTimeMillis() > SCHEDULE_TASK_MIN_TIME) {
            return;
        }
        scheduleTask(context, timedTask, millis, force);
        TimedTaskManager.getInstance().notifyTaskScheduled(timedTask);
    }

    public void scheduleTask(Context context, TimedTask timedTask, long millis, boolean force) {
        if (!force && timedTask.isScheduled()) {
            return;
        }
        autoJsLog( "schedule task:" + timedTask);
        if (force) {
            cancel(timedTask);
        }
        enqueueWork(timedTask, millis - System.currentTimeMillis());
    }


    public void runTask(Context context, TimedTask task) {
        Log.d(LOG_TAG, "run task: task = " + task);
        Intent intent = task.createIntent();
        ScriptIntents.handleIntent(context, intent);
        TimedTaskManager.getInstance().notifyTaskFinished(task.getId());
        // 如果队列中有任务正在等待，直接取消
        cancel(task);
    }

    private void setExactCompat(AlarmManager alarmManager, PendingIntent op, long millis) {
        int type = AlarmManager.RTC_WAKEUP;
        long gapMillis = millis - System.currentTimeMillis();
        if (gapMillis <= MIN_INTERVAL_GAP) {
            long oldMillis = millis;
            // 目标时间修改为真实时间
            millis = SystemClock.elapsedRealtime() + gapMillis;
            type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            autoJsLog( "less then 5 minutes, millis changed from " + oldMillis + " to " + millis);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, millis, op);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(millis, null), op);
        } else {
            alarmManager.setExact(type, millis, op);
        }
    }


    public void checkTasksRepeatedlyIfNeeded(Context context) {
        autoJsLog( "checkTasksRepeatedlyIfNeeded count:" + TimedTaskManager.getInstance().countTasks());
        if (TimedTaskManager.getInstance().countTasks() > 0) {
            // 设置周期性时间6分钟
            setupNextRtcWakeup(context, System.currentTimeMillis() +  INTERVAL);
        }
    }

    private void setupNextRtcWakeup(Context context, long millis) {
        autoJsLog("setupNextRtcWakeup: at " + millis);
        if (millis <= 0) {
            throw new IllegalArgumentException("millis <= 0: " + millis);
        }
        AlarmManager alarmManager = getAlarmManager(context);
        setExactCompat(alarmManager, createTaskCheckPendingIntent(context), millis);
    }


    public void stopRtcRepeating(Context context) {
        autoJsLog("stopRtcRepeating");
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(createTaskCheckPendingIntent(context));
    }

    private AlarmManager getAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        return alarmManager;
    }

    private PendingIntent createTaskCheckPendingIntent(Context context) {
        if (sCheckTasksPendingIntent == null) {
            int flags = PendingIntent.FLAG_UPDATE_CURRENT |
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
            sCheckTasksPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_CHECK_TASK_REPEATEDLY,
                    new Intent(ACTION_CHECK_TASK)
                            .setComponent(new ComponentName(BuildConfig.APPLICATION_ID,
                                    "org.autojs.autojs.timing.work.AlarmManagerProvider")),
                    flags);
        }
        return sCheckTasksPendingIntent;
    }

    private void autoJsLog(String content) {
        Log.d(LOG_TAG, content);
        AutoJs.getInstance().debugInfo(content);
    }
}
