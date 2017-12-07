package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

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
    protected boolean isIncluded(UiObject nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.endsWith(mSuffix);
    }

    @Override
    public String toString() {
        return mKeyGetter.toString() + "EndsWith(\"" + mSuffix + "\")";
    }
}
