package com.stardust.autojs.core.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/5/17.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapturer {

    private static final String LOG_TAG = "ScreenCapturer";
    private final Object mCachedImageLock = new Object();
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private volatile Looper mImageAcquireLooper;
    private volatile Image mUnderUsingImage;
    private volatile Image mCachedImage;
    private volatile boolean mImageAvailable = false;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mScreenDensity;
    private Handler mHandler;

    public ScreenCapturer(Context context, Intent data, int screenWidth, int screenHeight, int screenDensity, Handler handler) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mScreenDensity = screenDensity;
        mHandler = handler;
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        initVirtualDisplay(manager, data, screenWidth, screenHeight, screenDensity);
        mHandler = handler;
        startAcquireImageLoop();
    }

    public ScreenCapturer(Context context, Intent data, int screenWidth, int screenHeight) {
        this(context, data, screenWidth, screenHeight, ScreenMetrics.getDeviceScreenDensity(), null);
    }

    public ScreenCapturer(Context context, Intent data) {
        this(context, data, ScreenMetrics.getDeviceScreenWidth(), ScreenMetrics.getDeviceScreenHeight());
    }

    private void initVirtualDisplay(MediaProjectionManager manager, Intent data, int screenWidth, int screenHeight, int screenDensity) {
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        mMediaProjection = manager.getMediaProjection(Activity.RESULT_OK, data);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(LOG_TAG,
                screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startAcquireImageLoop() {
        if (mHandler != null) {
            setImageListener(mHandler);
            return;
        }
        new Thread(() -> {
            Looper.prepare();
            mImageAcquireLooper = Looper.myLooper();
            setImageListener(new Handler());
            Looper.loop();
        }).start();
    }

    private void setImageListener(Handler handler) {
        mImageReader.setOnImageAvailableListener(reader -> {
            if (mCachedImage != null) {
                synchronized (mCachedImageLock) {
                    if (mCachedImage != null) {
                        mCachedImage.close();
                    }
                    mCachedImage = reader.acquireLatestImage();
                    mImageAvailable = true;
                    mCachedImageLock.notify();
                    return;
                }
            }
            mCachedImage = reader.acquireLatestImage();
        }, handler);
    }

    @Nullable
    public Image capture() {
        if (!mImageAvailable) {
            waitForImageAvailable();
        }
        synchronized (mCachedImageLock) {
            if (mCachedImage != null) {
                if (mUnderUsingImage != null)
                    mUnderUsingImage.close();
                mUnderUsingImage = mCachedImage;
                mCachedImage = null;
            }
        }
        return mUnderUsingImage;
    }

    private void waitForImageAvailable() {
        synchronized (mCachedImageLock) {
            if (mImageAvailable) {
                return;
            }
            try {
                mCachedImageLock.wait();
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getScreenDensity() {
        return mScreenDensity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void release() {
        if (mImageAcquireLooper != null) {
            mImageAcquireLooper.quit();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (mUnderUsingImage != null) {
            mUnderUsingImage.close();
        }
        if (mCachedImage != null) {
            mCachedImage.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            release();
        } finally {
            super.finalize();
        }
    }
}