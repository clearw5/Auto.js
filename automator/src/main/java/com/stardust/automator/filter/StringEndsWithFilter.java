package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StringEndsWithFilter extends DfsFilter {

    private final String mSuffix;
    private final KeyGetter mKeyGetter;

    public StringEndsWithFilter(String suffix, KeyGetter keyGetter) {
        mSuffix = suffix;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.endsWith(mSuffix);
    }

}
