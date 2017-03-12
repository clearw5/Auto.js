package com.stardust.automator.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public interface Filter {

    List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo node);

}
