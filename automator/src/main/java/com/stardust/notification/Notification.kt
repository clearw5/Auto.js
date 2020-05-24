package com.stardust.notification

import android.app.PendingIntent
import android.os.Build
import android.os.Parcel
import androidx.annotation.RequiresApi

/**
 * Created by Stardust on 2017/10/30.
 */

class Notification private constructor(val packageName: String) : android.app.Notification() {

    val text: String?
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        get() = extras.getString(EXTRA_TEXT)

    val title: String?
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        get() = extras.getString(EXTRA_TITLE)

    fun click() {
        try {
            this.contentIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            throw RuntimeException(e)
        }

    }

    fun delete() {
        try {
            this.deleteIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            throw RuntimeException(e)
        }

    }

    override fun toString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            "Notification{" +
                    "packageName='" + packageName + "', " +
                    "title='" + title + ", " +
                    "text='" + text + "'" +
                    "} "
        } else super.toString()
    }

    companion object {

        fun create(n: android.app.Notification, packageName: String): Notification {
            val notification = Notification(packageName)
            clone(n, notification)
            return notification
        }

        @Suppress("DEPRECATION")
        fun clone(from: android.app.Notification, to: android.app.Notification) {
            to.`when` = from.`when`
            to.icon = from.icon
            to.iconLevel = from.iconLevel
            to.number = from.number
            to.contentIntent = from.contentIntent
            to.deleteIntent = from.deleteIntent
            to.fullScreenIntent = from.fullScreenIntent
            to.tickerText = from.tickerText
            to.tickerView = from.tickerView
            to.contentView = from.contentView
            to.bigContentView = from.bigContentView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                to.headsUpContentView = from.headsUpContentView
                to.audioAttributes = from.audioAttributes
                to.color = from.color
                to.visibility = from.visibility
                to.category = from.category
                to.publicVersion = from.publicVersion
            }
            to.largeIcon = from.largeIcon
            to.sound = from.sound
            to.audioStreamType = from.audioStreamType
            to.vibrate = from.vibrate
            to.ledARGB = from.ledARGB
            to.ledOnMS = from.ledOnMS
            to.ledOffMS = from.ledOffMS
            to.defaults = from.defaults
            to.flags = from.flags
            to.priority = from.priority
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                to.extras = from.extras
                to.actions = from.actions
            }
        }
    }

}
