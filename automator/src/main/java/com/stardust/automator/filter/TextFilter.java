package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public class TextFilter extends ListFilter.Default {

    private static final KeyGetter TEXT_GETTER = new KeyGetter() {
        @Override
        public String getKey(AccessibilityNodeInfo nodeInfo) {
            CharSequence charSequence = nodeInfo.getText();
            return charSequence == null ? null : charSequence.toString();
        }
    };

    public static ListFilter equals(String text) {
        return new EqualsFilter(text, TEXT_GETTER);
    }

    public static ListFilter contains(String str) {
        return new TextFilter(str);
    }

    public static ListFilter startsWith(String prefix) {
        return new StartsWithFilter(prefix, TEXT_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new EndsWithFilter(suffix, TEXT_GETTER);
    }

    public static ListFilter matches(String regex) {
        return new MatchesFilter(regex, TEXT_GETTER);
    }

    private String mText;

    private TextFilter(String text) {
        mText = text;
    }


    @Override
    public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo node) {
        return node.findAccessibilityNodeInfosByText(mText);
    }
}
