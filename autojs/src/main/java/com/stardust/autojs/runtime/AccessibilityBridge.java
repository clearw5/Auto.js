package com.stardust.autojs.runtime;

import android.support.annotation.Nullable;

import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityService;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface AccessibilityBridge {

    void ensureServiceEnabled();

    AccessibilityInfoProvider getInfoProvider();


    @Nullable
    AccessibilityService getService();


}
