package com.stardust.scriptdroid.layout_inspector;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.view.accessibility.AccessibilityDelegate;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutInspector implements AccessibilityDelegate {

    private NodeInfo mCapture;

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        return false;
    }

    public NodeInfo captureCurrentWindow() {
        AccessibilityService service = AccessibilityWatchDogService.getInstance();
        if (service == null) {
            mCapture = null;
        } else {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                mCapture = null;
            } else {
                mCapture = NodeInfo.capture(root);
            }
        }
        return mCapture;
    }


    public void clearCapture() {
        mCapture = null;
    }

    public NodeInfo getCapture() {
        return mCapture;
    }
}
