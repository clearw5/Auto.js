package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public class IdFilter extends ListFilter.Default {

    private static final KeyGetter ID_GETTER = new KeyGetter() {

        @Override
        public String getKey(AccessibilityNodeInfo nodeInfo) {
            return nodeInfo.getViewIdResourceName();
        }
    };

    public static IdFilter equals(String id) {
        return new IdFilter(id);
    }

    public static StringStartsWithFilter startsWith(String prefix) {
        return new StringStartsWithFilter(prefix, ID_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new StringEndsWithFilter(suffix, ID_GETTER);
    }

    public static StringContainsFilter contains(String contains) {
        return new StringContainsFilter(contains, ID_GETTER);
    }

    public static StringMatchesFilter matches(String regex) {
        return new StringMatchesFilter(regex, ID_GETTER);
    }

    private String mId;

    private IdFilter(String id) {
        mId = id;
    }

    @Override
    public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfoAllocator allocator, AccessibilityNodeInfo node) {
        return allocator.findAccessibilityNodeInfosByViewId(node, mId);
    }


}
