package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public class SearchUpTargetAction extends SearchTargetAction {

    private static final String TAG = SearchUpTargetAction.class.getSimpleName();
    private static final int LOOP_MAX = 20;
    private Able mAble;

    public SearchUpTargetAction(int action, Filter filter) {
        super(action, filter);
        mAble = Able.ABLE_MAP.get(action);
    }

    @Override
    public AccessibilityNodeInfo searchTarget(AccessibilityNodeInfo n) {
        AccessibilityNodeInfo node = n;
        int i = 0;
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        while (node != null && !mAble.isAble(node)) {
            i++;
            if (i > LOOP_MAX) {
                node.recycle();
                return null;
            }
            AccessibilityNodeInfo parent = getAllocator().getParent(node);
            list.add(node);
            node = parent;
        }
        return node;
    }

    @Override
    public String toString() {
        return "SearchUpTargetAction{" +
                "mAble=" + mAble + ", " +
                super.toString() +
                '}';
    }
}
