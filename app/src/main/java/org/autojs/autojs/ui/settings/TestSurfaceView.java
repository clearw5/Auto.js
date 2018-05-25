package org.autojs.autojs.ui.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2018/3/16.
 */

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private volatile boolean mDrawing = true;
    private static final String LOG_TAG = "TestSurfaceView";
    private ExecutorService mDrawingThreadPool;

    public TestSurfaceView(Context context) {
        super(context);
        init();
    }

    public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
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
                canvas.drawColor(Color.RED);
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
}
