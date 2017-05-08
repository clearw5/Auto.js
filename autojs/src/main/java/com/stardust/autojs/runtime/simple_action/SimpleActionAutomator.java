package com.stardust.autojs.runtime.simple_action;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.AbstractScriptRuntime;
import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptInterface;
import com.stardust.autojs.runtime.api.AutomatorConfig;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.automator.UiObject;
import com.stardust.automator.simple_action.SimpleAction;
import com.stardust.automator.simple_action.ActionFactory;
import com.stardust.automator.simple_action.ActionTarget;
import com.stardust.util.DeveloperUtils;

/**
 * Created by Stardust on 2017/4/2.
 */

public class SimpleActionAutomator {

    private static final String TAG = "SimpleActionAutomator";

    @Deprecated
    private static class PerformGlobalActionCommand extends AccessibilityEventCommandHost.AbstractCommand {

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
    private AbstractScriptRuntime mScriptRuntime;

    public SimpleActionAutomator(AccessibilityBridge accessibilityBridge, AbstractScriptRuntime scriptRuntime) {
        mAccessibilityBridge = accessibilityBridge;
        mScriptRuntime = scriptRuntime;
    }

    @ScriptInterface
    public ActionTarget text(String text, int i) {
        return new ActionTarget.TextActionTarget(text, i);
    }

    @ScriptInterface
    public ActionTarget bounds(int left, int top, int right, int bottom) {
        return new ActionTarget.BoundsActionTarget(new Rect(left, top, right, bottom));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ScriptInterface
    public ActionTarget editable(int i) {
        mScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return new ActionTarget.EditableActionTarget(i);
    }

    @ScriptInterface
    public ActionTarget id(String id) {
        return new ActionTarget.IdActionTarget(id);
    }

    @ScriptInterface
    public boolean click(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_CLICK));
    }

    @ScriptInterface
    public boolean longClick(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_LONG_CLICK));
    }

    @ScriptInterface
    public boolean scrollUp(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    @ScriptInterface
    public boolean scrollDown(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    @ScriptInterface
    public boolean scrollBackward(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, i));
    }

    @ScriptInterface
    public boolean scrollForward(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, i));
    }

    @ScriptInterface
    public boolean scrollMaxBackward() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    @ScriptInterface
    public boolean scrollMaxForward() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    @ScriptInterface
    public boolean focus(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_FOCUS));
    }

    @ScriptInterface
    public boolean select(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SELECT));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean setText(ActionTarget target, String text) {
        mScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text));
    }

    @ScriptInterface
    public boolean back() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @ScriptInterface
    public boolean home() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean powerDialog() {
        mScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
    }

    @ScriptInterface
    public boolean notifications() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    @ScriptInterface
    public boolean quickSettings() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
    }

    @ScriptInterface
    public boolean recents() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean splitScreen() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
    }

    private boolean performGlobalAction(final int action) {
        ensureAccessibilityServiceEnabled();
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service == null)
            return false;
        return service.performGlobalAction(action);
    }

    @ScriptInterface
    public boolean paste(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE));
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    @SuppressWarnings("unchecked")
    private boolean performAction(SimpleAction simpleAction) {
        ensureAccessibilityServiceEnabled();
        if (AutomatorConfig.isUnintendedGuardEnabled() && isRunningPackageSelf()) {
            Log.d(TAG, "performAction: running package is self. return false");
            return false;
        }
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service == null)
            return false;
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null)
            return false;
        Log.v(TAG, "performAction: " + simpleAction + " root = " + root);
        return simpleAction.perform(UiObject.createRoot(root));
    }

    private boolean isRunningPackageSelf() {
        return DeveloperUtils.isSelfPackage(mAccessibilityBridge.getInfoProvider().getLatestPackage());
    }
}
