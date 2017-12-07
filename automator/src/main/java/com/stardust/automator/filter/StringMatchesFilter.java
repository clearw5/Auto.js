package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StringMatchesFilter extends DfsFilter {

    private final String mRegex;
    private final KeyGetter mKeyGetter;

    StringMatchesFilter(String regex, KeyGetter keyGetter) {
        mRegex = regex;
        mKeyGetter = keyGetter;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        return key != null && key.matches(mRegex);
    }

    @Override
    public String toString() {
        return mKeyGetter.toString() + "Matches(\"" + mRegex + "\")";
    }
}
