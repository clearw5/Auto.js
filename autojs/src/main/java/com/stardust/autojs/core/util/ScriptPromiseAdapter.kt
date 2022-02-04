package com.stardust.autojs.core.util

import android.util.Log
import com.stardust.concurrent.VolatileDispose

class ScriptPromiseAdapter {

    interface Callback {
        fun call(arg: Any?)
    }

    private var mResolveCallback: Callback? = null
    private var mRejectCallback: Callback? = null
    private var mResult: Any? = UNSET
    private var mError: Any? = UNSET
    private val TAG = "ScriptPromiseAdapter";
    private var volatileDispose = VolatileDispose<Boolean>();

    fun onResolve(callback: Callback): ScriptPromiseAdapter {
        Log.d(TAG, "onResolve, mResult == UNSET ? " + (mResult == UNSET))
        mResolveCallback = callback
        mResult.let {
            if (it !== UNSET) {
                callback.call(it)
            }
        }
        volatileDispose.setAndNotify(true)
        return this
    }

    fun awaitResolver() {
        volatileDispose.blockedGet()
    }

    fun onReject(callback: Callback): ScriptPromiseAdapter {
        mRejectCallback = callback
        mError.let {
            if (it !== UNSET) {
                callback.call(it)
            }
        }
        return this
    }

    fun resolve(result: Any?) {
        Log.d(TAG, "resolve, result = " + result)
        mResult = result
        mResolveCallback?.call(result)
        releaseCallbacks()
    }

    fun reject(error: Any?) {
        mError = error
        mRejectCallback?.call(error)
        releaseCallbacks()
    }

    fun releaseCallbacks() {
        mResolveCallback = null
        mRejectCallback = null
    }

    companion object {
        private val UNSET = Object()
    }
}