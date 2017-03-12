package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

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

    public static StartsWithFilter startsWith(String prefix) {
        return new StartsWithFilter(prefix, ID_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new EndsWithFilter(suffix, ID_GETTER);
    }

    public static ContainsFilter contains(String contains) {
        return new ContainsFilter(contains, ID_GETTER);
    }

    public static MatchesFilter matches(String regex) {
        return new MatchesFilter(regex, ID_GETTER);
    }

    private String mId;

    private IdFilter(String id) {
        mId = id;
    }

    @Override
    public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo node) {
        return node.findAccessibilityNodeInfosByViewId(mId);
    }


}
