package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.accessibility.AccessibilityEvent

import com.stardust.notification.Notification

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Stardust on 2017/8/1.
 */

interface NotificationListener {


    fun onNotification(notification: Notification)


}
