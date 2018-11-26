package com.stardust.view.accessibility

import android.util.Log
import android.view.KeyEvent
import android.view.View

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Stardust on 2017/7/18.
 */

interface OnKeyListener {

    fun onKeyEvent(keyCode: Int, event: KeyEvent)

    class Observer : OnKeyListener {

        private val mOnKeyListeners = CopyOnWriteArrayList<OnKeyListener>()

        override fun onKeyEvent(keyCode: Int, event: KeyEvent) {
            for (listener in mOnKeyListeners) {
                try {
                    listener.onKeyEvent(keyCode, event)
                } catch (e: Exception) {
                    Log.e(TAG, "Error OnKeyEvent: $event Listener: $listener", e)
                }

            }
        }

        fun addListener(listener: OnKeyListener) {
            mOnKeyListeners.add(listener)
        }

        fun removeListener(listener: OnKeyListener): Boolean {
            return mOnKeyListeners.remove(listener)
        }

        companion object {

            private val TAG = "OnKeyListenerObserver"
        }
    }
}
