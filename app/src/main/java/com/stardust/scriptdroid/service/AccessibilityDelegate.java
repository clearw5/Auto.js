package com.stardust.scriptdroid.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Stardust on 2017/2/14.
 */

public interface AccessibilityDelegate {

    boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event);

}
