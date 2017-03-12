package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class EqualsFilter extends DfsFilter {

    private String mText;
    private KeyGetter mKeyGetter;

    public EqualsFilter(String text, KeyGetter getter) {
        mText = text;
        mKeyGetter = getter;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        if(key != null){
            return key.equals(mText);
        }
        return false;
    }
}
