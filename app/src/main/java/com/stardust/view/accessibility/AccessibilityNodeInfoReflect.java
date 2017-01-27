package com.stardust.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityNodeInfoReflect {

    private final AccessibilityNodeInfo mAccessibilityNodeInfo;

    public AccessibilityNodeInfoReflect(AccessibilityNodeInfo accessibilityNodeInfo) {
        mAccessibilityNodeInfo = accessibilityNodeInfo;
    }

    public void getChildNodeIds() {
        try {
            Field mChildNodeIdsField = AccessibilityNodeInfo.class.getDeclaredField("mChildNodeIds");
            mChildNodeIdsField.setAccessible(true);
            Object o = mChildNodeIdsField.get(mAccessibilityNodeInfo);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public long getSourceNodeId() {
        try {
            Field mSourceNodeIdField = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
            mSourceNodeIdField.setAccessible(true);
            return (long) mSourceNodeIdField.get(mSourceNodeIdField);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
