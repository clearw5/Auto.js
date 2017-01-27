package com.stardust.scriptdroid.droid.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ScrollAllAction extends Action {

    private int mScrollAction;

    public ScrollAllAction(int scrollAction) {
        mScrollAction = scrollAction;
    }

    @Override
    public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
        AccessibilityNodeInfo scrollableNodeInfo = findScrollableNodeInfo(rootNodeInfo);
        return scrollableNodeInfo != null && scrollableNodeInfo.performAction(mScrollAction);
    }

    private AccessibilityNodeInfo findScrollableNodeInfo(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null)
            return null;
        if (nodeInfo.isScrollable()) {
            return nodeInfo;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = findScrollableNodeInfo(nodeInfo.getChild(i));
            if (node != null) {
                return node;
            }
        }
        return null;
    }

}
