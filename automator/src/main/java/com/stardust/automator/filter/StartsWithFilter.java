package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StartsWithFilter extends DfsFilter {

    private final String mPrefix;
    private final KeyGetter mKeyGetter;

    public StartsWithFilter(String prefix, KeyGetter keyGetter) {
        mPrefix = prefix;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.startsWith(mPrefix);
    }

}
