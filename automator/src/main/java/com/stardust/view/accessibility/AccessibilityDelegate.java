package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/2/14.
 */

public interface AccessibilityDelegate {

    boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event);

}
