package com.stardust.automator.filter

import android.graphics.Rect

import com.stardust.automator.UiObject
import com.stardust.view.accessibility.AccessibilityNodeInfoHelper

import java.util.Locale

/**
 * Created by Stardust on 2017/3/9.
 */

class BoundsFilter(private val mBounds: Rect, private val mType: Int) : Filter {

    override fun filter(node: UiObject): Boolean {
        if (mType == TYPE_CONTAINS) {
            return AccessibilityNodeInfoHelper.getBoundsInScreen(node).contains(mBounds)
        }
        val boundsInScreen = AccessibilityNodeInfoHelper.getBoundsInScreen(node)
        return if (mType == TYPE_EQUALS) boundsInScreen == mBounds else mBounds.contains(boundsInScreen)
    }

    override fun toString(): String {
        return String.format(Locale.getDefault(), "bounds%s(%d, %d, %d, %d)", when (mType) {
            TYPE_EQUALS -> ""
            TYPE_INSIDE -> "Inside"
            else -> "Contains"
        },
                mBounds.left, mBounds.top, mBounds.right, mBounds.bottom)
    }

    companion object {

        const val TYPE_EQUALS = 0
        const val TYPE_INSIDE = 1
        const val TYPE_CONTAINS = 2
    }
}
