package com.stardust.autojs.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/12.
 */
public class ScrollAction extends Action {

    private int mIndex, mAction;

    public ScrollAction(int action, int i) {
        mAction = action;
        mIndex = i;
    }

    @Override
    public boolean perform(AccessibilityNodeInfo root) {
        List<AccessibilityNodeInfo> scrollableNodes = findScrollableNodes(root);
        boolean result = mIndex < scrollableNodes.size() && scrollableNodes.get(mIndex).performAction(mAction);
        recycle(scrollableNodes, root);
        return result;
    }

    private void recycle(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo root) {
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (nodeInfo != root)
                nodeInfo.recycle();
        }
    }

    private List<AccessibilityNodeInfo> findScrollableNodes(AccessibilityNodeInfo root) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        findScrollableNodes(root, list);
        return list;
    }

    private static boolean findScrollableNodes(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> list) {
        if (node == null) {
            return false;
        }
        if (node.isScrollable()) {
            list.add(node);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child == null)
                continue;
            if (!findScrollableNodes(child, list))
                child.recycle();
        }
        return node.isScrollable();
    }
}
