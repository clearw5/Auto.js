package com.stardust.autojs.core.util

class ScriptPromiseAdapter {

    interface Callback {
        fun call(arg: Any?)
    }

    private var mResolveCallback: Callback? = null
    private var mRejectCallback: Callback? = null
    private var mResult: Any? = UNSET
    private var mError: Any? = UNSET

    fun onResolve(callback: Callback): ScriptPromiseAdapter {
        mResolveCallback = callback
        mResult.let {
            if (it !== UNSET) {
                callback.call(it)
            }
        }
        return this
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