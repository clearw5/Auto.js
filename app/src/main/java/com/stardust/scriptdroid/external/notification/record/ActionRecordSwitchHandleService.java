package com.stardust.scriptdroid.external.notification.record;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stardust.scriptdroid.record.AccessibilityRecorderDelegate;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.service.VolumeChangeListenService;
import com.stardust.scriptdroid.ui.main.MainActivity;

import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.PAUSED;
import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.RECORDING;
import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.STOPPED;

/**
 * Created by Stardust on 2017/2/15.
 */

public class ActionRecordSwitchHandleService extends Service {


    private static final String EXTRA_INTENT_VALID = "ActionRecordSwitchHandleService.intentValid";
    private static final String EXTRA_ACTION = "action";
    private static final int ACTION_STOP = 17771;
    private static final int ACTION_START_OR_PAUSE = 17772;
    private static final int ACTION_DELETE = 17773;
    private static final int ACTION_REDO = 17774;

    public static PendingIntent getStartOrPauseIntent() {
        Intent intent = new Intent(App.getApp(), ActionRecordSwitchHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_START_OR_PAUSE);
        return PendingIntent.getService(App.getApp(), ACTION_START_OR_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getStopIntent() {
        Intent intent = new Intent(App.getApp(), ActionRecordSwitchHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_STOP);
        return PendingIntent.getService(App.getApp(), ACTION_STOP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent() {
        Intent intent = new Intent(App.getApp(), ActionRecordSwitchHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_DELETE);
        return PendingIntent.getService(App.getApp(), ACTION_DELETE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static PendingIntent getRedoIntent() {
        Intent intent = new Intent(App.getApp(), ActionRecordSwitchHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_REDO);
        return PendingIntent.getService(App.getApp(), ACTION_REDO, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean intentValid = intent.getBooleanExtra(EXTRA_INTENT_VALID, false);
        if (intentValid) {
            performAction(intent.getIntExtra(EXTRA_ACTION, 0));
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private void performAction(int action) {
        switch (action) {
            case ACTION_STOP:
                stopRecord(this);
                collapseNotificationBar();
                break;
            case ACTION_START_OR_PAUSE:
                startOrPauseRecord(this);
                collapseNotificationBar();
                break;
            case ACTION_DELETE:
                ActionRecordSwitchNotification.cancelNotification();
                stopRecordIfNeeded();
                VolumeChangeListenService.stopServiceIfNeeded();
                break;
            case ACTION_REDO:
                redoRecord(this);
                break;
        }
    }


    private void collapseNotificationBar() {
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void stopRecordIfNeeded() {
        if (AccessibilityRecorderDelegate.getInstance().getState() != STOPPED) {
            stopRecord(this);
        }
    }

    private static void startOrPauseRecord(Context context) {
        int state = AccessibilityRecorderDelegate.getInstance().getState();
        if (state == PAUSED) {
            resumeRecord(context);
        } else if (state == RECORDING) {
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
        AccessibilityRecorderDelegate.getInstance().startRecord();
        ActionRecordSwitchView.getInstance().setState(RECORDING);
        Toast.makeText(context, R.string.text_start_record, Toast.LENGTH_SHORT).show();
    }

    private static void pauseRecord(Context context) {
        AccessibilityRecorderDelegate.getInstance().pauseRecord();
        ActionRecordSwitchView.getInstance().setState(PAUSED);
    }

    private static void resumeRecord(Context context) {
        AccessibilityRecorderDelegate.getInstance().resumeRecord();
        ActionRecordSwitchView.getInstance().setState(RECORDING);
    }

    public static void stopRecord(Context context) {
        if (AccessibilityRecorderDelegate.getInstance().getState() != STOPPED) {
            String script = AccessibilityRecorderDelegate.getInstance().stopRecord();
            ActionRecordSwitchView.getInstance().setState(STOPPED);
            MainActivity.onActionRecordStopped(context, script);
        } else {
            Toast.makeText(App.getApp(), R.string.text_not_recording, Toast.LENGTH_SHORT).show();
        }
    }

    private static void redoRecord(Context context) {
        if (AccessibilityRecorderDelegate.getInstance().getState() != STOPPED) {
            AccessibilityRecorderDelegate.getInstance().stopRecord();
            ActionRecordSwitchView.getInstance().setState(STOPPED);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
