package com.stardust.automator.simple_action

import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.automator.UiObject
import com.stardust.automator.test.TestUiObject

import org.junit.Test

import java.util.ArrayList

import org.junit.Assert.*

/**
 * Created by Stardust on 2017/5/5.
 */
class DepthFirstSearchTargetActionTest {

    @Test
    fun perform() {
        val action = DepthFirstSearchTargetAction(AccessibilityNodeInfo.ACTION_CLICK, FilterAction.Filter { root ->
            val list = ArrayList<UiObject>()
            for (i in 0 until root.childCount) {
                list.add(root.child(i))
            }
            list
        })
        val root = TestUiObject(5)
        action.perform(root)
        println(TestUiObject.max)
        assertEquals(1, TestUiObject.count.toLong())
    }

}