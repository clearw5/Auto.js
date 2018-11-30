package com.stardust.view.accessibility

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by Stardust on 2017/3/10.
 */

class LayoutInspector(private val mContext: Context) {
    @Volatile
    var capture: NodeInfo? = null
        private set
    @Volatile
    var isDumping = false
        private set
    private val mExecutor = Executors.newSingleThreadExecutor()
    private val mCaptureAvailableListeners = CopyOnWriteArrayList<CaptureAvailableListener>()

    interface CaptureAvailableListener {
        fun onCaptureAvailable(capture: NodeInfo?)
    }

    fun captureCurrentWindow(): Boolean {
        val service = AccessibilityService.instance
        if (service == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: service = null")
            capture = null
            return false
        }
        val root = getRootInActiveWindow(service)
        if (root == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: root = null")
            capture = null
            return false
        }
        mExecutor.execute {
            isDumping = true
            capture = NodeInfo.capture(mContext, root)
            isDumping = false
            for (l in mCaptureAvailableListeners) {
                l.onCaptureAvailable(capture)
            }
        }
        return true
    }

    fun addCaptureAvailableListener(l: CaptureAvailableListener) {
        mCaptureAvailableListeners.add(l)
    }

    fun removeCaptureAvailableListener(l: CaptureAvailableListener): Boolean {
        return mCaptureAvailableListeners.remove(l)
    }

    private fun getRootInActiveWindow(service: AccessibilityService): AccessibilityNodeInfo? {
        return service.rootInActiveWindow ?: return service.fastRootInActiveWindow()

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun refreshChildList(root: AccessibilityNodeInfo?) {
        if (root == null)
            return
        root.refresh()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            refreshChildList(root.getChild(i))
        }
    }

    fun clearCapture() {
        capture = null
    }

    companion object {

        private val LOG_TAG = LayoutInspector::class.java.simpleName
    }
}
