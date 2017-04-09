package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/12.
 */
public class ScrollAction extends SimpleAction {

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
        findScrollableNodes(getAllocator(), root, list);
        return list;
    }

    private static boolean findScrollableNodes(AccessibilityNodeInfoAllocator allocator, AccessibilityNodeInfo node, List<AccessibilityNodeInfo> list) {
        if (node == null) {
            return false;
        }
        if (node.isScrollable()) {
            list.add(node);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = allocator.getChild(node, i);
            if (child == null)
                continue;
            if (!findScrollableNodes(allocator, child, list))
                child.recycle();
        }
        return node.isScrollable();
    }
}
