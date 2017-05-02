package com.stardust.automator.simple_action;

import com.stardust.automator.UiObject;

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
    public boolean perform(UiObject root) {
        List<UiObject> scrollableNodes = findScrollableNodes(root);
        boolean result = mIndex < scrollableNodes.size() && scrollableNodes.get(mIndex).performAction(mAction);
        recycle(scrollableNodes, root);
        return result;
    }

    private void recycle(List<UiObject> list, UiObject root) {
        for (UiObject nodeInfo : list) {
            if (nodeInfo != root)
                nodeInfo.recycle();
        }
    }

    private List<UiObject> findScrollableNodes(UiObject root) {
        List<UiObject> list = new ArrayList<>();
        findScrollableNodes(root, list);
        return list;
    }

    private static boolean findScrollableNodes(UiObject node, List<UiObject> list) {
        if (node == null) {
            return false;
        }
        if (node.isScrollable()) {
            list.add(node);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            UiObject child = node.child(i);
            if (child == null)
                continue;
            if (!findScrollableNodes(child, list))
                child.recycle();
        }
        return node.isScrollable();
    }
}
