package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.view.accessibility.AccessibilityDelegate

import java.util.Arrays
import java.util.Collections
import java.util.HashSet

/**
 * Created by Stardust on 2017/3/9.
 */

class AccessibilityInfoProvider(private val mPackageManager: PackageManager) : AccessibilityDelegate {


    @Volatile
    var latestPackage = ""
        private set
    @Volatile
    var latestActivity = ""
        private set

    override val eventTypes: Set<Int>?
        get() = AccessibilityDelegate.ALL_EVENT_TYPES

    override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent): Boolean {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            setLatestComponent(event.packageName, event.className)
        }
        return false
    }

    private fun setLatestComponent(latestPackage: CharSequence?, latestClass: CharSequence?) {
        if (latestPackage == null || latestClass == null)
            return
        val latestPackageStr = latestPackage.toString()
        val latestClassStr = latestClass.toString()
        if (latestClassStr.startsWith("android.view.") || latestClassStr.startsWith("android.widget."))
            return
        try {
            val componentName = ComponentName(latestPackageStr, latestClassStr)
            latestActivity = mPackageManager.getActivityInfo(componentName, PackageManager.MATCH_DEFAULT_ONLY).name
        } catch (ignored: PackageManager.NameNotFoundException) {
            return
        }

        this.latestPackage = latestPackage.toString()
    }
}
