package com.stardust.scriptdroid.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
    private static final int REQUEST_CODE = 4000;
    private static final long INTERVAL = 60 * 1000;
    private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        checkTasks(context);
    }

    public static void checkTasks(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        TimedTaskManager.getInstance().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTaskIfNeeded(context, alarmManager, timedTask));
    }

    public static void scheduleTaskIfNeeded(Context context, AlarmManager alarmManager, TimedTask timedTask) {
        long millis = timedTask.getNextTime();
        if (timedTask.isScheduled() || millis - System.currentTimeMillis() > ONE_HOUR) {
            return;
        }
        scheduleTask(context, alarmManager, timedTask, millis);
        TimedTaskManager.getInstance()
                .notifyTaskScheduled(timedTask);

    }

    public static void scheduleTaskIfNeeded(Context context, TimedTask timedTask) {
        scheduleTaskIfNeeded(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE), timedTask);
    }

    private static void scheduleTask(Context context, AlarmManager alarmManager, TimedTask timedTask, long millis) {
        if (millis <= System.currentTimeMillis()) {
            context.sendBroadcast(timedTask.createIntent());
            return;
        }
        // FIXME: 2017/11/28 requestCode may > 65535
        PendingIntent op = timedTask.createPendingIntent(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, op);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, op);
        }
    }

    public static void setupRepeating(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000,
                INTERVAL, PendingIntent.getBroadcast(context, REQUEST_CODE,
                        new Intent(TimedTaskScheduler.ACTION_CHECK_TASK), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public static void cancel(Context context, TimedTask timedTask) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(timedTask.createPendingIntent(context));
    }
}
