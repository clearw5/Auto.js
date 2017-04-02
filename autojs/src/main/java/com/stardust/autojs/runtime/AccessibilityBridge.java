package com.stardust.autojs.runtime;

import com.stardust.autojs.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.view.accessibility.AccessibilityInfoProvider;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface AccessibilityBridge {

    void ensureServiceEnabled();

    AccessibilityInfoProvider getInfoProvider();

    AccessibilityEventCommandHost getCommandHost();

    ActionPerformAccessibilityDelegate getActionPerformHost();


}
