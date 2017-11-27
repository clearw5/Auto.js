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

public class TaskSchedulerReceiver extends BroadcastReceiver {

    public static final String ACTION_CHECK_TASK = "com.stardust.autojs.action.check_task";
    private static final String LOG_TAG = "TaskSchedulerReceiver";
    private static final int REQUEST_CODE = 4056;
    private static final long INTERVAL = 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        checkTasks(context);
    }

    public static void checkTasks(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        TimedTaskManager.getInstance().getFutureTasksInMillis(TimeUnit.HOURS.toMillis(1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timedTask -> scheduleTask(context, alarmManager, timedTask));
    }

    private static void scheduleTask(Context context, AlarmManager alarmManager, TimedTask timedTask) {
        long millis = timedTask.getNextTime();
        PendingIntent op = PendingIntent.getBroadcast(context, REQUEST_CODE + 1 + timedTask.getId(),
                timedTask.createIntent().setAction(TaskReceiver.ACTION_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT);
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
                        new Intent(TaskSchedulerReceiver.ACTION_CHECK_TASK), PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
