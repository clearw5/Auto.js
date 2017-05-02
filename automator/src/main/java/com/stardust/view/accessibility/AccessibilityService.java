package com.stardust.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/5/2.
 */

public abstract class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        try {
            return super.getRootInActiveWindow();
        } catch (Exception ignored) {
            return null;
        }
    }
}
