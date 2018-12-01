package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.stardust.app.isOpPermissionGranted

/**
 * Created by Stardust on 2017/3/9.
 */

class AccessibilityInfoProvider(private val context: Context) : AccessibilityDelegate {

    private val mPackageManager: PackageManager = context.packageManager

    @Volatile
    private var mLatestPackage: String = ""

    val latestPackage: String
        get() {
            if (useUsageStats && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mLatestPackage = getLatestPackageByUsageStats()
            }
            return mLatestPackage
        }

    @Volatile
    var latestActivity = ""
        private set

    var useUsageStats: Boolean = false

    override val eventTypes: Set<Int>?
        get() = AccessibilityDelegate.ALL_EVENT_TYPES

    override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent): Boolean {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            setLatestComponent(event.packageName, event.className)
        }
        return false
    }

    fun getLatestPackageByUsageStatsIfGranted(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && context.isOpPermissionGranted(AppOpsManager.OPSTR_GET_USAGE_STATS)) {
            return getLatestPackageByUsageStats()
        }
        return mLatestPackage
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getLatestPackageByUsageStats(): String {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val current = System.currentTimeMillis()
        val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, current - 60 * 60 * 1000, current)
        return if (usageStats.isEmpty()) {
            mLatestPackage
        } else {
            usageStats.sortBy {
                it.lastTimeStamp
            }
            usageStats.last().packageName
        }

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
        mLatestPackage = latestPackage.toString()
    }
}
