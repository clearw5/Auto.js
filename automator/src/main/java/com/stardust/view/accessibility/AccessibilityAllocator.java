package com.stardust.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Vector;

/**
 * Created by Stardust on 2017/3/22.
 */

public class AccessibilityAllocator {


    private List<AccessibilityNodeInfo> mAccessibilityNodeInfoList = new Vector<>();

    public AccessibilityNodeInfo getChild(AccessibilityNodeInfo parent, int i) {
        return add(parent.getChild(i));
    }

    private AccessibilityNodeInfo add(AccessibilityNodeInfo nodeInfo) {
        mAccessibilityNodeInfoList.add(nodeInfo);
        return nodeInfo;
    }

    public int recycleAll() {
        int recycled = 0;
        for (AccessibilityNodeInfo nodeInfo : mAccessibilityNodeInfoList) {
            try {
                nodeInfo.recycle();
                recycled++;
            } catch (IllegalStateException ignored) {
            }
        }
        return recycled;
    }
}
