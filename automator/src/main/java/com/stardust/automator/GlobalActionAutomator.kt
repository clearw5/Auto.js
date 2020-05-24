package com.stardust.automator

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import android.view.ViewConfiguration

import com.stardust.concurrent.VolatileBox
import com.stardust.concurrent.VolatileDispose
import com.stardust.util.ScreenMetrics

/**
 * Created by Stardust on 2017/5/16.
 */

class GlobalActionAutomator(private val mHandler: Handler?, private val serviceProvider: () -> AccessibilityService) {

    private val service: AccessibilityService
        get() = serviceProvider()

    private var mScreenMetrics: ScreenMetrics? = null

    fun setScreenMetrics(screenMetrics: ScreenMetrics?) {
        mScreenMetrics = screenMetrics
    }

    fun back(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    fun home(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun powerDialog(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
    }

    private fun performGlobalAction(globalAction: Int): Boolean {
        return service.performGlobalAction(globalAction)
    }

    fun notifications(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
    }

    fun quickSettings(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
    }

    fun recents(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun splitScreen(): Boolean {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gesture(start: Long, duration: Long, vararg points: IntArray): Boolean {
        val path = pointsToPath(points)
        return gestures(GestureDescription.StrokeDescription(path, start, duration))
    }

    private fun pointsToPath(points: Array<out IntArray>): Path {
        val path = Path()
        path.moveTo(scaleX(points[0][0]).toFloat(), scaleY(points[0][1]).toFloat())
        for (i in 1 until points.size) {
            val point = points[i]
            path.lineTo(scaleX(point[0]).toFloat(), scaleY(point[1]).toFloat())
        }
        return path
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gestureAsync(start: Long, duration: Long, vararg points: IntArray) {
        val path = pointsToPath(points)
        gesturesAsync(GestureDescription.StrokeDescription(path, start, duration))
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gestures(vararg strokes: GestureDescription.StrokeDescription): Boolean {
        val builder = GestureDescription.Builder()
        for (stroke in strokes) {
            builder.addStroke(stroke)
        }
        val handler = mHandler
        return if (handler == null) {
            gesturesWithoutHandler(builder.build())
        } else {
            gesturesWithHandler(handler, builder.build())
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun gesturesWithHandler(handler: Handler, description: GestureDescription): Boolean {
        val result = VolatileDispose<Boolean>()
        service.dispatchGesture(description, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                result.setAndNotify(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                result.setAndNotify(false)
            }
        }, handler)
        return result.blockedGet()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun gesturesWithoutHandler(description: GestureDescription): Boolean {
        prepareLooperIfNeeded()
        val result = VolatileBox(false)
        val handler = Handler(Looper.myLooper())
        service.dispatchGesture(description, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                result.set(true)
                quitLoop()
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                result.set(false)
                quitLoop()
            }
        }, handler)
        Looper.loop()
        return result.get()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun gesturesAsync(vararg strokes: GestureDescription.StrokeDescription) {
        val builder = GestureDescription.Builder()
        for (stroke in strokes) {
            builder.addStroke(stroke)
        }
        service.dispatchGesture(builder.build(), null, null)
    }

    private fun quitLoop() {
        val looper = Looper.myLooper()
        looper?.quit()
    }

    private fun prepareLooperIfNeeded() {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun click(x: Int, y: Int): Boolean {
        return press(x, y, ViewConfiguration.getTapTimeout() + 50)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun press(x: Int, y: Int, delay: Int): Boolean {
        return gesture(0, delay.toLong(), intArrayOf(x, y))
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun longClick(x: Int, y: Int): Boolean {
        return gesture(0, (ViewConfiguration.getLongPressTimeout() + 200).toLong(), intArrayOf(x, y))
    }

    private fun scaleX(x: Int): Int {
        return mScreenMetrics?.scaleX(x) ?: x
    }

    private fun scaleY(y: Int): Int {
        return mScreenMetrics?.scaleX(y) ?: y
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int, delay: Long): Boolean {
        return gesture(0, delay, intArrayOf(x1, y1), intArrayOf(x2, y2))
    }

}
