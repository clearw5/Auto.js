package com.stardust.scriptdroid.layout_inspector;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityDelegate;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutInspector implements AccessibilityDelegate {

    private AccessibilityNodeInfo mRootInActiveWindow;
    private NodeInfo mCapture;

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        mRootInActiveWindow = service.getRootInActiveWindow();
        return false;
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return mRootInActiveWindow;
    }

    public NodeInfo captureCurrentWindow() {
        if (mRootInActiveWindow == null) {
            mCapture = null;
        } else {
            mCapture = NodeInfo.capture(mRootInActiveWindow);
        }
        return mCapture;
    }


    public void clearCapture() {
        mRootInActiveWindow = null;
        mCapture = null;
    }

    public NodeInfo getCapture() {
        return mCapture;
    }
}
