package com.tony;

import android.util.Log;

import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.core.opencv.Mat;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * 该方法内部进行图片二值化
 * 原始图片 根据指定位置颜色二值化后，中值滤波
 */
public class ColorCenterCalculator extends ColorCenterCalculatorWithInterval {

    private ImageWrapper img;
    private int threshold;
    private int color;
    private BitCheck checker;
    private int startX;
    private int offset;
    private int width;
    private int height;
    private Point point;
    private boolean debug;
    private ColorFinder colorFinder;

    public ColorCenterCalculator(ImageWrapper img, int startX, int x, int y, int threshold, ScriptRuntime runtime) {
        super(img, startX, x, y);
        this.threshold = threshold;
        if (debug) {
            img.saveTo("/storage/emulated/0/脚本/dev_original.png");
        }
        this.colorFinder = ((Images) runtime.getImages()).colorFinder;
        this.color = img.getBitmap().getPixel(x, y);
        this.img = medianBlur(interval(img));
        // 如果二值化后点颜色不匹配 重新获取匹配的点 宽度为20的正方形内搜索
        this.regetPointIfNotFit();
    }

    @Override
    protected String getLogTag() {
        return "ColorCenterCalculator";
    }

    private ImageWrapper inRange(ImageWrapper img, int lowColor, int upColor) {
        Scalar low = new Scalar(lowColor >> 16 & 0xFF, lowColor >> 8 & 0xFF, lowColor & 0xFF, 255);
        Scalar upper = new Scalar(upColor >> 16 & 0xFF, upColor >> 8 & 0xFF, upColor & 0xFF, 255);
        Mat newMat = new Mat();
        Core.inRange(img.getMat(), low, upper, newMat);
        return ImageWrapper.ofMat(newMat);
    }

    private ImageWrapper interval(ImageWrapper img) {
        Scalar lb = new Scalar(color >> 16 & 0xFF - threshold, color >> 8 & 0xFF - threshold,
                color & 0xFF - threshold, color >>> 24 & 0xFF);
        Scalar ub = new Scalar(color >> 16 & 0xFF + threshold, color >> 8 & 0xFF + threshold,
                color & 0xFF + threshold, color >>> 24 & 0xFF);
        Mat bi = new Mat();
        Core.inRange(img.getMat(), lb, ub, bi);
        ImageWrapper tmp = ImageWrapper.ofMat(bi);
        if (debug) {
            tmp.saveTo("/storage/emulated/0/脚本/dev_interval.png");
        }
        // 原始图片回收
        img.recycle();
        return tmp;
    }

    private ImageWrapper medianBlur(ImageWrapper img) {
        Mat mat = new Mat();
        Imgproc.medianBlur(img.getMat(), mat, 5);
        ImageWrapper tmp = ImageWrapper.ofMat(mat);
        if (debug) {
            tmp.saveTo("/storage/emulated/0/脚本/dev_interval_blur.png");
        }
        // 原始图片回收
        img.recycle();
        return tmp;
    }

    private int getInRangeX(int originX, int offset) {
        int newRangeX = originX + offset;
        if (newRangeX < startX) {
            newRangeX = startX;
        }
        if (newRangeX >= width) {
            newRangeX = width - 1;
        }
        return newRangeX;
    }

    private int getInRangeY(int originY, int offset) {
        int newRangeY = originY + offset;
        if (newRangeY < 0) {
            newRangeY = 0;
        }
        if (newRangeY >= height) {
            newRangeY = height - 1;
        }
        return newRangeY;
    }

    private Rect buildRegion() {
        org.opencv.core.Point leftTop = new org.opencv.core.Point(getInRangeX((int) point.x, -10), getInRangeY((int) point.y, -10));
        org.opencv.core.Point rightBottom = new org.opencv.core.Point(getInRangeX((int) point.x, 10), getInRangeY((int) point.y, 10));
        return new Rect(leftTop, rightBottom);
    }

    /**
     * 如果二值化后点颜色不匹配 重新获取匹配的点 宽度为20的正方形内搜索
     */
    private void regetPointIfNotFit() {
        if ((this.img.getBitmap().getPixel((int) this.point.x, (int) this.point.y) & 0xFFFFFF) != 0xFFFFFF) {
            Rect region = buildRegion();
            org.opencv.core.Point newPoint = colorFinder.findColor(this.img, 0xFFFFFF, 0, region);
            if (newPoint != null) {
                logD(getLogTag(), "二值化后颜色不匹配重新获取point originPoint:" + String.format("[%d,%d]", point.x, point.y)
                        + String.format(" currentColor: %s", Integer.toHexString(this.img.getBitmap().getPixel((int) this.point.x, (int) this.point.y) & 0xFFFFFF))
                        + " newPoint:" + String.format("[%d, %d]", newPoint.x, newPoint.y));
                this.point = new Point(newPoint.x, newPoint.y);
            } else {
                logE(getLogTag(),
                        "二值化后颜色不匹配且重新获取point失败 originPoint:" + String.format("[%d,%d]", point.x, point.y)
                                + String.format(" currentColor: %s", Integer.toHexString(this.img.getBitmap().getPixel((int) this.point.x, (int) this.point.y) & 0xFFFFFF))
                                + " recheckRegion:" + String.format("[%d, %d, %d, %d]", region.x, region.y, region.width, region.height)
                );
            }
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
