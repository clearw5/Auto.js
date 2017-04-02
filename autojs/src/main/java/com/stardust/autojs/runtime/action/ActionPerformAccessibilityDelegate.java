package com.stardust.autojs.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformAccessibilityDelegate implements AccessibilityDelegate {

    private static final String TAG = "ActionPerformDelegate";

    private final Queue<Action> mActions = new LinkedList<>();
    private Action mCurrentAction;

    public void addAction(Action action) {
        synchronized (mActions) {
            mActions.offer(action);
        }
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        synchronized (mActions) {
            mCurrentAction = getCurrentAction();
            if (mCurrentAction == null)
                return false;
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                return false;
            }
            performAction(root, mCurrentAction);
            return false;
        }
    }

    private Action getCurrentAction() {
        if (isCurrentActionValid()) {
            return mCurrentAction;
        }
        if (mActions.isEmpty())
            return null;
        return mActions.poll();
    }

    private boolean isCurrentActionValid() {
        return mCurrentAction != null && mCurrentAction.isValid();
    }

    private void performAction(final AccessibilityNodeInfo root, final Action action) {
        Log.i(TAG, "perform action:" + action);
        if (action.perform(root)) {
            action.setResult(true);
            onActionPerformed(action);
        }
    }


    private void onActionPerformed(Action action) {
        mCurrentAction = null;
        synchronized (action) {
            action.notify();
        }
    }


}

