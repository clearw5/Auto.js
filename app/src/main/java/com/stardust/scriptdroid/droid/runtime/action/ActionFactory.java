package com.stardust.scriptdroid.droid.runtime.action;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.util.MapEntries;

import java.util.Map;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ActionFactory {

    private static Map<Integer, Object> searchUpAction = new MapEntries<Integer, Object>()
            .entry(AccessibilityNodeInfo.ACTION_CLICK, null)
            .entry(AccessibilityNodeInfo.ACTION_LONG_CLICK, null)
            .entry(AccessibilityNodeInfo.ACTION_SELECT, null)
            .entry(AccessibilityNodeInfo.ACTION_FOCUS, null)
            .entry(AccessibilityNodeInfo.ACTION_SELECT, null)
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, null)
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, null)
            .map();

    public static Action createActionWithTextFilter(int action, String text) {
        if (searchUpAction.containsKey(action))
            return new SearchUpTargetAction(action, new FilterAction.TextFilter(text));
        else
            return new DepthFirstSearchTargetAction(action, new FilterAction.TextFilter(text));
    }

    public static Action createActionWithBoundsFilter(int action, Rect rect) {
        if (searchUpAction.containsKey(action))
            return new SearchUpTargetAction(action, new FilterAction.BoundsFilter(rect));
        else
            return new DepthFirstSearchTargetAction(action, new FilterAction.BoundsFilter(rect));
    }

    public static Action createActionWithEditableFilter(int action, int index, final String text) {
        return new SearchTargetAction(action, new FilterAction.EditableFilter(index)) {

            @Override
            protected void performAction(AccessibilityNodeInfo node) {
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                node.performAction(getAction(), args);
            }
        };
    }

    public static Action createScrollAllAction(int action) {
        return new ScrollAllAction(action);
    }
}
