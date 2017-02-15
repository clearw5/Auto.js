package com.stardust.scriptdroid.external.notification.bounds_assist;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.bounds_assist.BoundsAssistant;
import com.stardust.util.StateObserver;


/**
 * Created by Stardust on 2017/2/2.
 */

public class BoundsAssistSwitchNotification {

    private static final int NOTIFY_ID = 11126;
    public static final String KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE = "KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE";

    private static boolean enable = false;

    public static boolean isEnable() {
        return enable;
    }

    static {
        App.getStateObserver().register(KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE, new StateObserver.OnStateChangedListener() {
            @Override
            public void onStateChanged(boolean newState) {
                setEnable(newState);
            }

            @Override
            public void initState(boolean state) {
                enable = state;
                if (enable) {
                    showNotification();
                }
            }
        });
        App.getStateObserver().register(BoundsAssistant.KEY_BOUNDS_ASSIST_ENABLE, new StateObserver.OnStateChangedListener() {
            @Override
            public void onStateChanged(boolean newState) {
                if (enable) {
                    setEnable(false);
                    setEnable(true);
                }
            }

            @Override
            public void initState(boolean state) {

            }
        });
    }

    public static void setEnable(boolean enable) {
        if (BoundsAssistSwitchNotification.enable == enable)
            return;
        BoundsAssistSwitchNotification.enable = enable;
        PreferenceManager.getDefaultSharedPreferences(App.getApp()).edit().putBoolean(KEY_BOUNDS_ASSIST_SWITCH_NOTIFICATION_ENABLE, enable).apply();
        if (enable) {
            showNotification();
        } else {
            hideNotification();
        }
    }


    private static void showNotification() {
        Notification notification = new NotificationCompat.Builder(App.getApp())
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_robot_head)
                .setDeleteIntent(BoundsAssistSwitchNotificationHandleService.getDeletePendingIntent())
                .setContentText(BoundsAssistant.isAssistModeEnable() ?
                        App.getApp().getString(R.string.text_assist_mode_enabled) :
                        App.getApp().getString(R.string.text_assist_mode_disabled))
                .setContentIntent(BoundsAssistSwitchNotificationHandleService.getStartIntent())
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
