package com.stardust.automator.filter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;

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

        boolean get(AccessibilityNodeInfo node);

    }

    public static final BooleanSupplier CHECKABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isCheckable();
        }
    };

    public static final BooleanSupplier CHECKED = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isChecked();
        }
    };

    public static final BooleanSupplier FOCUSABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isFocusable();
        }
    };

    public static final BooleanSupplier FOCUSED = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isFocused();
        }
    };

    public static final BooleanSupplier VISIBLE_TO_USER = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isVisibleToUser();
        }
    };

    public static final BooleanSupplier ACCESSIBILITY_FOCUSED = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isAccessibilityFocused();
        }
    };

    public static final BooleanSupplier SELECTED = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isSelected();
        }
    };

    public static final BooleanSupplier CLICKABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isClickable();
        }
    };

    public static final BooleanSupplier LONG_CLICKABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isLongClickable();
        }
    };

    public static final BooleanSupplier ENABLED = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isEnabled();
        }
    };


    public static final BooleanSupplier PASSWORD = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isPassword();
        }
    };

    public static final BooleanSupplier SCROLLABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isScrollable();
        }
    };

    public static final BooleanSupplier EDITABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isEditable();
        }
    };
    public static final BooleanSupplier CONTENT_INVALID = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isContentInvalid();
        }
    };

    public static final BooleanSupplier CONTEXT_CLICKABLE = new BooleanSupplier() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isContextClickable();
        }
    };

    public static final BooleanSupplier MULTI_LINE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isMultiLine();
        }
    };

    public static final BooleanSupplier DISMISSABLE = new BooleanSupplier() {

        @Override
        public boolean get(AccessibilityNodeInfo node) {
            return node.isDismissable();
        }
    };

    private BooleanSupplier mBooleanSupplier;
    private boolean mExceptedValue;

    public BooleanFilter(BooleanSupplier booleanSupplier, boolean exceptedValue) {
        mBooleanSupplier = booleanSupplier;
        mExceptedValue = exceptedValue;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo != null && mBooleanSupplier.get(nodeInfo) == mExceptedValue;
    }

}
