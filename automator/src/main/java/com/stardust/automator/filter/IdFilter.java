package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public class IdFilter {

    public interface FullIdGetter {
        String getFullId(String id);
    }

    private static final KeyGetter ID_GETTER = new KeyGetter() {

        @Override
        public String getKey(UiObject nodeInfo) {
            return nodeInfo.getViewIdResourceName();
        }

        @Override
        public String toString() {
            return "id";
        }
    };

    public static StringEqualsFilter equals(String id) {
        return new StringEqualsFilter(id, ID_GETTER);
    }
    public static StringStartsWithFilter startsWith(String prefix) {
        return new StringStartsWithFilter(prefix, ID_GETTER);
    }

    public static ListFilter endsWith(String suffix) {
        return new StringEndsWithFilter(suffix, ID_GETTER);
    }

    public static StringContainsFilter contains(String contains) {
        return new StringContainsFilter(contains, ID_GETTER);
    }

    public static StringMatchesFilter matches(String regex) {
        return new StringMatchesFilter(regex, ID_GETTER);
    }

}
