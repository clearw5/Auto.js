package com.stardust.autojs.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.automator.AccessibilityEventCommandHost;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ActionAutomator {

    private static class PerformGlobalActionCommand implements AccessibilityEventCommandHost.Command {

        boolean result;
        private int mGlobalAction;

        PerformGlobalActionCommand(int globalAction) {
            mGlobalAction = globalAction;
        }

        @Override
        public void execute(AccessibilityService service, AccessibilityEvent event) {
            result = service.performGlobalAction(mGlobalAction);
        }
    }

    private AccessibilityBridge mAccessibilityBridge;
    private ScriptRuntime mScriptRuntime;

    public ActionAutomator(AccessibilityBridge accessibilityBridge, ScriptRuntime scriptRuntime) {
        mAccessibilityBridge = accessibilityBridge;
        mScriptRuntime = scriptRuntime;
    }

    public ActionTarget text(String text, int i) {
        return new ActionTarget.TextActionTarget(text, i);
    }

    public ActionTarget bounds(int left, int top, int right, int bottom) {
        return new ActionTarget.BoundsActionTarget(new Rect(left, top, right, bottom));
    }

    public ActionTarget editable(int i) {
        return new ActionTarget.EditableActionTarget(i);
    }

    public ActionTarget id(String id) {
        return new ActionTarget.IdActionTarget(id);
    }

    public boolean click(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_CLICK));
    }

    public boolean longClick(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_LONG_CLICK));
    }

    public boolean scrollUp(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollDown(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    public boolean scrollUp(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, i));
    }

    public boolean scrollDown(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, i));
    }

    public boolean scrollAllUp() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollAllDown() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    public boolean focus(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_FOCUS));
    }

    public boolean select(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SELECT));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean setText(ActionTarget target, String text) {
        ensureApi(Build.VERSION_CODES.LOLLIPOP);
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text));
    }

    public boolean back() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public boolean home() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean powerDialog() {
        ensureApi(Build.VERSION_CODES.LOLLIPOP);
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
    }

    private void ensureApi(int i) {
        if (Build.VERSION.SDK_INT < i) {
            throw new ScriptStopException();
        }
    }

    public boolean notifications() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    public boolean quickSettings() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
    }

    public boolean recents() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean splitScreen() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
    }

    public boolean swipeDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN);
    }

    public boolean swipeDownLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT);
    }

    public boolean swipeDownRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT);
    }

    public boolean swipeDownUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP);
    }

    public boolean swipeUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP);
    }

    public boolean swipeUpLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT);
    }

    public boolean swipeUpRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT);
    }

    public boolean swipeUpDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN);
    }

    public boolean swipeLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);
    }

    public boolean swipeLeftRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_RIGHT);
    }

    public boolean swipeLeftUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP);
    }

    public boolean swipeLeftDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_DOWN);
    }

    public boolean swipeRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT);
    }

    public boolean swipeRightLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_LEFT);
    }

    public boolean swipeRightUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP);
    }

    public boolean swipeRightDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN);
    }

    private boolean performGlobalAction(final int action) {
        ensureAccessibilityServiceEnabled();
        PerformGlobalActionCommand command = new PerformGlobalActionCommand(action);
        mAccessibilityBridge.getCommandHost().executeAndWaitForEvent(command);
        return command.result;
    }

    public boolean paste(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE));
    }


    public String currentPackage() {
        ensureAccessibilityServiceEnabled();
        return mAccessibilityBridge.getInfoProvider().getLatestPackage();
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    public String currentActivity() {
        ensureAccessibilityServiceEnabled();
        return mAccessibilityBridge.getInfoProvider().getLatestActivity();
    }

    private <T> T performAction(Action action) {
        ensureAccessibilityServiceEnabled();
        mAccessibilityBridge.getActionPerformHost().addAction(action);
        synchronized (action) {
            try {
                action.wait();
            } catch (InterruptedException e) {
                action.setValid(false);
                mScriptRuntime.stoppedByInterrupted(e);
            }
        }
        return (T) action.getResult();
    }
}
