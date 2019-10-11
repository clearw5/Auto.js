package com.stardust.automator.simple_action

import android.view.accessibility.AccessibilityNodeInfo
import com.stardust.automator.UiObject
import com.stardust.util.SparseArrayEntries

/**
 * Created by Stardust on 2017/1/27.
 */

interface Able {

    fun isAble(node: UiObject): Boolean

    companion object {

        val ABLE_MAP = SparseArrayEntries<Able>()
                .entry(AccessibilityNodeInfo.ACTION_CLICK, object : Able {
                    override fun isAble(nodeInfo: UiObject): Boolean {
                        return nodeInfo.isClickable
                    }
                })
                .entry(AccessibilityNodeInfo.ACTION_LONG_CLICK, object : Able {
                    override fun isAble(nodeInfo: UiObject): Boolean {
                        return nodeInfo.isLongClickable
                    }
                })
                .entry(AccessibilityNodeInfo.ACTION_FOCUS, object : Able {
                    override fun isAble(nodeInfo: UiObject): Boolean {
                        return nodeInfo.isFocusable
                    }
                })
                .entry(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, object : Able {
                    override fun isAble(nodeInfo: UiObject): Boolean {
                        return nodeInfo.isScrollable
                    }
                })
                .entry(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, object : Able {
                    override fun isAble(nodeInfo: UiObject): Boolean {
                        return nodeInfo.isScrollable
                    }
                })
                .sparseArray()
    }

}
