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

    public synchronized String getLatestPackage() {
        return mLatestPackage;
    }

    public synchronized String getLatestActivity() {
        return mLatestActivity;
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root != null)
            setLatestComponent(root.getPackageName(), event.getClassName());
        else
            setLatestComponent(event.getPackageName(), event.getClassName());
        return false;
    }

    @Override
    public Set<Integer> getEventTypes() {
        return null;
    }

    private synchronized void setLatestComponent(CharSequence latestPackage, CharSequence latestClass) {
        if (latestPackage == null)
            return;
        mLatestPackage = latestPackage.toString();
        if (latestClass == null)
            return;
        try {
            ComponentName componentName = new ComponentName(latestPackage.toString(), latestClass.toString());
            ActivityInfo activityInfo = mPackageManager.getActivityInfo(componentName, 0);
            mLatestActivity = activityInfo.name;
        } catch (PackageManager.NameNotFoundException ignored) {

        }
    }
}
