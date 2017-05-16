package com.stardust.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/5/2.
 */

public abstract class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    public interface NotificationCallback {
        void onNotification();
    }

    private CopyOnWriteArrayList<NotificationCallback> mNotificationCallbacks = new CopyOnWriteArrayList<>();

    @Override

    public AccessibilityNodeInfo getRootInActiveWindow() {
        try {
            return super.getRootInActiveWindow();
        } catch (Exception ignored) {
            return null;
        }
    }

}
