package com.stardust.scriptdroid.service;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Stardust on 2017/1/19.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class MediaProjectionScreenCaptureService extends Service {
    private ImageReader mImageReader;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    public static Intent mResultData;


    @Override
    public void onCreate() {
        if (mResultData == null) {

        }
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        createImageReader();
        startVirtualDisplay();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createImageReader() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }

    public void startVirtualDisplay() {
        mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onDestroy() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        super.onDestroy();
    }

}