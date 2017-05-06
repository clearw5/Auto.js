package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
import com.stardust.automator.test.TestUiObject;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Stardust on 2017/5/5.
 */
public class ScrollMaxActionTest {
    @Test
    public void perform() throws Exception {
        ScrollMaxAction action = new ScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        UiObject root = new TestUiObject(20);
        action.perform(root);
        System.out.println(TestUiObject.max);
        assertEquals(1, TestUiObject.count);
    }

}