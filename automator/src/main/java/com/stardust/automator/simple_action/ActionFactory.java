package com.stardust.automator.simple_action;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
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

    public static SimpleAction createActionWithTextFilter(int action, String text, int index) {
        if (searchUpAction.containsKey(action))
            return new SearchUpTargetAction(action, new FilterAction.TextFilter(text, index));
        else
            return new DepthFirstSearchTargetAction(action, new FilterAction.TextFilter(text, index));
    }

    public static SimpleAction createActionWithBoundsFilter(int action, Rect rect) {
        if (searchUpAction.containsKey(action))
            return new SearchUpTargetAction(action, new FilterAction.BoundsFilter(rect));
        else
            return new DepthFirstSearchTargetAction(action, new FilterAction.BoundsFilter(rect));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static SimpleAction createActionWithEditableFilter(final int action, int index, final String text) {
        return new SearchTargetAction(action, new FilterAction.EditableFilter(index)) {

            @Override
            protected boolean performAction(UiObject node) {
                Bundle args = new Bundle();
                if (action == AccessibilityNodeInfo.ACTION_SET_TEXT) {
                    args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                } else {
                    args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, node.text() + text);
                }
                return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            }
        };
    }

    public static SimpleAction createScrollMaxAction(int action) {
        return new ScrollMaxAction(action);
    }

    public static SimpleAction createScrollAction(int action, int i) {
        return new ScrollAction(action, i);
    }

    public static SimpleAction createActionWithIdFilter(int action, String id) {
        return new FilterAction.SimpleFilterAction(action, new FilterAction.IdFilter(id));
    }
}
