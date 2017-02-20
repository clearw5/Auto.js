package com.stardust.scriptdroid.droid.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.service.AccessibilityDelegate;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformAccessibilityDelegate implements AccessibilityDelegate {

    private static final String TAG = "ActionPerformDelegate";

    public static final Action NO_ACTION = null;

    private static Action action;

    public static void setAction(Action action) {
        synchronized (ActionPerformAccessibilityDelegate.class) {
            ActionPerformAccessibilityDelegate.action = action;
        }
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
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


    private void onActionPerformed() {
        synchronized (ActionPerformAccessibilityDelegate.class) {
            action = NO_ACTION;
            DroidRuntime.getRuntime().notifyActionPerformed();
        }
    }

}
