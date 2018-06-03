package com.stardust.autojs.core.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;
import com.stardust.automator.GlobalActionAutomator;
import com.stardust.automator.UiObject;
import com.stardust.automator.simple_action.ActionFactory;
import com.stardust.automator.simple_action.ActionTarget;
import com.stardust.automator.simple_action.SimpleAction;
import com.stardust.util.DeveloperUtils;
import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/4/2.
 */

public class SimpleActionAutomator {

    private static final String TAG = "SimpleActionAutomator";

    private AccessibilityBridge mAccessibilityBridge;
    private ScriptRuntime mScriptRuntime;
    private GlobalActionAutomator mGlobalActionAutomator;
    private ScreenMetrics mScreenMetrics;

    public SimpleActionAutomator(AccessibilityBridge accessibilityBridge, ScriptRuntime scriptRuntime) {
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
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
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

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean setText(ActionTarget target, String text) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text));
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean appendText(ActionTarget target, String text) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return performAction(target.createAction(UiObject.ACTION_APPEND_TEXT, text));
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
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
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

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gesture(long start, long duration, int[]... points) {
        prepareForGesture();
        return mGlobalActionAutomator.gesture(start, duration, points);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gestureAsync(long start, long duration, int[]... points) {
        prepareForGesture();
        mGlobalActionAutomator.gestureAsync(start, duration, points);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gestures(Object strokes) {
        prepareForGesture();
        return mGlobalActionAutomator.gestures((GestureDescription.StrokeDescription[]) strokes);
    }

    //如果这里用GestureDescription.StrokeDescription[]为参数，安卓7.0以下会因为找不到这个类而报错
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gesturesAsync(Object strokes) {
        prepareForGesture();
        mGlobalActionAutomator.gesturesAsync((GestureDescription.StrokeDescription[]) strokes);
    }

    private void prepareForGesture() {
        ScriptRuntime.requiresApi(24);
        ensureAccessibilityServiceEnabled();
        if (mGlobalActionAutomator != null)
            return;
        mGlobalActionAutomator = new GlobalActionAutomator(new Handler(mScriptRuntime.loopers.getServantLooper()));
        mGlobalActionAutomator.setScreenMetrics(mScreenMetrics);
        mGlobalActionAutomator.setService(mAccessibilityBridge.getService());
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean click(int x, int y) {
        prepareForGesture();
        return mGlobalActionAutomator.click(x, y);
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean press(int x, int y, int delay) {
        prepareForGesture();
        return mGlobalActionAutomator.press(x, y, delay);
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean longClick(int x, int y) {
        prepareForGesture();
        return mGlobalActionAutomator.longClick(x, y);
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean swipe(int x1, int y1, int x2, int y2, int delay) {
        prepareForGesture();
        return mGlobalActionAutomator.swipe(x1, y1, x2, y2, delay);
    }

    private boolean performGlobalAction(final int action) {
        ensureAccessibilityServiceEnabled();
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service == null)
            return false;
        return service.performGlobalAction(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @ScriptInterface
    public boolean paste(ActionTarget target) {
        ScriptRuntime.requiresApi(18);
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE));
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }


    @SuppressWarnings("unchecked")
    private boolean performAction(SimpleAction simpleAction) {
        ensureAccessibilityServiceEnabled();
        if (AccessibilityConfig.isUnintendedGuardEnabled() && isRunningPackageSelf()) {
            Log.d(TAG, "performAction: running package is self. return false");
            return false;
        }
        AccessibilityNodeInfo root = mAccessibilityBridge.getRootInActiveWindow();
        if (root == null)
            return false;
        Log.v(TAG, "performAction: " + simpleAction + " root = " + root);
        return simpleAction.perform(UiObject.createRoot(root));
    }

    private boolean isRunningPackageSelf() {
        return DeveloperUtils.isSelfPackage(mAccessibilityBridge.getInfoProvider().getLatestPackage());
    }

    public void setScreenMetrics(ScreenMetrics metrics) {
        mScreenMetrics = metrics;
        if (mGlobalActionAutomator != null)
            mGlobalActionAutomator.setScreenMetrics(metrics);
    }

}
