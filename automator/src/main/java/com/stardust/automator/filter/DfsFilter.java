package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public abstract class DfsFilter implements ListFilter, Filter {

    @Override
    public List<UiObject> filter(List<UiObject> nodes) {
        ArrayList<UiObject> list = new ArrayList<>();
        for (UiObject node : nodes) {
            if (isIncluded(node)) {
                list.add(node);
            }
            filterChildren(node, list);
        }
        return list;
    }

    public List<UiObject> filter(UiObject node) {
        ArrayList<UiObject> list = new ArrayList<>();
        if (isIncluded(node)) {
            list.add(node);
        }
        filterChildren(node, list);
        return list;
    }

    private void filterChildren(UiObject parent, List<UiObject> list) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            UiObject child = parent.child(i);
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

    protected abstract boolean isIncluded(UiObject nodeInfo);
}
