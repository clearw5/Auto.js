package com.stardust.autojs.runtime;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityService;

/**
 * Created by Stardust on 2017/4/2.
 */

public abstract class AccessibilityBridge {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_FAST = 1;

    private int mMode = MODE_NORMAL;


    public abstract void ensureServiceEnabled();


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


    public abstract AccessibilityInfoProvider getInfoProvider();


    public void setMode(int mode) {
        mMode = mode;
    }

}
