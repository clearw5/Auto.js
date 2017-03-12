package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public abstract class DfsFilter implements ListFilter, Filter {

    @Override
    public List<AccessibilityNodeInfo> filter(List<AccessibilityNodeInfo> nodes) {
        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        for (AccessibilityNodeInfo node : nodes) {
            if (isIncluded(node)) {
                list.add(node);
            }
            filterChildren(node, list);
        }
        return list;
    }

    public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo node) {
        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        if (isIncluded(node)) {
            list.add(node);
        }
        filterChildren(node, list);
        return list;
    }

    private void filterChildren(AccessibilityNodeInfo parent, List<AccessibilityNodeInfo> list) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null)
                continue;
            boolean included = isIncluded(child);
            if (included) {
                list.add(child);
            }
            filterChildren(child, list);
            if (!included) {
                child.recycle();
            }
        }
    }

    protected abstract boolean isIncluded(AccessibilityNodeInfo nodeInfo);
}
