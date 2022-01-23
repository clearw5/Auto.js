package com.stardust.autojs.core.image.capture;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.OrientationEventListener;

import androidx.annotation.Nullable;

import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.lang.ThreadCompat;
import com.stardust.util.ScreenMetrics;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by TonyJiangWJ on 2022/1/22
 */
public class GlobalScreenCapture {
    public static final int ORIENTATION_AUTO = Configuration.ORIENTATION_UNDEFINED;
    public static final int ORIENTATION_LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    public static final int ORIENTATION_PORTRAIT = Configuration.ORIENTATION_PORTRAIT;

    private static final String TAG = "GlobalScreenCapture";
    private final ConcurrentHashMap<Thread, Boolean> registeredThreads = new ConcurrentHashMap<>();

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private volatile Looper mImageAcquireLooper;
    private volatile Image mUnderUsingImage;
    private Context mContext;
    private Intent mData;
    private Handler mHandler;
    private final AtomicReference<Image> mCachedImage = new AtomicReference<>();
    private volatile Exception mException;

    private final int mScreenDensity;
    private int mOrientation = -1;
    private int mDetectedOrientation;
    private OrientationEventListener mOrientationEventListener;

    private boolean hasPermission;
    private boolean noRegister;

    private GlobalScreenCapture() {
        mScreenDensity = ScreenMetrics.getDeviceScreenDensity();
    }

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private final static GlobalScreenCapture INSTANCE = new GlobalScreenCapture();
    }

    public static GlobalScreenCapture getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void initCapture(Context context, Intent data, int orientation) {
        Log.d(TAG, "initCapture: ");
        mProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) data.clone());
        mContext = context;
        mData = (Intent) data.clone();
        new Thread(() -> {
            Looper.prepare();
            mHandler = new Handler(Looper.myLooper());
            synchronized (GlobalScreenCapture.this) {
                GlobalScreenCapture.this.notifyAll();
            }
            Looper.loop();
        }).start();
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
        observeOrientation();
        setOrientation(orientation);
        hasPermission = true;
    }

    public synchronized boolean hasPermission() {
        return hasPermission;
    }

    public void setOrientation(int orientation) {
        if (mOrientation == orientation) {
            return;
        }
        mOrientation = orientation;
        mDetectedOrientation = mContext.getResources().getConfiguration().orientation;
        refreshVirtualDisplay(mOrientation == ORIENTATION_AUTO ? mDetectedOrientation : mOrientation);
    }


    private void observeOrientation() {
        mOrientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int o) {
                int orientation = mContext.getResources().getConfiguration().orientation;
                if (mOrientation == ORIENTATION_AUTO && mDetectedOrientation != orientation) {
                    mDetectedOrientation = orientation;
                    try {
                        refreshVirtualDisplay(orientation);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mException = e;
                    }
                }
            }

        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    private void refreshVirtualDisplay(int orientation) {
        if (mImageAcquireLooper != null) {
            mImageAcquireLooper.quit();
        }
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        try {
            mMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) mData.clone());
        } catch (Exception e) {
            Log.d(TAG, "refreshVirtualDisplay: 获取新projection失败 可能只是MIUI的bug " + e);
        }
        int screenHeight = ScreenMetrics.getOrientationAwareScreenHeight(orientation);
        int screenWidth = ScreenMetrics.getOrientationAwareScreenWidth(orientation);
        initVirtualDisplay(screenWidth, screenHeight, mScreenDensity);
        startAcquireImageLoop();
    }

    @SuppressLint("WrongConstant")
    private void initVirtualDisplay(int width, int height, int screenDensity) {
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3);
        try {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG,
                    width, height, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
        } catch (SecurityException e) {
            Log.d(TAG, "initVirtualDisplay: 获取virtualDisplay失败" + e);
            release();
        }
    }

    private void startAcquireImageLoop() {
        if (mImageReader == null) {
            // 初始化virtualDisplay异常
            return;
        }

        if (mHandler != null) {
            setImageListener(mHandler);
            return;
        }
        new Thread(() -> {
            Log.d(TAG, "AcquireImageLoop: start");
            Looper.prepare();
            mImageAcquireLooper = Looper.myLooper();
            setImageListener(new Handler());
            Looper.loop();
            Log.d(TAG, "AcquireImageLoop: stop");
        }).start();
    }

    private void setImageListener(Handler handler) {
        mImageReader.setOnImageAvailableListener(reader -> {
            try {
                if (noRegister) {
                    return;
                }
                Image oldCacheImage = mCachedImage.getAndSet(null);
                if (oldCacheImage != null) {
                    oldCacheImage.close();
                }
                mCachedImage.set(reader.acquireLatestImage());
            } catch (Exception e) {
                mException = e;
            }

        }, handler);
    }

    @Nullable
    public synchronized Image capture() {
        Exception e = mException;
        if (e != null) {
            mException = null;
            throw new ScriptException(e);
        }
        Thread thread = ThreadCompat.currentThread();
        long startTime = System.currentTimeMillis();
        while (!thread.isInterrupted()) {
            Image cachedImage = mCachedImage.getAndSet(null);
            if (cachedImage != null) {
                if (mUnderUsingImage != null) {
                    mUnderUsingImage.close();
                }
                mUnderUsingImage = cachedImage;
                return cachedImage;
            }
            if (System.currentTimeMillis() - startTime > 1000) {
                startTime = System.currentTimeMillis();
                this.refreshVirtualDisplay(mOrientation);
            }
        }
        throw new ScriptInterruptedException();
    }

    public synchronized void unregister(Looper looper) {
        Log.d(TAG, "unregister: " + looper.getThread().getName());
        registeredThreads.remove(looper.getThread());
        Iterator<Thread> keyThreads = registeredThreads.keySet().iterator();
        while (keyThreads.hasNext()) {
            Thread thread = keyThreads.next();
            if (!thread.isAlive()) {
                keyThreads.remove();
            }
        }
        noRegister = registeredThreads.size() == 0;
        if (noRegister) {
            Log.d(TAG, "全部引擎已注销，释放截图权限，清除通知");
            release();
        }
    }

    public synchronized void register(Looper looper) {
        Log.d(TAG, "新引擎注册：" + looper.getThread().getName() + " hasPermission? " + hasPermission);
        noRegister = false;
        registeredThreads.put(looper.getThread(), true);
    }

    private void release() {
        noRegister = true;
        hasPermission = false;
        if (mImageAcquireLooper != null) {
            mImageAcquireLooper.quit();
            mImageAcquireLooper = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mImageReader != null) {
            mImageReader.setOnImageAvailableListener(null, null);
            mImageReader.close();
            mImageReader = null;
        }
        if (mUnderUsingImage != null) {
            mUnderUsingImage.close();
            mUnderUsingImage = null;
        }
        Image cachedImage = mCachedImage.getAndSet(null);
        if (cachedImage != null) {
            cachedImage.close();
        }
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientationEventListener = null;
        }
        mContext.stopService(new Intent(mContext, CaptureForegroundService.class));
    }

}
