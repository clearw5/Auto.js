package com.stardust.notification

import android.app.Notification
import android.os.Build
import android.os.Parcel
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi

import com.stardust.view.accessibility.NotificationListener
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Stardust on 2017/10/30.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationListenerService : android.service.notification.NotificationListenerService() {

    private val mNotificationListeners = CopyOnWriteArrayList<NotificationListener>()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: RankingMap) {
        onNotificationPosted(sbn)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        for (listener in mNotificationListeners) {
            listener.onNotification(com.stardust.notification.Notification.create(
                    sbn.notification, sbn.packageName))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: RankingMap) {}

    fun addListener(listener: NotificationListener) {
        mNotificationListeners.add(listener)
    }

    fun removeListener(listener: NotificationListener): Boolean {
        return mNotificationListeners.remove(listener)
    }


    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    companion object {
        var instance: NotificationListenerService? = null
            private set
    }
}
