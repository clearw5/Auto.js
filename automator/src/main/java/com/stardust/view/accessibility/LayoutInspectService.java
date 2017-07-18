package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/7/13.
 */

public class LayoutInspectService extends AccessibilityService {

    private static LayoutInspectService instance;

    public static LayoutInspectService getInstance() {
        return instance;
    }


    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(info);
        super.onServiceConnected();
    }
}

