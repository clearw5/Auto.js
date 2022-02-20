package com.stardust.autojs.core.graphics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.SurfaceTexture
import android.os.SystemClock
import android.util.Log
import android.view.TextureView
import android.view.View
import com.stardust.autojs.core.eventloop.EventEmitter
import com.stardust.autojs.core.ui.ViewExtras
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.exception.ScriptInterruptedException
import com.stardust.ext.ifNull
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Stardust on 2018/3/16.
 */

@SuppressLint("ViewConstructor")
class ScriptCanvasView(context: Context, scriptRuntime: ScriptRuntime) : TextureView(context),
    TextureView.SurfaceTextureListener {
    private val mEventEmitter: EventEmitter = EventEmitter(scriptRuntime.bridges)
    private val mScriptRuntime: WeakReference<ScriptRuntime>
    private var mDrawingThreadPool: ExecutorService? = null
    private val state: AtomicReference<CanvasState>
    private val lock: ReentrantLock
    private val resumed: Condition

    private enum class CanvasState {
        INIT, DRAWING, PAUSE, EXITING, END
    }

    @Volatile
    private var mTimePerDraw = (1000 / 30).toLong()

    val maxListeners: Int
        get() = mEventEmitter.maxListeners

    init {
        surfaceTextureListener = this
        mScriptRuntime = WeakReference(scriptRuntime)
        state = AtomicReference(CanvasState.INIT)
        lock = ReentrantLock()
        resumed = lock.newCondition()
    }

    fun setMaxFps(maxFps: Int) {
        mTimePerDraw = if (maxFps <= 0) {
            0
        } else {
            (100 / maxFps).toLong()
        }
    }

    private fun drawOnce(scriptCanvas: ScriptCanvas) {
        var canvas: Canvas? = null
        lock.lock()
        try {
            if (state.get() != CanvasState.DRAWING) {
                Log.d(LOG_TAG, "canvas state is not drawing ${state.get()}")
                return
            }
//            Log.d(LOG_TAG, "canvas draw")
            val time = SystemClock.uptimeMillis()
            canvas = lockCanvas()
            scriptCanvas.setCanvas(canvas)
            emit("draw", scriptCanvas, this@ScriptCanvasView)
            if (state.get() != CanvasState.DRAWING) {
                Log.d(LOG_TAG, "canvas state is not drawing ${state.get()}")
                return
            }
            val dt = mTimePerDraw - (SystemClock.uptimeMillis() - time)
            if (dt > 0) {
                sleep(dt)
            }
        } catch (e: Exception) {
            mScriptRuntime.get()?.exit(e)
            state.set(CanvasState.END)
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas)
            }
            lock.unlock()
        }
    }

    @Synchronized
    private fun performDraw() {
        ::mDrawingThreadPool.ifNull {
            Executors.newCachedThreadPool()
        }.run {
            execute {
                try {
                    val scriptCanvas = ScriptCanvas()
                    while (true) {
                        if (mScriptRuntime.get()?.isStopped == true) {
                            Log.d(LOG_TAG, "performDraw: script runtime stopped ${mScriptRuntime.get()?.isStopped}")
                            break
                        }
                        try {
                            lock.lock()
                            while (state.get() == CanvasState.PAUSE || state.get() == CanvasState.INIT) {
                                Log.d(LOG_TAG, "canvas draw paused, wait for resume")
                                resumed.await()
                                Log.d(LOG_TAG, "canvas draw resume")
                            }
                        } finally {
                            lock.unlock()
                        }
                        drawOnce(scriptCanvas)
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "performDraw: error $e")
                    mScriptRuntime.get()?.exit(e)
                    state.set(CanvasState.END)
                }
            }
        }
    }

    private fun sleep(dt: Long) {
        try {
            Thread.sleep(dt)
        } catch (e: InterruptedException) {
            throw ScriptInterruptedException(e)
        }

    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        Log.d(
            LOG_TAG,
            "onWindowVisibilityChanged: " + this + ": visibility=" + visibility + ", mDrawingThreadPool=" + mDrawingThreadPool
        )
        if (visibility == View.VISIBLE) {
            if (state.compareAndSet(CanvasState.PAUSE, CanvasState.DRAWING)) {
                lock.lock()
                try {
                    Log.d(LOG_TAG, "resume canvas draw")
                    resumed.signalAll()
                } finally {
                    lock.unlock()
                }
            }
        } else if (state.compareAndSet(CanvasState.DRAWING, CanvasState.PAUSE)) {
            Log.d(LOG_TAG, "pause canvas draw")
        }
        super.onWindowVisibilityChanged(visibility)
    }

    fun once(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.once(eventName, listener)
    }

    fun on(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.on(eventName, listener)
    }

    fun addListener(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.addListener(eventName, listener)
    }

    fun emit(eventName: String, vararg args: Any): Boolean {
        return mEventEmitter.emit(eventName, *args)
    }

    fun eventNames(): Array<String> {
        return mEventEmitter.eventNames()
    }

    fun listenerCount(eventName: String): Int {
        return mEventEmitter.listenerCount(eventName)
    }

    fun listeners(eventName: String): Array<Any> {
        return mEventEmitter.listeners(eventName)
    }

    fun prependListener(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.prependListener(eventName, listener)
    }

    fun prependOnceListener(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.prependOnceListener(eventName, listener)
    }

    fun removeAllListeners(): EventEmitter {
        return mEventEmitter.removeAllListeners()
    }

    fun removeAllListeners(eventName: String): EventEmitter {
        return mEventEmitter.removeAllListeners(eventName)
    }

    fun removeListener(eventName: String, listener: Any): EventEmitter {
        return mEventEmitter.removeListener(eventName, listener)
    }

    fun setMaxListeners(n: Int): EventEmitter {
        return mEventEmitter.setMaxListeners(n)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d(LOG_TAG, "onSurfaceTextureAvailable: ${this}, width = $width, height = $height")
        if (state.compareAndSet(CanvasState.INIT, CanvasState.DRAWING)) {
            Log.d(LOG_TAG, "start drawing")
            lock.lock()
            try {
                performDraw()
                resumed.signalAll()
            } finally {
                lock.unlock()
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        removeAllListeners()
        val currentStat = state.get()
        while (
            currentStat != CanvasState.END && currentStat != CanvasState.EXITING
            && !state.compareAndSet(currentStat, CanvasState.EXITING)
        ) {
            // waiting
        }
        lock.lock()
        try {
            state.set(CanvasState.END)
            mDrawingThreadPool?.shutdown()
        } finally {
            lock.unlock()
        }
        surfaceTextureListener = null
        setOnTouchListener(null)
        Log.d(LOG_TAG, "onSurfaceTextureDestroyed: ${this}")
        ViewExtras.recycle(this)
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    companion object {

        private const val LOG_TAG = "ScriptCanvasView"

        fun defaultMaxListeners(): Int {
            return EventEmitter.defaultMaxListeners()
        }
    }
}
