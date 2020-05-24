package com.stardust.automator.simple_action

import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.automator.test.TestUiObject
import com.stardust.automator.UiObject

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Stardust on 2017/5/5.
 */
class ScrollActionTest {
    @Test
    @Throws(Exception::class)
    fun perform() {
        val action = ScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, 0)
        val root = TestUiObject(5)
        action.perform(root)
        println(TestUiObject.max)
        assertEquals(1, TestUiObject.count.toLong())
    }

}