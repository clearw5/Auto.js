package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public interface ListFilter {

    List<AccessibilityNodeInfo> filter(AccessibilityNodeInfoAllocator allocator, List<AccessibilityNodeInfo> nodes);

    abstract class Default implements Filter, ListFilter {

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfoAllocator allocator, List<AccessibilityNodeInfo> nodes) {
            List<AccessibilityNodeInfo> list = new ArrayList<>();
            for (AccessibilityNodeInfo node : nodes) {
                list.addAll(filter(allocator, node));
            }
            return list;
        }
    }

}
