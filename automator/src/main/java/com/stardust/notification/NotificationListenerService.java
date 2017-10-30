package com.stardust.notification;

import android.app.Notification;
import android.os.Build;
import android.os.Parcel;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import com.stardust.view.accessibility.NotificationListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/10/30.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();
    private static NotificationListenerService sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static NotificationListenerService getInstance() {
        return sInstance;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotification(com.stardust.notification.Notification.create(
                    sbn.getNotification(), sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
    }

    public void addListener(NotificationListener listener) {
        mNotificationListeners.add(listener);
    }

    public boolean removeListener(NotificationListener listener) {
        return mNotificationListeners.remove(listener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sInstance = null;
    }
}
