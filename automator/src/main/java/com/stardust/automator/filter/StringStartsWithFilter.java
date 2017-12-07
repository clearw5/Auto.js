package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StringStartsWithFilter extends DfsFilter {

    private final String mPrefix;
    private final KeyGetter mKeyGetter;

    public StringStartsWithFilter(String prefix, KeyGetter keyGetter) {
        mPrefix = prefix;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.startsWith(mPrefix);
    }

    @Override
    public String toString() {
        return mKeyGetter.toString() + "StartsWith(\"" + mPrefix + "\")";
    }
}
