package org.autojs.autojs.accessibility;


import android.accessibilityservice.AccessibilityServiceInfo;

import org.autojs.autojs.Pref;

/**
 * Created by Stardust on 2017/8/14.
 */

public class AccessibilityService extends com.stardust.view.accessibility.AccessibilityService {

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        if (Pref.isStableModeEnabled()) {
            serviceInfo.flags &= ~AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        } else {
            serviceInfo.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        }
        setServiceInfo(serviceInfo);
        super.onServiceConnected();
    }
}
