package com.stardust.autojs.core.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Xfermode;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.stardust.autojs.runtime.api.Images;
import com.stardust.pio.UncheckedIOException;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * Created by Stardust on 2017/11/25.
 */

public class ImageWrapper {

    private Mat mMat;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;

    public ImageWrapper(Mat mat) {
        mMat = mat;
        mWidth = mat.cols();
        mHeight = mat.rows();
    }

    public ImageWrapper(Bitmap bitmap) {
        mBitmap = bitmap;
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ImageWrapper ofImage(Image image) {
        if (image == null) {
            return null;
        }
        return new ImageWrapper(toBitmap(image));
    }

    public static ImageWrapper ofBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new ImageWrapper(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap toBitmap(Image image) {
        Image.Plane plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        buffer.position(0);
        int pixelStride = plane.getPixelStride();
        int rowPadding = plane.getRowStride() - pixelStride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        if(rowPadding == 0){
            return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Mat getMat() {
        if (mMat == null) {
            mMat = new Mat();
            Utils.bitmapToMat(mBitmap, mMat);
        }
        return mMat;
    }

    public void saveTo(String path) {
        if (mBitmap != null) {
            try {
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Highgui.imwrite(path, mMat);
        }
    }

    public int pixel(int x, int y) {
        if (mBitmap != null) {
            return mBitmap.getPixel(x, y);
        }
        double[] channels = mMat.get(x, y);
        return Color.argb((int) channels[3], (int) channels[0], (int) channels[1], (int) channels[2]);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mMat != null) {
            mMat.release();
            mMat = null;
        }

    }
}
