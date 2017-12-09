package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/9.
 */

public class ClassNameFilter {

    private static final KeyGetter CLASS_NAME_GETTER = new KeyGetter() {
        @Override
        public String getKey(UiObject nodeInfo) {
            CharSequence charSequence = nodeInfo.getClassName();
            return charSequence == null ? null : charSequence.toString();
        }

        @Override
        public String toString() {
            return "className";
        }
    };

    public static ListFilter equals(String text) {
        if (!text.contains(".")) {
            text = "android.widget." + text;
        }
        return new StringEqualsFilter(text, CLASS_NAME_GETTER);
    }

    public static ListFilter contains(String str) {
        return new StringContainsFilter(str, CLASS_NAME_GETTER);
    }

    public static ListFilter startsWith(String prefix) {
        return new StringStartsWithFilter(prefix, CLASS_NAME_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new StringEndsWithFilter(suffix, CLASS_NAME_GETTER);
    }

    public static ListFilter matches(String regex) {
        return new StringMatchesFilter(regex, CLASS_NAME_GETTER);
    }

    private ClassNameFilter() {

    }
}
