package com.stardust.autojs.core.graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2018/3/16.
 */

@SuppressLint("ViewConstructor")
public class ScriptCanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = "ScriptCanvasView";
    private volatile boolean mDrawing = true;
    private EventEmitter mEventEmitter;
    private final SurfaceHolder mHolder;
    private ExecutorService mDrawingThreadPool;
    private ScriptRuntime mScriptRuntime;
    private volatile long mTimePerDraw = 1000 / 30;

    public ScriptCanvasView(Context context, ScriptRuntime scriptRuntime) {
        super(context);
        mScriptRuntime = scriptRuntime;
        mEventEmitter = new EventEmitter(mScriptRuntime.bridges);
        mHolder = getHolder();
        init();
    }


    public void setMaxFps(int maxFps) {
        if (maxFps <= 0) {
            mTimePerDraw = 0;
        } else {
            mTimePerDraw = 100 / maxFps;
        }
    }

    private void init() {
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        performDraw();
        Log.d(LOG_TAG, "surfaceCreated: " + this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private synchronized void performDraw() {
        if (mDrawingThreadPool != null)
            return;
        mDrawingThreadPool = Executors.newCachedThreadPool();
        mDrawingThreadPool.execute(() -> {
            Canvas canvas = null;
            SurfaceHolder holder = getHolder();
            long time = SystemClock.uptimeMillis();
            ScriptCanvas scriptCanvas = new ScriptCanvas();
            try {
                while (mDrawing) {
                    canvas = holder.lockCanvas();
                    scriptCanvas.setCanvas(canvas);
                    scriptCanvas.drawColor(Color.WHITE);
                    emit("draw", scriptCanvas, ScriptCanvasView.this);
                    holder.unlockCanvasAndPost(canvas);
                    canvas = null;
                    long dt = mTimePerDraw - (SystemClock.uptimeMillis() - time);
                    if (dt > 0) {
                        sleep(dt);
                    }
                    time = SystemClock.uptimeMillis();
                }
            } catch (Exception e) {
                mScriptRuntime.exit(e);
                mDrawing = false;
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        });
    }

    private void sleep(long dt) {
        try {
            Thread.sleep(dt);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException(e);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        Log.d(LOG_TAG, "onWindowVisibilityChanged: " + this + ": visibility=" + visibility + ", mDrawingThreadPool=" + mDrawingThreadPool);
        if (visibility == VISIBLE) {
            mDrawing = true;
        } else {
            mDrawing = false;
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        mDrawing = false;
        mDrawingThreadPool.shutdown();
        mDrawingThreadPool = null;
        Log.d(LOG_TAG, "surfaceDestroyed: " + this);
    }

    public EventEmitter once(String eventName, Object listener) {
        return mEventEmitter.once(eventName, listener);
    }

    public EventEmitter on(String eventName, Object listener) {
        return mEventEmitter.on(eventName, listener);
    }

    public EventEmitter addListener(String eventName, Object listener) {
        return mEventEmitter.addListener(eventName, listener);
    }

    public boolean emit(String eventName, Object... args) {
        return mEventEmitter.emit(eventName, args);
    }

    public String[] eventNames() {
        return mEventEmitter.eventNames();
    }

    public int listenerCount(String eventName) {
        return mEventEmitter.listenerCount(eventName);
    }

    public Object[] listeners(String eventName) {
        return mEventEmitter.listeners(eventName);
    }

    public EventEmitter prependListener(String eventName, Object listener) {
        return mEventEmitter.prependListener(eventName, listener);
    }

    public EventEmitter prependOnceListener(String eventName, Object listener) {
        return mEventEmitter.prependOnceListener(eventName, listener);
    }

    public EventEmitter removeAllListeners() {
        return mEventEmitter.removeAllListeners();
    }

    public EventEmitter removeAllListeners(String eventName) {
        return mEventEmitter.removeAllListeners(eventName);
    }

    public EventEmitter removeListener(String eventName, Object listener) {
        return mEventEmitter.removeListener(eventName, listener);
    }

    public EventEmitter setMaxListeners(int n) {
        return mEventEmitter.setMaxListeners(n);
    }

    public int getMaxListeners() {
        return mEventEmitter.getMaxListeners();
    }

    public static int defaultMaxListeners() {
        return EventEmitter.defaultMaxListeners();
    }

}
