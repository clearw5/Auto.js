package com.stardust.scriptdroid.droid.runtime.action;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.util.SparseArrayEntries;

/**
 * Created by Stardust on 2017/1/27.
 */

@FunctionalInterface
public interface Able {

    SparseArray<Able> ABLE_MAP = new SparseArrayEntries<Able>()
            .entry(AccessibilityNodeInfo.ACTION_CLICK, AccessibilityNodeInfo::isClickable)
            .entry(AccessibilityNodeInfo.ACTION_LONG_CLICK, AccessibilityNodeInfo::isLongClickable)
            .entry(AccessibilityNodeInfo.ACTION_FOCUS, AccessibilityNodeInfo::isFocusable)
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, AccessibilityNodeInfo::isScrollable)
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, AccessibilityNodeInfo::isScrollable)
            .sparseArray();

    boolean isAble(AccessibilityNodeInfo node);

}
