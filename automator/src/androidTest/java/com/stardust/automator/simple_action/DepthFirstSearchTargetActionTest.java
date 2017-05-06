package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
import com.stardust.automator.test.TestUiObject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Stardust on 2017/5/5.
 */
public class DepthFirstSearchTargetActionTest {

    @Test
    public void perform() {
        DepthFirstSearchTargetAction action = new DepthFirstSearchTargetAction(AccessibilityNodeInfo.ACTION_CLICK, new FilterAction.Filter() {
            @Override
            public List<UiObject> filter(UiObject root) {
                List<UiObject> list = new ArrayList<>();
                for (int i = 0; i < root.getChildCount(); i++) {
                    list.add(root.child(i));
                }
                return list;
            }
        });
        TestUiObject root = new TestUiObject(5);
        action.perform(root);
        System.out.println(TestUiObject.max);
        assertEquals(1, TestUiObject.count);
    }

}