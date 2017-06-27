package com.stardust.view.accessibility;

import android.support.annotation.CallSuper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/5/2.
 */

public abstract class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private AccessibilityNodeInfo mRootInActiveWindow;

    @CallSuper
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || event.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER
                || event.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_EXIT) {
            mRootInActiveWindow = super.getRootInActiveWindow();
        }
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        try {
            return super.getRootInActiveWindow();
        } catch (IllegalStateException ignored) {
            return mRootInActiveWindow;
        }
    }


}
