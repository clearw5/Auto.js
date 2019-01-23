package com.stardust.autojs.core.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.stardust.autojs.annotation.ScriptInterface
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig
import com.stardust.automator.GlobalActionAutomator
import com.stardust.automator.UiObject
import com.stardust.automator.simple_action.ActionFactory
import com.stardust.automator.simple_action.ActionTarget
import com.stardust.automator.simple_action.SimpleAction
import com.stardust.util.DeveloperUtils
import com.stardust.util.ScreenMetrics

/**
 * Created by Stardust on 2017/4/2.
 */

class SimpleActionAutomator(private val mAccessibilityBridge: AccessibilityBridge, private val mScriptRuntime: ScriptRuntime) {

    private lateinit var mGlobalActionAutomator: GlobalActionAutomator

    private var mScreenMetrics: ScreenMetrics? = null

    private val isRunningPackageSelf: Boolean
        get() = DeveloperUtils.isSelfPackage(mAccessibilityBridge.infoProvider.latestPackage)

    @ScriptInterface
    fun text(text: String, i: Int): ActionTarget {
        return ActionTarget.TextActionTarget(text, i)
    }

    @ScriptInterface
    fun bounds(left: Int, top: Int, right: Int, bottom: Int): ActionTarget {
        return ActionTarget.BoundsActionTarget(Rect(left, top, right, bottom))
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ScriptInterface
    fun editable(i: Int): ActionTarget {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP)
        return ActionTarget.EditableActionTarget(i)
    }

    @ScriptInterface
    fun id(id: String): ActionTarget {
        return ActionTarget.IdActionTarget(id)
    }

    @ScriptInterface
    fun click(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_CLICK))
    }

    @ScriptInterface
    fun longClick(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_LONG_CLICK))
    }

    @ScriptInterface
    fun scrollUp(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD))
    }

    @ScriptInterface
    fun scrollDown(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
    }

    @ScriptInterface
    fun scrollBackward(i: Int): Boolean {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, i))
    }

    @ScriptInterface
    fun scrollForward(i: Int): Boolean {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, i))
    }

    @ScriptInterface
    fun scrollMaxBackward(): Boolean {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD))
    }

    @ScriptInterface
    fun scrollMaxForward(): Boolean {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
    }

    @ScriptInterface
    fun focus(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_FOCUS))
    }

    @ScriptInterface
    fun select(target: ActionTarget): Boolean {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SELECT))
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setText(target: ActionTarget, text: String): Boolean {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP)
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text))
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun appendText(target: ActionTarget, text: String): Boolean {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP)
        return performAction(target.createAction(UiObject.ACTION_APPEND_TEXT, text))
    }

    @ScriptInterface
    fun back(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    @ScriptInterface
    fun home(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun powerDialog(): Boolean {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP)
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
    }

    @ScriptInterface
    fun notifications(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
    }

    @ScriptInterface
    fun quickSettings(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
    }

    @ScriptInterface
    fun recents(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun splitScreen(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gesture(start: Long, duration: Long, vararg points: IntArray): Boolean {
        prepareForGesture()
        return mGlobalActionAutomator.gesture(start, duration, *points)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gestureAsync(start: Long, duration: Long, vararg points: IntArray) {
        prepareForGesture()
        mGlobalActionAutomator.gestureAsync(start, duration, *points)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gestures(strokes: Any): Boolean {
        prepareForGesture()
        @Suppress("UNCHECKED_CAST")
        return mGlobalActionAutomator.gestures(*strokes as Array<GestureDescription.StrokeDescription>)
    }

    //如果这里用GestureDescription.StrokeDescription[]为参数，安卓7.0以下会因为找不到这个类而报错
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gesturesAsync(strokes: Any) {
        prepareForGesture()
        @Suppress("UNCHECKED_CAST")
        mGlobalActionAutomator.gesturesAsync(*strokes as Array<GestureDescription.StrokeDescription>)
    }

    private fun prepareForGesture() {
        ScriptRuntime.requiresApi(24)
        if (!::mGlobalActionAutomator.isInitialized) {
            mGlobalActionAutomator = GlobalActionAutomator(Handler(mScriptRuntime.loopers.servantLooper)) {
                ensureAccessibilityServiceEnabled()
                return@GlobalActionAutomator mAccessibilityBridge.service!!
            }
        }
        mGlobalActionAutomator.setScreenMetrics(mScreenMetrics)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun click(x: Int, y: Int): Boolean {
        prepareForGesture()
        return mGlobalActionAutomator.click(x, y)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun press(x: Int, y: Int, delay: Int): Boolean {
        prepareForGesture()
        return mGlobalActionAutomator.press(x, y, delay)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun longClick(x: Int, y: Int): Boolean {
        prepareForGesture()
        return mGlobalActionAutomator.longClick(x, y)
    }

    @ScriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int, delay: Int): Boolean {
        prepareForGesture()
        return mGlobalActionAutomator.swipe(x1, y1, x2, y2, delay.toLong())
    }

    private fun performGlobalAction(action: Int): Boolean {
        ensureAccessibilityServiceEnabled()
        val service = mAccessibilityBridge.service ?: return false
        return service.performGlobalAction(action)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @ScriptInterface
    fun paste(target: ActionTarget): Boolean {
        ScriptRuntime.requiresApi(18)
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE))
    }

    private fun ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled()
    }

    private fun performAction(simpleAction: SimpleAction): Boolean {
        ensureAccessibilityServiceEnabled()
        if (AccessibilityConfig.isUnintendedGuardEnabled() && isRunningPackageSelf) {
            return false
        }
        val roots = mAccessibilityBridge.windowRoots().filter { it != null }
        if (roots.isEmpty())
            return false
        var succeed = true
        for (root in roots) {
            succeed = succeed and simpleAction.perform(UiObject.createRoot(root))
        }
        return succeed
    }

    fun setScreenMetrics(metrics: ScreenMetrics) {
        mScreenMetrics = metrics
    }

}
