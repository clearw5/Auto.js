package com.stardust.automator.simple_action;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
import com.stardust.util.SparseArrayEntries;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface Able {

    SparseArray<Able> ABLE_MAP = new SparseArrayEntries<Able>()
            .entry(AccessibilityNodeInfo.ACTION_CLICK, new Able() {
                @Override
                public boolean isAble(UiObject nodeInfo) {
                    return nodeInfo.isClickable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_LONG_CLICK, new Able() {
                @Override
                public boolean isAble(UiObject nodeInfo) {
                    return nodeInfo.isLongClickable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_FOCUS, new Able() {
                @Override
                public boolean isAble(UiObject nodeInfo) {
                    return nodeInfo.isFocusable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, new Able() {
                @Override
                public boolean isAble(UiObject nodeInfo) {
                    return nodeInfo.isScrollable();
                }
            })
            .entry(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, new Able() {
                @Override
                public boolean isAble(UiObject nodeInfo) {
                    return nodeInfo.isScrollable();
                }
            })
            .sparseArray();

    boolean isAble(UiObject node);

}
