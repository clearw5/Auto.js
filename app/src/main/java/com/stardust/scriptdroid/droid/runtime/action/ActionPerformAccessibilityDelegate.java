package com.stardust.scriptdroid.droid.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.service.AccessibilityDelegate;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformAccessibilityDelegate implements AccessibilityDelegate {

    private static final String TAG = "ActionPerformDelegate";

    public static final Action NO_ACTION = null;

    private static Action action;
    private static String latestPackage, latestActivity;


    public static void setAction(Action action) {
        synchronized (ActionPerformAccessibilityDelegate.class) {
            ActionPerformAccessibilityDelegate.action = action;
        }
    }

    public synchronized static String getLatestPackage() {
        return latestPackage;
    }

    public synchronized static String getLatestActivity() {
        return latestActivity;
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        synchronized (ActionPerformAccessibilityDelegate.class) {
            setLatestComponent(event.getPackageName(), event.getClassName());
            if (action == NO_ACTION)
                return false;
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                Log.v(TAG, "root = null");
            }
            Log.i(TAG, "perform action:" + action);
            if (action.perform(root)) {
                action.setResult(true);
                onActionPerformed();
            } else if (!action.performUtilSucceed()) {
                action.setResult(false);
                onActionPerformed();
            }
            return false;
        }
    }


    private void onActionPerformed() {
        synchronized (ActionPerformAccessibilityDelegate.class) {
            action = NO_ACTION;
            DroidRuntime.getRuntime().notifyActionPerformed();
        }
    }

    private static void setLatestComponent(CharSequence latestPackage, CharSequence latestClass) {
        if (latestPackage == null)
            return;
        ActionPerformAccessibilityDelegate.latestPackage = latestPackage.toString();
        if (latestClass == null)
            return;
        try {
            ComponentName componentName = new ComponentName(latestPackage.toString(), latestClass.toString());
            ActivityInfo activityInfo = App.getApp().getPackageManager().getActivityInfo(componentName, 0);
            ActionPerformAccessibilityDelegate.latestActivity = activityInfo.name;
        } catch (PackageManager.NameNotFoundException ignored) {

        }
    }

}

