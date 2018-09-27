package org.autojs.autojs.timing;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskScheduler extends BroadcastReceiver {

    public static final String ACTION_CHECK_TASK = "com.stardust.autojs.action.check_task";
    private static final String LOG_TAG = "TimedTaskScheduler";
    private static final int REQUEST_CODE_CHECK_TASK_REPEATEDLY = 4000;
    private static final long INTERVAL = TimeUnit.MINUTES.toMillis(1);
    private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);
    private static PendingIntent sCheckTasksPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceiveRtcWakeup");
        checkTasks(context);
        setupNextRtcWakeup(context, System.currentTimeMillis() + INTERVAL);
    }

    @SuppressLint("CheckResult")
    public static void checkTasks(Context context) {
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


    private static void scheduleTask(Context context, TimedTask timedTask, long millis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (millis <= System.currentTimeMillis()) {
            context.sendBroadcast(timedTask.createIntent());
            return;
        }
        assert alarmManager != null;
        // FIXME: 2017/11/28 requestCode may > 65535
        PendingIntent op = timedTask.createPendingIntent(context);
        setExactCompat(alarmManager, op, millis);
    }

    private static void setExactCompat(AlarmManager alarmManager, PendingIntent op, long millis) {
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


    public static void checkTasksRepeatedlyIfNeeded(Context context) {
        if (TimedTaskManager.getInstance().countTasks() > 0) {
            setupNextRtcWakeup(context, System.currentTimeMillis() + 5000);
        }
    }

    private static void setupNextRtcWakeup(Context context, long millis) {
        Log.v(LOG_TAG, "setupNextRtcWakeup: at " + millis);
        if (millis <= 0) {
            throw new IllegalArgumentException("millis <= 0: " + millis);
        }
        AlarmManager alarmManager = getAlarmManager(context);
        setExactCompat(alarmManager, createTaskCheckPendingIntent(context), millis);
    }


    public static void cancel(Context context, TimedTask timedTask) {
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(timedTask.createPendingIntent(context));
    }

    public static void stopRtcRepeating(Context context) {
        Log.v(LOG_TAG, "stopRtcRepeating");
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(createTaskCheckPendingIntent(context));
    }

    private static AlarmManager getAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        return alarmManager;
    }

    private static PendingIntent createTaskCheckPendingIntent(Context context) {
        if (sCheckTasksPendingIntent == null) {
            sCheckTasksPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_CHECK_TASK_REPEATEDLY,
                    new Intent(TimedTaskScheduler.ACTION_CHECK_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return sCheckTasksPendingIntent;
    }
}
