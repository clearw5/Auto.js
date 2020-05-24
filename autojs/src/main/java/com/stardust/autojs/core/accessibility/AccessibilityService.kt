package com.stardust.autojs.core.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import com.stardust.autojs.core.pref.Pref
import com.stardust.view.accessibility.AccessibilityService

class AccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        val serviceInfo = serviceInfo
        if (Pref.isStableModeEnabled) {
            serviceInfo.flags = serviceInfo.flags and AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS.inv()
        } else {
            serviceInfo.flags = serviceInfo.flags or AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Pref.isGestureObservingEnabled) {
                serviceInfo.flags = serviceInfo.flags or AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
            } else {
                serviceInfo.flags = serviceInfo.flags and AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE.inv()
            }
        }
        setServiceInfo(serviceInfo)
        super.onServiceConnected()
    }
}