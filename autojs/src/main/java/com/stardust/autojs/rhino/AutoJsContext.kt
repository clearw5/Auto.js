package com.stardust.autojs.rhino

import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.ContinuationPending
import org.mozilla.javascript.Scriptable

class AutoJsContext(factory: ContextFactory?) : Context(factory) {

    private val mContinuations = HashSet<Any>()

    override fun captureContinuation(): ContinuationPending {
        val continuationPending = super.captureContinuation()
        mContinuations.add(continuationPending.continuation)
        return continuationPending
    }

    override fun resumeContinuation(continuation: Any, scope: Scriptable?, functionResult: Any?): Any {
        mContinuations.remove(continuation)
        return super.resumeContinuation(continuation, scope, functionResult)
    }

    fun hasPendingContinuation(): Boolean {
        return mContinuations.isNotEmpty()
    }

}