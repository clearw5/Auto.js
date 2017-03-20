package com.stardust.scriptdroid.external.notification.record;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.record.Recorder;
import com.stardust.scriptdroid.record.accessibility.AccessibilityActionRecorder;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.service.VolumeChangeObverseService;
import com.stardust.scriptdroid.ui.main.MainActivity;

/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityActionRecordNotification {

    private static final String INTENT_ACTION = " com.stardust.scriptdroid.Record";
    private static final String EXTRA_ACTION = "action";
    private static final int ACTION_STOP = 17771;
    private static final int ACTION_START_OR_PAUSE = 17772;
    private static final int ACTION_DELETE = 17773;
    private static final int ACTION_REDO = 17774;

    private static final int NOTIFY_ID = 22236;
    private static NotificationCompat.Builder builder;
    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_ACTION, 0);
            switch (action) {
                case ACTION_STOP:
                    stopRecord(context);
                    collapseNotificationBar(context);
                    break;
                case ACTION_START_OR_PAUSE:
                    startOrPauseRecord(context);
                    collapseNotificationBar(context);
                    break;
                case ACTION_DELETE:
                    AccessibilityActionRecordNotification.cancelNotification();
                    stopRecordIfNeeded(context);
                    break;
                case ACTION_REDO:
                    redoRecord(context);
                    break;
            }
        }
    };


    static PendingIntent getStartOrPauseIntent() {
        Intent intent = new Intent(INTENT_ACTION)
                .putExtra(EXTRA_ACTION, ACTION_START_OR_PAUSE);
        return PendingIntent.getBroadcast(App.getApp(), ACTION_START_OR_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static PendingIntent getStopIntent() {
        Intent intent = new Intent(INTENT_ACTION)
                .putExtra(EXTRA_ACTION, ACTION_STOP);
        return PendingIntent.getBroadcast(App.getApp(), ACTION_STOP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static PendingIntent getDeleteIntent() {
        Intent intent = new Intent(INTENT_ACTION)
                .putExtra(EXTRA_ACTION, ACTION_DELETE);
        return PendingIntent.getBroadcast(App.getApp(), ACTION_DELETE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    static PendingIntent getRedoIntent() {
        Intent intent = new Intent(INTENT_ACTION)
                .putExtra(EXTRA_ACTION, ACTION_REDO);
        return PendingIntent.getService(App.getApp(), ACTION_REDO, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void showOrUpdateNotification() {
        if (builder == null) {
            builder = new NotificationCompat.Builder(App.getApp())
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_robot_head)
                    .setDeleteIntent(getDeleteIntent())
                    .setCustomContentView(AccessibilityActionRecordSwitchView.getInstance());
        }
        showNotification(builder.build());
        App.getApp().startService(new Intent(App.getApp(), VolumeChangeObverseService.class));
    }

    private static void showNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
        App.getApp().registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION));
    }

    public static void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);
        App.getApp().unregisterReceiver(broadcastReceiver);
    }

    private static void collapseNotificationBar(Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private static void stopRecordIfNeeded(Context context) {
        int state = AccessibilityActionRecorder.getInstance().getState();
        if (state == Recorder.STATE_RECORDING || state == Recorder.STATE_PAUSED) {
            stopRecord(context);
        }
    }

    private static void startOrPauseRecord(Context context) {
        int state = AccessibilityActionRecorder.getInstance().getState();
        if (state == Recorder.STATE_PAUSED) {
            resumeRecord(context);
        } else if (state == Recorder.STATE_RECORDING) {
            pauseRecord(context);
        } else {
            //state == STOPPED
            startRecord(context);
        }
    }

    public static void startRecord(Context context) {
        if (AccessibilityWatchDogService.getInstance() == null) {
            Toast.makeText(context, R.string.text_need_enable_accessibility_service, Toast.LENGTH_SHORT).show();
            return;
        }
        AccessibilityActionRecorder.getInstance().start();
        AccessibilityActionRecordSwitchView.getInstance().setState(Recorder.STATE_RECORDING);
        Toast.makeText(context, R.string.text_start_record, Toast.LENGTH_SHORT).show();
    }

    private static void pauseRecord(Context context) {
        AccessibilityActionRecorder.getInstance().pause();
        AccessibilityActionRecordSwitchView.getInstance().setState(Recorder.STATE_PAUSED);
    }

    private static void resumeRecord(Context context) {
        AccessibilityActionRecorder.getInstance().resume();
        AccessibilityActionRecordSwitchView.getInstance().setState(Recorder.STATE_RECORDING);
    }

    public static void stopRecord(Context context) {
        if (AccessibilityActionRecorder.getInstance().getState() != Recorder.STATE_STOPPED) {
            AccessibilityActionRecorder.getInstance().stop();
            String script = AccessibilityActionRecorder.getInstance().getCode();
            AccessibilityActionRecordSwitchView.getInstance().setState(Recorder.STATE_STOPPED);
            MainActivity.onActionRecordStopped(context, script);
        } else {
            Toast.makeText(App.getApp(), R.string.text_not_recording, Toast.LENGTH_SHORT).show();
        }
    }

    private static void redoRecord(Context context) {
        if (AccessibilityActionRecorder.getInstance().getState() != Recorder.STATE_STOPPED) {
            AccessibilityActionRecorder.getInstance().stop();
            AccessibilityActionRecordSwitchView.getInstance().setState(Recorder.STATE_STOPPED);
        }
    }
}
