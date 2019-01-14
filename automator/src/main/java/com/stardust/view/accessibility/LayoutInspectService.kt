package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by Stardust on 2017/7/13.
 */

class LayoutInspectService : AccessibilityService() {


    override fun onServiceConnected() {
        val info = serviceInfo
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = info
        super.onServiceConnected()
    }

    companion object {

        val instance: LayoutInspectService? = null
    }
}

