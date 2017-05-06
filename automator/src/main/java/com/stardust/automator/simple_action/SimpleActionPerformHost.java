package com.stardust.automator.simple_action;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by Stardust on 2017/1/21.
 */

public class SimpleActionPerformHost implements AccessibilityDelegate {

    private static final String TAG = "ActionPerformDelegate";

    private BlockingQueue<SimpleAction> mSimpleAction = new ArrayBlockingQueue<>(1);
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public void addToActionQueueAndWait(SimpleAction simpleAction) throws InterruptedException {
        mSimpleAction.put(simpleAction);
        synchronized (simpleAction) {
            simpleAction.wait();
        }
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        SimpleAction action = mSimpleAction.poll();
        if (action == null)
            return false;
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) {
            return false;
        }
        performAction(root, action);
        return false;
    }

    @Override
    public Set<Integer> getEventTypes() {
        return null;
    }

    private void performAction(AccessibilityNodeInfo root, SimpleAction simpleAction) {
        mExecutor.execute(new SimpleActionPerformRunnable(root, simpleAction));
    }


    private synchronized void onActionPerformed(SimpleAction simpleAction) {
        synchronized (simpleAction) {
            simpleAction.notify();
        }
    }


    private class SimpleActionPerformRunnable implements Runnable {

        private final SimpleAction mSimpleAction;
        private final AccessibilityNodeInfo mRoot;

        SimpleActionPerformRunnable(AccessibilityNodeInfo root, SimpleAction simpleAction) {
            mRoot = root;
            mSimpleAction = simpleAction;
        }

        @Override
        public void run() {
            if (!mSimpleAction.isValid()) {
                return;
            }
            Log.i(TAG, "perform simpleAction: " + mSimpleAction);
            if (mSimpleAction.perform(UiObject.createRoot(mRoot))) {
                mSimpleAction.setResult(true);
                onActionPerformed(mSimpleAction);
            }
        }
    }

}

