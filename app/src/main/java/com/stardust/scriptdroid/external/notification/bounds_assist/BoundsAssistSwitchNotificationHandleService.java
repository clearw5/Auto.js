package com.stardust.scriptdroid.external.notification.bounds_assist;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.bounds_assist.BoundsAssistant;

import static com.stardust.scriptdroid.external.notification.bounds_assist.BoundsAssistSwitchNotification.KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE;

/**
 * Created by Stardust on 2017/2/2.
 */
public class BoundsAssistSwitchNotificationHandleService extends Service {


    private static final String EXTRA_INTENT_VALID = "BoundsAssistSwitchNotificationHandleService.intentValid";
    private static final String EXTRA_ACTION = "action";

    private static final int ACTION_TOGGLE_ASSIST_MODE = 1;
    private static final int ACTION_CANCEL_ASSIST_MODE_NOTIFICATION = 2;


    public static PendingIntent getStartIntent() {
        Intent intent = new Intent(App.getApp(), BoundsAssistSwitchNotificationHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_TOGGLE_ASSIST_MODE);
        return PendingIntent.getService(App.getApp(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
            case ACTION_TOGGLE_ASSIST_MODE:
                BoundsAssistant.setAssistModeEnable(!BoundsAssistant.isAssistModeEnable());
                break;
            case ACTION_CANCEL_ASSIST_MODE_NOTIFICATION:
                App.getStateObserver().setState(KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE, false);
                break;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static PendingIntent getDeletePendingIntent() {
        Intent deleteIntent = new Intent(App.getApp(), BoundsAssistSwitchNotificationHandleService.class)
                .putExtra(EXTRA_INTENT_VALID, true)
                .putExtra(EXTRA_ACTION, ACTION_CANCEL_ASSIST_MODE_NOTIFICATION);
        return PendingIntent.getService(App.getApp(), 0, deleteIntent, PendingIntent.FLAG_ONE_SHOT);
    }
}
