package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.test.TestUiObject;
import com.stardust.automator.UiObject;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Stardust on 2017/5/5.
 */
public class ScrollActionTest {
    @Test
    public void perform() throws Exception {
        ScrollAction action = new ScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, 0);
        UiObject root = new TestUiObject(5);
        action.perform(root);
        System.out.println(TestUiObject.max);
        assertEquals(1, TestUiObject.count);
    }

}