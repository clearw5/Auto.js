package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class MatchesFilter extends DfsFilter {

    private final String mRegex;
    private final KeyGetter mKeyGetter;

    MatchesFilter(String regex, KeyGetter keyGetter) {
        mRegex = regex;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.matches(mRegex);
    }

}
