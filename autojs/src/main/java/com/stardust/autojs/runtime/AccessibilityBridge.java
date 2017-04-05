package com.stardust.autojs.runtime;

import android.accessibilityservice.AccessibilityService;
import android.support.annotation.Nullable;

import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.automator.simple_action.SimpleActionPerformHost;
import com.stardust.view.accessibility.AccessibilityInfoProvider;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface AccessibilityBridge {

    void ensureServiceEnabled();

    AccessibilityInfoProvider getInfoProvider();

    AccessibilityEventCommandHost getCommandHost();

    SimpleActionPerformHost getActionPerformHost();

    @Nullable
    AccessibilityService getService();


}
