package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class PackageNameFilter {

    private static final KeyGetter PACKAGE_NAME_GETTER = new KeyGetter() {
        @Override
        public String getKey(UiObject nodeInfo) {
            CharSequence charSequence = nodeInfo.getPackageName();
            return charSequence == null ? null : charSequence.toString();
        }

        @Override
        public String toString() {
            return "packageName";
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
