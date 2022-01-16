package com.taobao.idlefish

import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.view.WindowManager
import com.stardust.autojs.core.pref.Pref
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.view.accessibility.AccessibilityService

class AccessibilityService: AccessibilityService() {

    private lateinit var windowManager: WindowManager

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
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setServiceInfo(serviceInfo)
        super.onServiceConnected()
    }

    override fun getWindowManager() : WindowManager {
        return windowManager
    }

    override fun refreshFloatyService() {
        FloatyService.getInstance()?.refreshAccessWindowManager()
    }
}