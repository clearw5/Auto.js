package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.core.image.OpenCVHelper;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequester;
import com.stardust.autojs.core.image.capture.ScreenCapturer;
import com.stardust.autojs.core.image.TemplateMatching;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private Image mPreCapture;
    private ImageWrapper mPreCaptureImage;
    private ScreenMetrics mScreenMetrics;

    @ScriptVariable
    public final ColorFinder colorFinder;

    public Images(Context context, ScriptRuntime scriptRuntime, ScreenCaptureRequester screenCaptureRequester) {
        mScriptRuntime = scriptRuntime;
        mScreenCaptureRequester = screenCaptureRequester;
        mContext = context;
        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mScreenMetrics = mScriptRuntime.getScreenMetrics();
        colorFinder = new ColorFinder(mScreenMetrics);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean requestScreenCapture(int orientation) {
        ScriptRuntime.requiresApi(21);
        if (mScreenCapturer != null) {
            mScreenCapturer.setOrientation(orientation);
            return true;
        }
        final VolatileDispose<Boolean> requestResult = new VolatileDispose<>();
        mScreenCaptureRequester.setOnActivityResultCallback((result, data) -> {
            if (result == Activity.RESULT_OK) {
                mScreenCapturer = new ScreenCapturer(mContext, data, orientation, ScreenMetrics.getDeviceScreenDensity(),
                        new Handler(mScriptRuntime.loopers.getServantLooper()));
                requestResult.setAndNotify(true);
            } else {
                requestResult.setAndNotify(false);
            }
        });
        mScreenCaptureRequester.request();
        return requestResult.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean requestScreenCapture(boolean landscape) {
        return requestScreenCapture(landscape ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean requestScreenCapture() {
        return requestScreenCapture(ScreenCapturer.ORIENTATION_AUTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageWrapper captureScreen() {
        ScriptRuntime.requiresApi(21);
        if (mScreenCapturer == null) {
            throw new SecurityException("No screen capture permission");
        }
        Image capture = mScreenCapturer.capture();
        if (capture == mPreCapture && mPreCaptureImage != null) {
            return mPreCaptureImage;
        }
        mPreCapture = capture;
        if (mPreCaptureImage != null) {
            mPreCaptureImage.recycle();
        }
        mPreCaptureImage = ImageWrapper.ofImage(capture);
        return mPreCaptureImage;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean captureScreen(String path) {
        path = mScriptRuntime.files.path(path);
        ImageWrapper image = captureScreen();
        if (image != null) {
            image.saveTo(path);
            return true;
        }
        return false;
    }

    public ImageWrapper copy(ImageWrapper image) {
        image.ensureNotRecycled();
        if (image.getBitmap() == null) {
            return new ImageWrapper(image.getMat().clone());
        }
        if (image.getMat() == null) {
            return new ImageWrapper(image.getBitmap().copy(image.getBitmap().getConfig(), true));
        }
        return new ImageWrapper(image.getBitmap().copy(image.getBitmap().getConfig(), true), image.getMat().clone());
    }

    public boolean save(ImageWrapper image, String path, String format, int quality) throws IOException {
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);
        if (compressFormat == null)
            throw new IllegalArgumentException("unknown format " + format);
        Bitmap bitmap = image.getBitmap();
        FileOutputStream outputStream = new FileOutputStream(mScriptRuntime.files.path(path));
        return bitmap.compress(compressFormat, quality, outputStream);
    }

    public static int pixel(ImageWrapper image, int x, int y) {
        if (image == null) {
            throw new NullPointerException("image = null");
        }
        return image.pixel(x, y);
    }

    public ImageWrapper clip(ImageWrapper img, int x, int y, int w, int h) {
        return ImageWrapper.ofBitmap(Bitmap.createBitmap(img.getBitmap(), x, y, w, h));
    }

    public ImageWrapper read(String path) {
        path = mScriptRuntime.files.path(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return ImageWrapper.ofBitmap(bitmap);
    }

    public ImageWrapper fromBase64(String data) {
        return ImageWrapper.ofBitmap(Drawables.loadBase64Data(data));
    }

    public String toBase64(ImageWrapper wrapper, String format, int quality) {
        return Base64.encodeToString(toBytes(wrapper, format, quality), Base64.NO_WRAP);
    }

    public byte[] toBytes(ImageWrapper wrapper, String format, int quality) {
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);
        if (compressFormat == null)
            throw new IllegalArgumentException("unknown format " + format);
        Bitmap bitmap = wrapper.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, outputStream);
        return outputStream.toByteArray();
    }

    public ImageWrapper fromBytes(byte[] bytes) {
        return ImageWrapper.ofBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    private Bitmap.CompressFormat parseImageFormat(String format) {
        switch (format) {
            case "png":
                return Bitmap.CompressFormat.PNG;
            case "jpeg":
            case "jpg":
                return Bitmap.CompressFormat.JPEG;
            case "webp":
                return Bitmap.CompressFormat.WEBP;
        }
        return null;
    }

    public ImageWrapper load(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return ImageWrapper.ofBitmap(bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
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

    public Point findImage(ImageWrapper image, ImageWrapper template) {
        return findImage(image, template, 0.9f, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold) {
        return findImage(image, template, threshold, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold, Rect rect) {
        return findImage(image, template, 0.7f, threshold, rect, TemplateMatching.MAX_LEVEL_AUTO);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float weakThreshold, float threshold, Rect rect, int maxLevel) {
        if (image == null)
            throw new NullPointerException("image = null");
        if (template == null)
            throw new NullPointerException("template = null");
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        org.opencv.core.Point point = TemplateMatching.fastTemplateMatching(src, template.getMat(), TemplateMatching.MATCHING_METHOD_DEFAULT,
                weakThreshold, threshold, maxLevel);
        if (point != null) {
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        return point;
    }


}
