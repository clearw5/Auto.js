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
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.exception.ScriptInterruptedException
import com.stardust.ext.ifNull
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Stardust on 2018/3/16.
 */

@SuppressLint("ViewConstructor")
class ScriptCanvasView(context: Context, private val mScriptRuntime: ScriptRuntime) : TextureView(context), TextureView.SurfaceTextureListener {
    @Volatile
    private var mDrawing = true
    private val mEventEmitter: EventEmitter = EventEmitter(mScriptRuntime.bridges)
    private var mDrawingThreadPool: ExecutorService? = null
    @Volatile
    private var mTimePerDraw = (1000 / 30).toLong()

    val maxListeners: Int
        get() = mEventEmitter.maxListeners

    init {
        surfaceTextureListener = this
    }

    fun setMaxFps(maxFps: Int) {
        mTimePerDraw = if (maxFps <= 0) {
            0
        } else {
            (100 / maxFps).toLong()
        }
    }

    @Synchronized
    private fun performDraw() {
        ::mDrawingThreadPool.ifNull {
            Executors.newCachedThreadPool()
        }.run {
            execute {
                var canvas: Canvas? = null
                var time = SystemClock.uptimeMillis()
                val scriptCanvas = ScriptCanvas()
                try {
                    while (mDrawing) {
                        canvas = lockCanvas()
                        scriptCanvas.setCanvas(canvas)
                        emit("draw", scriptCanvas, this@ScriptCanvasView)
                        unlockCanvasAndPost(canvas)
                        canvas = null
                        val dt = mTimePerDraw - (SystemClock.uptimeMillis() - time)
                        if (dt > 0) {
                            sleep(dt)
                        }
                        time = SystemClock.uptimeMillis()
                    }
                } catch (e: Exception) {
                    mScriptRuntime.exit(e)
                    mDrawing = false
                } finally {
                    if (canvas != null) {
                        unlockCanvasAndPost(canvas)
                    }
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
        Log.d(LOG_TAG, "onWindowVisibilityChanged: " + this + ": visibility=" + visibility + ", mDrawingThreadPool=" + mDrawingThreadPool)
        val oldDrawing = mDrawing
        mDrawing = visibility == View.VISIBLE
        if (!oldDrawing && mDrawing) {
            performDraw()
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
        performDraw()
        Log.d(LOG_TAG, "onSurfaceTextureAvailable: ${this}, width = $width, height = $height")
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mDrawing = false
        mDrawingThreadPool?.shutdown()
        Log.d(LOG_TAG, "onSurfaceTextureDestroyed: ${this}")
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
