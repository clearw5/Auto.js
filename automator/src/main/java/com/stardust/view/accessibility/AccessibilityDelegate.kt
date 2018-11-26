package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by Stardust on 2017/2/14.
 */

interface AccessibilityDelegate {

    /**
     * 返回调用onAccessibilityEvent时的EventType的集合。如果需要对所有EventType都有效，返回null。
     *
     * @return
     */
    val eventTypes: Set<Int>?

    fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent): Boolean

    companion object {

        val ALL_EVENT_TYPES: Set<Int>? = null
    }

}
