package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class ContainsFilter extends DfsFilter {
    private final KeyGetter mKeyGetter;
    private final String mContains;

    ContainsFilter(String contains, KeyGetter keyGetter) {
        mContains = contains;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.contains(mContains);
    }

}
