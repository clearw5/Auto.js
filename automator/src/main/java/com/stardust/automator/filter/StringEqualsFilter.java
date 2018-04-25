package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class StringEqualsFilter extends DfsFilter {

    private String mValue;
    private KeyGetter mKeyGetter;

    public StringEqualsFilter(String value, KeyGetter getter) {
        mValue = value;
        mKeyGetter = getter;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        String key = mKeyGetter.getKey(nodeInfo);
        if (key != null) {
            return key.equals(mValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return mKeyGetter.toString() + "(\"" + mValue + "\")";
    }
}
