package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class PackageNameFilter {

    private static final KeyGetter PACKAGE_NAME_GETTER = new KeyGetter() {
        @Override
        public String getKey(AccessibilityNodeInfo nodeInfo) {
            CharSequence charSequence = nodeInfo.getPackageName();
            return charSequence == null ? null : charSequence.toString();
        }
    };

    public static ListFilter equals(String text) {
        return new StringEqualsFilter(text, PACKAGE_NAME_GETTER);
    }

    public static ListFilter contains(String str) {
        return new StringContainsFilter(str, PACKAGE_NAME_GETTER);
    }

    public static ListFilter startsWith(String prefix) {
        return new StringStartsWithFilter(prefix, PACKAGE_NAME_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new StringEndsWithFilter(suffix, PACKAGE_NAME_GETTER);
    }

    public static ListFilter matches(String regex) {
        return new StringMatchesFilter(regex, PACKAGE_NAME_GETTER);
    }

    private PackageNameFilter() {

    }
}
