package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/3/9.
 */

public class ClassNameFilter {

    private static final KeyGetter CLASS_NAME_GETTER = new KeyGetter() {
        @Override
        public String getKey(AccessibilityNodeInfo nodeInfo) {
            CharSequence charSequence = nodeInfo.getClassName();
            return charSequence == null ? null : charSequence.toString();
        }
    };

    public static ListFilter equals(String text) {
        if (!text.contains(".")) {
            text = "android.widget." + text;
        }
        return new EqualsFilter(text, CLASS_NAME_GETTER);
    }

    public static ListFilter contains(String str) {
        return new ContainsFilter(str, CLASS_NAME_GETTER);
    }

    public static ListFilter startsWith(String prefix) {
        return new StartsWithFilter(prefix, CLASS_NAME_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new EndsWithFilter(suffix, CLASS_NAME_GETTER);
    }

    public static ListFilter matches(String regex) {
        return new MatchesFilter(regex, CLASS_NAME_GETTER);
    }

    private ClassNameFilter() {

    }
}
