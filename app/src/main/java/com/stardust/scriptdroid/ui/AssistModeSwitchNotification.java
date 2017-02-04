package com.stardust.scriptdroid.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.AssistModeSwitchService;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;
import com.stardust.util.StateObserver;

import static com.stardust.scriptdroid.droid.runtime.action.ActionPerformService.KEY_ASSIST_MODE_ENABLE;

/**
 * Created by Stardust on 2017/2/2.
 */

public class AssistModeSwitchNotification {

    private static final int NOTIFY_ID = 11126;
    public static final String KEY_ASSIST_MODE_NOTIFICATION = "KEY_ASSIST_MODE_NOTIFICATION";

    private static boolean enable = false;
    private static Notification notification;

    public static boolean isEnable() {
        return enable;
    }

    static {
        App.getStateObserver().register(KEY_ASSIST_MODE_NOTIFICATION, new StateObserver.OnBooleanStateChangedListener() {
            @Override
            public void onStateChanged(Boolean newState) {
                setEnable(newState);
            }

            @Override
            public void initState(Boolean state) {
                enable = state;
                if (enable) {
                    showNotification();
                }
            }
        });
        App.getStateObserver().register(KEY_ASSIST_MODE_ENABLE, new StateObserver.OnBooleanStateChangedListener() {
            @Override
            public void onStateChanged(Boolean newState) {
                if (enable) {
                    setEnable(false);
                    setEnable(true);
                }
            }

            @Override
            public void initState(Boolean state) {

            }
        });
    }

    public static void setEnable(boolean enable) {
        if (AssistModeSwitchNotification.enable == enable)
            return;
        AssistModeSwitchNotification.enable = enable;
        PreferenceManager.getDefaultSharedPreferences(App.getApp()).edit().putBoolean(KEY_ASSIST_MODE_NOTIFICATION, enable).apply();
        if (enable) {
            showNotification();
        } else {
            hideNotification();
        }
    }


    private static void showNotification() {
        notification = new NotificationCompat.Builder(App.getApp())
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_robot_head)
                .setDeleteIntent(AssistModeSwitchService.getDeletePendingIntent())
                .setContentText(ActionPerformService.isAssistModeEnable() ?
                        App.getApp().getString(R.string.text_assist_mode_enabled) :
                        App.getApp().getString(R.string.text_assist_mode_disabled))
                .setContentIntent(AssistModeSwitchService.getStartIntent())
                .build();
        showNotification(notification);
    }

    private static void showNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    private static void hideNotification() {
        NotificationManager notificationManager = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);
    }

}
