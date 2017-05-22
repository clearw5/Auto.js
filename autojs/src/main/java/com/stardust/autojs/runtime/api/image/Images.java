package com.stardust.autojs.runtime.api.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.stardust.autojs.runtime.AbstractScriptRuntime;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.runtime.ScriptVariable;
import com.stardust.concurrent.VolatileBox;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Stardust on 2017/5/20.
 */

public class Images {

    private AbstractScriptRuntime mScriptRuntime;
    private ScreenCaptureRequester mScreenCaptureRequester;
    private ScreenCapturer mScreenCapturer;
    private Context mContext;

    @ScriptVariable
    public ColorFinder colorFinder = new ColorFinder();

    public Images(Context context, AbstractScriptRuntime scriptRuntime, ScreenCaptureRequester screenCaptureRequester) {
        mScriptRuntime = scriptRuntime;
        mScreenCaptureRequester = screenCaptureRequester;
        mContext = context;
    }

    public boolean requestScreenCapture(final int width, final int height) {
        mScriptRuntime.requiresApi(21);
        colorFinder.prestartThreads();
        final VolatileBox<Boolean> requestResult = new VolatileBox<>();
        mScreenCaptureRequester.setOnActivityResultCallback(new ScreenCaptureRequester.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRequestResult(int result, Intent data) {
                if (result == Activity.RESULT_OK) {
                    mScreenCapturer = new ScreenCapturer(mContext, data, width, height);
                    requestResult.setAndNotify(true);
                } else {
                    requestResult.setAndNotify(false);
                }
            }
        });
        mScreenCaptureRequester.request();
        return requestResult.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    public boolean requestScreenCapture() {
        return requestScreenCapture(ScreenMetrics.getDeviceScreenWidth(), ScreenMetrics.getDeviceScreenHeight());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Image captureScreen() {
        mScriptRuntime.requiresApi(21);
        colorFinder.prestartThreads();
        return mScreenCapturer.capture();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean captureScreen(String path) {
        mScriptRuntime.requiresApi(21);
        Image image = mScreenCapturer.capture();
        if (image != null) {
            saveImage(image, path);
            image.close();
            return true;
        }
        return false;
    }

    public void saveImage(Image image, String path) {
        Bitmap bitmap = toBitmap(image);
        saveBitmap(bitmap, path);
        bitmap.recycle();
    }

    public static Bitmap toBitmap(Image image) {
        Image.Plane plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        buffer.position(0);
        int pixelStride = plane.getPixelStride();
        int rowPadding = plane.getRowStride() - pixelStride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }


    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void saveImage(Image image, String path, int width, int height) {
        Bitmap bitmap = toBitmap(image);
        if (width != bitmap.getWidth() || height != bitmap.getHeight()) {
            bitmap = scaleBitmap(bitmap, width, height);
        }
        saveBitmap(bitmap, path);
        bitmap.recycle();
    }

    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    public void releaseScreenCapturer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mScreenCapturer != null) {
            mScreenCapturer.release();
        }
    }

}
