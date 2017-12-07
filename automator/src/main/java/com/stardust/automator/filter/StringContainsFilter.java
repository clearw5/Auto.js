package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StringContainsFilter extends DfsFilter {
    private final KeyGetter mKeyGetter;
    private final String mContains;

    StringContainsFilter(String contains, KeyGetter keyGetter) {
        mContains = contains;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.contains(mContains);
    }

    @Override
    public String toString() {
        return mKeyGetter.toString() + "Contains(\"" + mContains + "\")";
    }
}
