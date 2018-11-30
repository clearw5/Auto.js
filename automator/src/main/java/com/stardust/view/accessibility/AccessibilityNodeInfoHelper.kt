package com.stardust.view.accessibility

import android.graphics.Rect
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/6.
 */

object AccessibilityNodeInfoHelper {

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @param width  pixel width of the display
     * @param height pixel height of the display
     * @return null if node is null, else a Rect containing visible bounds
     */
    fun getVisibleBoundsInScreen(node: AccessibilityNodeInfo?, width: Int, height: Int): Rect? {
        if (node == null) {
            return null
        }
        // targeted node's bounds
        val nodeRect = Rect()
        node.getBoundsInScreen(nodeRect)

        val displayRect = Rect()
        displayRect.top = 0
        displayRect.left = 0
        displayRect.right = width
        displayRect.bottom = height
        val intersect = nodeRect.intersect(displayRect)
        return nodeRect
    }

    fun getBoundsInParent(nodeInfo: AccessibilityNodeInfo): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInParent(rect)
        return rect
    }

    fun getBoundsInScreen(nodeInfo: AccessibilityNodeInfo): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        return rect
    }

    fun getBoundsInScreen(nodeInfo: AccessibilityNodeInfoCompat): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        return rect
    }

    fun getBoundsInParent(nodeInfo: AccessibilityNodeInfoCompat): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInParent(rect)
        return rect
    }
}
