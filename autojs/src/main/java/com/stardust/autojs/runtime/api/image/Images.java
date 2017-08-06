package com.stardust.autojs.runtime.api.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.concurrent.VolatileBox;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Created by Stardust on 2017/5/20.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Images {

    private ScriptRuntime mScriptRuntime;
    private ScreenCaptureRequester mScreenCaptureRequester;
    private ScreenCapturer mScreenCapturer;
    private Context mContext;
    private Display mDisplay;

    @ScriptVariable
    public final ColorFinder colorFinder;

    public Images(Context context, ScriptRuntime scriptRuntime, ScreenCaptureRequester screenCaptureRequester) {
        mScriptRuntime = scriptRuntime;
        mScreenCaptureRequester = screenCaptureRequester;
        mContext = context;
        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        colorFinder = new ColorFinder();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean requestScreenCapture(final int width, final int height) {
        mScriptRuntime.requiresApi(21);
        colorFinder.prestartThreads();
        final VolatileBox<Boolean> requestResult = new VolatileBox<>();
        mScreenCaptureRequester.setOnActivityResultCallback(new ScreenCaptureRequester.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRequestResult(int result, Intent data) {
                if (result == Activity.RESULT_OK) {
                    mScreenCapturer = new ScreenCapturer(mContext, data, width, height, ScreenMetrics.getDeviceScreenDensity(),
                            new Handler(mScriptRuntime.loopers.getServantLooper()));
                    requestResult.setAndNotify(true);
                } else {
                    requestResult.setAndNotify(false);
                }
            }
        });
        mScreenCaptureRequester.request();
        return requestResult.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean requestScreenCapture() {
        if (mDisplay.getRotation() == Surface.ROTATION_0 || mDisplay.getRotation() == Surface.ROTATION_180)
            return requestScreenCapture(ScreenMetrics.getDeviceScreenWidth(), ScreenMetrics.getDeviceScreenHeight());
        else
            return requestScreenCapture(ScreenMetrics.getDeviceScreenHeight(), ScreenMetrics.getDeviceScreenWidth());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Image captureScreen() {
        mScriptRuntime.requiresApi(21);
        if (mScreenCapturer == null) {
            throw new SecurityException("No screen capture permission");
        }
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

    public static int pixel(Image image, int x, int y) {
        int originX = x;
        int originY = y;
        ScreenMetrics metrics = new ScreenMetrics(image.getWidth(), image.getHeight());
        x = metrics.rescaleX(x);
        y = metrics.rescaleY(y);
        Image.Plane plane = image.getPlanes()[0];
        int offset = y * plane.getRowStride() + x * plane.getPixelStride();
        int c = plane.getBuffer().getInt(offset);
        Log.d("Images", String.format(Locale.getDefault(), "(%d, %d)→(%d, %d)", originX, originY, x, y));
        return (c & 0xff000000) + ((c & 0xff) << 16) + (c & 0x00ff00) + ((c & 0xff0000) >> 16);
    }

    public static int pixel(Bitmap bitmap, int x, int y) {
        x = ScreenMetrics.rescaleX(x, bitmap.getWidth());
        y = ScreenMetrics.rescaleY(y, bitmap.getHeight());
        return bitmap.getPixel(x, y);
    }


    public static Bitmap read(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path, int width, int height) {
        if (width != bitmap.getWidth() || height != bitmap.getHeight()) {
            Bitmap scaleBitmap = scaleBitmap(bitmap, width, height);
            saveBitmap(scaleBitmap, path);
            if (scaleBitmap != bitmap) {
                scaleBitmap.recycle();
            }
        } else {
            saveBitmap(bitmap, path);
        }
    }

    public void saveImage(Image image, String path, int width, int height) {
        Bitmap bitmap = toBitmap(image);
        saveBitmap(bitmap, path, width, height);
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
