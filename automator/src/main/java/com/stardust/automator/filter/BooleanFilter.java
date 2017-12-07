package com.stardust.automator.filter;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.stardust.automator.UiObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/9.
 */

public class BooleanFilter extends DfsFilter {

    private static Map<BooleanSupplier, BooleanFilter[]> cache = new HashMap<>();


    public static BooleanFilter get(BooleanSupplier supplier, boolean b) {
        BooleanFilter[] booleanFilters = cache.get(supplier);
        if (booleanFilters == null) {
            booleanFilters = new BooleanFilter[2];
            cache.put(supplier, booleanFilters);
        }
        int i = b ? 1 : 0;
        if (booleanFilters[i] == null) {
            booleanFilters[i] = new BooleanFilter(supplier, b);
        }
        return booleanFilters[i];
    }

    public interface BooleanSupplier {

        boolean get(UiObject node);

    }

    public static final BooleanSupplier CHECKABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isCheckable();
        }

        @Override
        public String toString() {
            return "checkable";
        }
    };

    public static final BooleanSupplier CHECKED = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isChecked();
        }

        @Override
        public String toString() {
            return "checked";
        }
    };

    public static final BooleanSupplier FOCUSABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isFocusable();
        }

        @Override
        public String toString() {
            return "focusable";
        }
    };

    public static final BooleanSupplier FOCUSED = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isFocused();
        }

        @Override
        public String toString() {
            return "focused";
        }
    };

    public static final BooleanSupplier VISIBLE_TO_USER = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isVisibleToUser();
        }

        @Override
        public String toString() {
            return "visibleToUser";
        }
    };

    public static final BooleanSupplier ACCESSIBILITY_FOCUSED = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isAccessibilityFocused();
        }

        @Override
        public String toString() {
            return "accessibilityFocused";
        }
    };

    public static final BooleanSupplier SELECTED = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isSelected();
        }

        @Override
        public String toString() {
            return "selected";
        }
    };

    public static final BooleanSupplier CLICKABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isClickable();
        }

        @Override
        public String toString() {
            return "clickable";
        }
    };

    public static final BooleanSupplier LONG_CLICKABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isLongClickable();
        }

        @Override
        public String toString() {
            return "longClickable";
        }
    };

    public static final BooleanSupplier ENABLED = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isEnabled();
        }

        @Override
        public String toString() {
            return "enabled";
        }
    };


    public static final BooleanSupplier PASSWORD = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isPassword();
        }

        @Override
        public String toString() {
            return "password";
        }
    };

    public static final BooleanSupplier SCROLLABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isScrollable();
        }

        @Override
        public String toString() {
            return "scrollable";
        }
    };

    public static final BooleanSupplier EDITABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isEditable();
        }

        @Override
        public String toString() {
            return "editable";
        }
    };
    public static final BooleanSupplier CONTENT_INVALID = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isContentInvalid();
        }

        @Override
        public String toString() {
            return "contentInvalid";
        }
    };

    public static final BooleanSupplier CONTEXT_CLICKABLE = new BooleanSupplier() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean get(UiObject node) {
            return node.isContextClickable();
        }

        @Override
        public String toString() {
            return "checkable";
        }
    };

    public static final BooleanSupplier MULTI_LINE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isMultiLine();
        }

        @Override
        public String toString() {
            return "multiLine";
        }
    };

    public static final BooleanSupplier DISMISSABLE = new BooleanSupplier() {

        @Override
        public boolean get(UiObject node) {
            return node.isDismissable();
        }

        @Override
        public String toString() {
            return "dismissable";
        }
    };

    private BooleanSupplier mBooleanSupplier;
    private boolean mExceptedValue;

    public BooleanFilter(BooleanSupplier booleanSupplier, boolean exceptedValue) {
        mBooleanSupplier = booleanSupplier;
        mExceptedValue = exceptedValue;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        return nodeInfo != null && mBooleanSupplier.get(nodeInfo) == mExceptedValue;
    }

    @Override
    public String toString() {
        return mBooleanSupplier.toString() + "(" + mExceptedValue + ")";
    }
}
