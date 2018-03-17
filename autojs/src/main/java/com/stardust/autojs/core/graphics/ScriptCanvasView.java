package com.stardust.autojs.core.graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stardust.autojs.core.eventloop.EventEmitter;

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


    public ScriptCanvasView(Context context, EventEmitter eventEmitter) {
        super(context);
        mEventEmitter = eventEmitter;
        mHolder = getHolder();
        init();
    }

    private void init() {
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        performDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private void performDraw() {
        if (mDrawingThreadPool == null)
            mDrawingThreadPool = Executors.newCachedThreadPool();
        mDrawingThreadPool.execute(() -> {
            SurfaceHolder holder = getHolder();
            while (mDrawing) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                emit("draw", canvas, this);
                holder.unlockCanvasAndPost(canvas);
            }
            Log.d(LOG_TAG, "drawing thread: mRunning = false");
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE) {
            if (mDrawingThreadPool != null) {
                mDrawing = true;
                performDraw();
            }
        } else {
            mDrawing = false;
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceDestroyed: mRunning = true");
        mDrawing = false;
        Log.d(LOG_TAG, "surfaceDestroyed: mRunning = false");
        mDrawingThreadPool.shutdown();
        mDrawingThreadPool = null;
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
