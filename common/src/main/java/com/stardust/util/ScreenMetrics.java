package com.stardust.util;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

import static java.lang.System.out;

/**
 * Created by Stardust on 2017/4/26.
 */

public class ScreenMetrics {

    private static int deviceScreenHeight;
    private static int deviceScreenWidth;
    private static boolean initialized = false;
    private static int deviceScreenDensity;
    private static Display display;

    public static void initIfNeeded(Activity activity) {
        if (!initialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            deviceScreenHeight = metrics.heightPixels;
//            deviceScreenWidth = metrics.widthPixels;
            deviceScreenDensity = metrics.densityDpi;
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getRealSize(size);
            deviceScreenHeight = size.y;
            deviceScreenWidth = size.x;
            display = activity.getWindowManager().getDefaultDisplay();
            initialized = true;
        }
    }

    public static int getDeviceScreenHeight() {
        return deviceScreenHeight;
    }

    public static int getDeviceScreenWidth() {
        return deviceScreenWidth;
    }

    public static int getDeviceScreenDensity() {
        return deviceScreenDensity;
    }

    public static int scaleX(int x, int width) {
        if (width == 0 || !initialized)
            return x;
        return x * deviceScreenWidth / width;
    }

    public static int scaleY(int y, int height) {
        if (height == 0 || !initialized)
            return y;
        return y * deviceScreenHeight / height;
    }

    public static int rescaleX(int x, int width) {
        if (width == 0 || !initialized)
            return x;
        return x * width / deviceScreenWidth;
    }

    public static int rescaleY(int y, int height) {
        if (height == 0 || !initialized)
            return y;
        return y * height / deviceScreenHeight;
    }


    private int mDesignWidth;
    private int mDesignHeight;

    public ScreenMetrics(int designWidth, int designHeight) {
        mDesignWidth = designWidth;
        mDesignHeight = designHeight;
    }

    public ScreenMetrics() {
    }

    public void setDesignWidth(int designWidth) {
        mDesignWidth = designWidth;
    }

    public void setDesignHeight(int designHeight) {
        mDesignHeight = designHeight;
    }

    public int scaleX(int x) {
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            return scaleX(x, mDesignWidth);
        else
            return scaleY(x, mDesignWidth);
    }

    public int scaleY(int y) {
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            return scaleY(y, mDesignHeight);
        else
            return scaleX(y, mDesignHeight);
    }


    public void setScreenMetrics(int width, int height) {
        mDesignWidth = width;
        mDesignHeight = height;
    }

    public int rescaleX(int x) {
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            return rescaleX(x, mDesignWidth);
        else
            return rescaleY(x, mDesignWidth);
    }


    public int rescaleY(int y) {
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            return rescaleY(y, mDesignHeight);
        else
            return rescaleX(y, mDesignHeight);
    }
}
