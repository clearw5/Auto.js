package com.stardust.scriptdroid;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.droid.assist.Assistant;

import static com.stardust.scriptdroid.ui.AssistModeSwitchNotification.KEY_ASSIST_MODE_NOTIFICATION;

/**
 * Created by Stardust on 2017/2/2.
 */
public class AssistModeSwitchService extends Service {

    public static PendingIntent getStartIntent() {
        Intent intent = new Intent(App.getApp(), AssistModeSwitchService.class)
                .putExtra("intentValid", true)
                .putExtra("switch", "assistMode");
        return PendingIntent.getService(App.getApp(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean intentValid = intent.getBooleanExtra("intentValid", false);
        if (intentValid) {
            String action = intent.getStringExtra("switch");
            if (action != null) {
                if (action.equals("assistMode")) {
                    Assistant.setAssistModeEnable(!Assistant.isAssistModeEnable());
                } else {
                    App.getStateObserver().setState(KEY_ASSIST_MODE_NOTIFICATION, false);
                }
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static PendingIntent getDeletePendingIntent() {
        Intent deleteIntent = new Intent(App.getApp(), AssistModeSwitchService.class)
                .putExtra("intentValid", true)
                .putExtra("switch", "assistModeNotification");
        return PendingIntent.getService(App.getApp(), 0, deleteIntent, PendingIntent.FLAG_ONE_SHOT);
    }
}
