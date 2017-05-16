package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Stardust on 2017/3/9.
 */

public class AccessibilityInfoProvider implements AccessibilityDelegate {


    private volatile String mLatestPackage = "";
    private volatile String mLatestActivity = "";
    private PackageManager mPackageManager;

    public AccessibilityInfoProvider(PackageManager packageManager) {
        mPackageManager = packageManager;
    }

    public String getLatestPackage() {
        return mLatestPackage;
    }

    public String getLatestActivity() {
        return mLatestActivity;
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            setLatestComponent(event.getPackageName(), event.getClassName());
        }
        return false;
    }

    @Override
    public Set<Integer> getEventTypes() {
        return ALL_EVENT_TYPES;
    }

    private void setLatestComponent(CharSequence latestPackage, CharSequence latestClass) {
        if (latestPackage == null || latestClass == null)
            return;
        String latestPackageStr = latestPackage.toString();
        String latestClassStr = latestClass.toString();
        if (latestClassStr.startsWith("android.view.") || latestClassStr.startsWith("android.widget."))
            return;
        try {
            ComponentName componentName = new ComponentName(latestPackageStr, latestClassStr);
            mLatestActivity = mPackageManager.getActivityInfo(componentName, 0).name;
        } catch (PackageManager.NameNotFoundException ignored) {
            return;
        }
        mLatestPackage = latestPackage.toString();
    }
}
