package com.stardust.autojs.core.accessibility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.NotificationListener;

/**
 * Created by Stardust on 2017/4/2.
 */

public abstract class AccessibilityBridge {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_FAST = 1;

    private int mMode = MODE_NORMAL;
    private AccessibilityConfig mConfig;

    public AccessibilityBridge(AccessibilityConfig config) {
        mConfig = config;
        mConfig.seal();
    }

    public abstract void ensureServiceEnabled();

    public abstract void waitForServiceEnabled();

    @Nullable
    public abstract AccessibilityService getService();

    @Nullable
    public AccessibilityNodeInfo getRootInActiveWindow() {
        AccessibilityService service = getService();
        if (service == null)
            return null;
        if (mMode == MODE_FAST) {
            return service.fastRootInActiveWindow();
        }
        return service.getRootInActiveWindow();
    }


    @NonNull
    public abstract AccessibilityInfoProvider getInfoProvider();


    public void setMode(int mode) {
        mMode = mode;
    }

    @NonNull
    public abstract AccessibilityNotificationObserver getNotificationObserver();

    public AccessibilityConfig getConfig() {
        return mConfig;
    }
}
