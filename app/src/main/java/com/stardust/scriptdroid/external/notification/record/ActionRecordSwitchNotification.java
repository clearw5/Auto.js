package com.stardust.scriptdroid.external.notification.record;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/2/14.
 */

public class ActionRecordSwitchNotification {

    private static final int NOTIFY_ID = 22236;
    private static NotificationCompat.Builder builder;


    public static void showOrUpdateNotification() {
        if (builder == null) {
            builder = new NotificationCompat.Builder(App.getApp())
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_robot_head)
                    .setDeleteIntent(ActionRecordNotificationHandleService.getDeleteIntent())
                    .setCustomContentView(ActionRecordSwitchView.getInstance());
        }
        showNotification(builder.build());
    }

    private static void showNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
