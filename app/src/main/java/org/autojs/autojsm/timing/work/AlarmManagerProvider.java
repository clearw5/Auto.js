package org.autojs.autojsm.timing.work;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.autojs.autojsm.timing.TimedTask;
import org.autojs.autojsm.timing.TimedTaskManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmManagerProvider extends BroadcastReceiver implements WorkProvider {

    private Context context;

    private static final String ACTION_CHECK_TASK = "com.stardust.autojs.action.check_task";

    private static final String LOG_TAG = "AlarmManagerProvider";
    private static final int REQUEST_CODE_CHECK_TASK_REPEATEDLY = 4000;
    private static final long INTERVAL = TimeUnit.MINUTES.toMillis(1);

    private static final long SCHEDULE_TASK_MIN_TIME = TimeUnit.DAYS.toMillis(2);

    private volatile static AlarmManagerProvider instance = null;

    private static PendingIntent sCheckTasksPendingIntent;

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
        Log.d(LOG_TAG, "onReceiveRtcWakeUp");
        checkTasks(context, false);
        setupNextRtcWakeup(context, System.currentTimeMillis() + INTERVAL);
    }


    @Override
    public void enqueueWork(TimedTask timedTask, long timeWindow) {
        Log.d(LOG_TAG, "enqueue task:" + timedTask.toString());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent op = timedTask.createPendingIntent(context);
        setExactCompat(alarmManager, op, System.currentTimeMillis() + timeWindow);
    }

    @Override
    public void enqueuePeriodicWork(int delay) {
        Log.d(LOG_TAG, "checkTasksRepeatedlyIfNeeded");
        checkTasksRepeatedlyIfNeeded(context);
    }

    @Override
    public void cancel(TimedTask timedTask) {
        Log.d(LOG_TAG, "cancel task:" + timedTask);
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(timedTask.createPendingIntent(context));
    }

    @Override
    @SuppressLint("CheckResult")
    public void cancelAllWorks() {
        Log.d(LOG_TAG, "cancel all tasks");
        stopRtcRepeating(context);
        TimedTaskManager.getInstance()
                .getAllTasks()
                .filter(TimedTask::isScheduled)
                .forEach(this::cancel);
    }

    @Override
    public boolean isCheckWorkFine() {
        return true;
    }

    @SuppressLint("CheckResult")
    public void checkTasks(Context context, boolean force) {
        Log.d(LOG_TAG, "check tasks: force = " + force);
        TimedTaskManager.getInstance().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTaskIfNeeded(context, timedTask, force));
    }

    public void scheduleTaskIfNeeded(Context context, TimedTask timedTask, boolean force) {
        long millis = timedTask.getNextTime();
        if (!force && timedTask.isScheduled() || millis - System.currentTimeMillis() > SCHEDULE_TASK_MIN_TIME) {
            return;
        }
        scheduleTask(context, timedTask, millis);
        TimedTaskManager.getInstance().notifyTaskScheduled(timedTask);
    }

    private void scheduleTask(Context context, TimedTask timedTask, long millis) {
        Log.d(LOG_TAG, "schedule task:" + timedTask);
        if (millis <= System.currentTimeMillis()) {
            Log.d(LOG_TAG, "task out date run:" + timedTask);
            context.sendBroadcast(timedTask.createIntent());
            return;
        }
        enqueueWork(timedTask, millis);
    }


    private void setExactCompat(AlarmManager alarmManager, PendingIntent op, long millis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, op);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(millis, null), op);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, op);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, op);
        }
    }


    public void checkTasksRepeatedlyIfNeeded(Context context) {
        if (TimedTaskManager.getInstance().countTasks() > 0) {
            setupNextRtcWakeup(context, System.currentTimeMillis() + 5000);
        }
    }

    private void setupNextRtcWakeup(Context context, long millis) {
        Log.v(LOG_TAG, "setupNextRtcWakeup: at " + millis);
        if (millis <= 0) {
            throw new IllegalArgumentException("millis <= 0: " + millis);
        }
        AlarmManager alarmManager = getAlarmManager(context);
        setExactCompat(alarmManager, createTaskCheckPendingIntent(context), millis);
    }


    public void stopRtcRepeating(Context context) {
        Log.v(LOG_TAG, "stopRtcRepeating");
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
            sCheckTasksPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_CHECK_TASK_REPEATEDLY,
                    new Intent(ACTION_CHECK_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return sCheckTasksPendingIntent;
    }
}
