package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.view.accessibility.AccessibilityDelegate;

/**
 * Created by Stardust on 2017/3/9.
 */

public class AccessibilityInfoProvider implements AccessibilityDelegate {


    private String mLatestPackage = "";
    private String mLatestActivity = "";
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
        setLatestComponent(event.getPackageName(), event.getClassName());
        return false;
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
