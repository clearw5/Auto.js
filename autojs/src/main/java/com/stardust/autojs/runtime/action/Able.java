package com.stardust.autojs.runtime.action;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.util.SparseArrayEntries;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface Able {

    SparseArray<Able> ABLE_MAP = new SparseArrayEntries<Able>()
            .entry(AccessibilityNodeInfo.ACTION_CLICK, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo nodeInfo) {
                    return nodeInfo.isClickable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_LONG_CLICK, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo nodeInfo) {
                    return nodeInfo.isLongClickable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_FOCUS, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo nodeInfo) {
                    return nodeInfo.isFocusable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo nodeInfo) {
                    return nodeInfo.isScrollable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo nodeInfo) {
                    return nodeInfo.isScrollable();
                }
            })
            .sparseArray();

    boolean isAble(AccessibilityNodeInfo node);

}
