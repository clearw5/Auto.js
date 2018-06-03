package com.stardust.autojs.core.image.capture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.OrientationEventListener;

import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/5/17.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapturer {

    public static final int ORIENTATION_AUTO = -1;

    private static final String LOG_TAG = "ScreenCapturer";
    private final Object mCachedImageLock = new Object();
    private final MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private final MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private volatile Looper mImageAcquireLooper;
    private volatile Image mUnderUsingImage;
    private volatile Image mCachedImage;
    private volatile boolean mImageAvailable = false;
    private volatile Exception mException;
    private final int mScreenDensity;
    private Handler mHandler;
    private Intent mData;
    private Context mContext;
    private int mOrientation = Configuration.ORIENTATION_UNDEFINED;
    private int mDetectedOrientation;
    private OrientationEventListener mOrientationEventListener;

    public ScreenCapturer(Context context, Intent data, int orientation, int screenDensity, Handler handler) {
        mContext = context;
        mData = data;
        mScreenDensity = screenDensity;
        mHandler = handler;
        mProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, mData);
        mHandler = handler;
        setOrientation(orientation);
        observeOrientation();
    }

    private void observeOrientation() {
        mOrientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int o) {
                int orientation = mContext.getResources().getConfiguration().orientation;
                if (mOrientation == ORIENTATION_AUTO && mDetectedOrientation != orientation) {
                    mDetectedOrientation = orientation;
                    refreshVirtualDisplay(orientation);
                }
            }

        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    public void setOrientation(int orientation) {
        if (mOrientation == orientation)
            return;
        mOrientation = orientation;
        mDetectedOrientation = mContext.getResources().getConfiguration().orientation;
        refreshVirtualDisplay(mOrientation == ORIENTATION_AUTO ? mDetectedOrientation : mOrientation);
    }


    private void refreshVirtualDisplay(int orientation) {
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        mImageAvailable = false;
        int screenHeight = ScreenMetrics.getOrientationAwareScreenHeight(orientation);
        int screenWidth = ScreenMetrics.getOrientationAwareScreenWidth(orientation);
        initVirtualDisplay(screenWidth, screenHeight, mScreenDensity);
        startAcquireImageLoop();
    }

    private void initVirtualDisplay(int width, int height, int screenDensity) {
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(LOG_TAG,
                width, height, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
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
            try {
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
            } catch (Exception e) {
                mException = e;
            }

        }, handler);
    }

    @Nullable
    public Image capture() {
        if (!mImageAvailable) {
            waitForImageAvailable();
        }
        if (mException != null) {
            Exception e = mException;
            mException = null;
            throw new ScriptException(e);
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
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
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