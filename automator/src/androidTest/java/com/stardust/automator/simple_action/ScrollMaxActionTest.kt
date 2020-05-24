package com.stardust.automator.simple_action

import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.automator.UiObject
import com.stardust.automator.test.TestUiObject

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Stardust on 2017/5/5.
 */
class ScrollMaxActionTest {
    @Test
    @Throws(Exception::class)
    fun perform() {
        val action = ScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        val root = TestUiObject(20)
        action.perform(root)
        println(TestUiObject.max)
        assertEquals(1, TestUiObject.count.toLong())
    }

}