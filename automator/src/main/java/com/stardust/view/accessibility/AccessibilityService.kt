package com.stardust.view.accessibility

import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.stardust.event.EventDispatcher

import java.util.HashSet
import java.util.TreeMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Stardust on 2017/5/2.
 */


open class AccessibilityService : android.accessibilityservice.AccessibilityService() {

    interface GestureListener {
        fun onGesture(gestureId: Int)
    }

    val onKeyObserver = OnKeyListener.Observer()
    val keyInterrupterObserver = KeyInterceptor.Observer()
    val gestureEventDispatcher = EventDispatcher<GestureListener>()
    private var mEventExecutor: ExecutorService? = null
    private var mFastRootInActiveWindow: AccessibilityNodeInfo? = null
    private val eventExecutor: ExecutorService
        get() {
            return mEventExecutor ?: {
                val executor = Executors.newSingleThreadExecutor()
                mEventExecutor = executor
                executor
            }()
        }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        instance = this
        // Log.v(TAG, "onAccessibilityEvent: $event");
        if (!containsAllEventTypes && !eventTypes.contains(event.eventType))
            return
        val type = event.eventType
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || type == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            val root = rootInActiveWindow
            if (root != null) {
                mFastRootInActiveWindow = root
            }
        }

        for ((_, delegate) in mDelegates) {
            val types = delegate.eventTypes
            if (types != null && !delegate.eventTypes!!.contains(event.eventType))
                continue
            //long start = System.currentTimeMillis();
            if (delegate.onAccessibilityEvent(this@AccessibilityService, event))
                break
            //Log.v(TAG, "millis: " + (System.currentTimeMillis() - start) + " delegate: " + entry.getValue().getClass().getName());
        }
    }


    override fun onInterrupt() {

    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        eventExecutor.execute {
            stickOnKeyObserver.onKeyEvent(event.keyCode, event)
            onKeyObserver.onKeyEvent(event.keyCode, event)
        }
        return keyInterrupterObserver.onInterceptKeyEvent(event)
    }

    override fun onGesture(gestureId: Int): Boolean {
        eventExecutor.execute {
            gestureEventDispatcher.dispatchEvent {
                onGesture(gestureId)
            }
        }
        return false
    }

    override fun getRootInActiveWindow(): AccessibilityNodeInfo? {
        return try {
            super.getRootInActiveWindow()
        } catch (e: Exception) {
            null
        }

    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy: $instance")
        instance = null
        mEventExecutor?.shutdownNow()
        super.onDestroy()
    }


    override fun onServiceConnected() {
        Log.v(TAG, "onServiceConnected: " + serviceInfo.toString())
        instance = this
        super.onServiceConnected()
        LOCK.lock()
        ENABLED.signalAll()
        LOCK.unlock()
        // FIXME: 2017/2/12 有时在无障碍中开启服务后这里不会调用服务也不会运行，安卓的BUG???
    }


    fun fastRootInActiveWindow(): AccessibilityNodeInfo? {
        return mFastRootInActiveWindow
    }

    companion object {

        private val TAG = "AccessibilityService"

        private val mDelegates = TreeMap<Int, AccessibilityDelegate>()
        private val LOCK = ReentrantLock()
        private val ENABLED = LOCK.newCondition()
        var instance: AccessibilityService? = null
            private set
        val stickOnKeyObserver = OnKeyListener.Observer()
        private var containsAllEventTypes = false
        private val eventTypes = HashSet<Int>()

        fun addDelegate(uniquePriority: Int, delegate: AccessibilityDelegate) {
            mDelegates[uniquePriority] = delegate
            val set = delegate.eventTypes
            if (set == null)
                containsAllEventTypes = true
            else
                eventTypes.addAll(set)
        }

        fun disable(): Boolean {
            if (instance != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                instance!!.disableSelf()
                return true
            }
            return false
        }

        fun waitForEnabled(timeOut: Long): Boolean {
            if (instance != null)
                return true
            LOCK.lock()
            try {
                if (instance != null)
                    return true
                if (timeOut == -1L) {
                    ENABLED.await()
                    return true
                }
                return ENABLED.await(timeOut, TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return false
            } finally {
                LOCK.unlock()
            }
        }
    }


}
