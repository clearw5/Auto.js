package com.stardust.autojs.runtime.api.image;

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
import android.os.Looper;
import android.support.annotation.RequiresApi;

import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/5/17.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapturer {

    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private Image mImage;

    public ScreenCapturer(Context context, Intent data, int screenWidth, int screenHeight, int screenDensity) {
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        initVirtualDisplay(manager, data, screenWidth, screenHeight, screenDensity);
    }

    public ScreenCapturer(Context context, Intent data, int screenWidth, int screenHeight) {
        this(context, data, screenWidth, screenHeight, ScreenMetrics.getDeviceScreenDensity());
    }

    public ScreenCapturer(Context context, Intent data) {
        this(context, data, ScreenMetrics.getDeviceScreenWidth(), ScreenMetrics.getDeviceScreenHeight());
    }

    private void initVirtualDisplay(MediaProjectionManager manager, Intent data, int screenWidth, int screenHeight, int screenDensity) {
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
        mMediaProjection = manager.getMediaProjection(Activity.RESULT_OK, data);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public Image capture() {
        if (mImage != null) {
            mImage.close();
        }
        mImage = mImageReader.acquireLatestImage();
        if (mImage == null) {
            Looper.prepare();
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Looper.myLooper().quit();
                }
            }, null);
            Looper.loop();
            mImage = mImageReader.acquireLatestImage();
        }
        return mImage;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void release() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mImage != null) {
            mImage.close();
        }
    }
}
