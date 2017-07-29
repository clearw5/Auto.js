package com.stardust.autojs.runtime.api.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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

import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/5/17.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapturer {

    private static final String LOG_TAG = "ScreenCapturer";
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private volatile Looper mImageAcquireLooper;
    private final Object mImageWaitingLock = new Object();
    private volatile Image mImage;
    private volatile Image mLatestImage;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mScreenDensity;
    private Handler mHandler;
    private volatile boolean mImageAvailable = false;

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
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startAcquireImageLoop() {
        if (mHandler != null) {
            setImageListener(mHandler);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mImageAcquireLooper = Looper.myLooper();
                setImageListener(new Handler());
                Looper.loop();
            }
        }).start();
    }

    private void setImageListener(Handler handler) {
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (mImageAvailable) {
                    return;
                }
                mLatestImage = reader.acquireLatestImage();
                if (mLatestImage != null) {
                    mImageAvailable = true;
                    synchronized (mImageWaitingLock) {
                        mImageWaitingLock.notify();
                    }
                }
            }
        }, handler);
    }

    @Nullable
    public Image capture() {
        if (!mImageAvailable) {
            waitForImageAvailable();
            return mLatestImage;
        }
        if (mLatestImage != null) {
            tryClose(mLatestImage);
        }
        mLatestImage = mImageReader.acquireLatestImage();
        return mLatestImage;
    }

    private void tryClose(Image image) {
        try {
            image.close();
        } catch (Exception ignored) {

        }
    }

    private void waitForImageAvailable() {
        Log.d(LOG_TAG, "waitForImageAvailable");
        synchronized (mImageWaitingLock) {
            try {
                mImageWaitingLock.wait();
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
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (mImageAcquireLooper != null) {
            mImageAcquireLooper.quitSafely();
        }
        if (mImage != null) {
            mImage.close();
        }
        if (mLatestImage != null) {
            mLatestImage.close();
        }
    }
}
