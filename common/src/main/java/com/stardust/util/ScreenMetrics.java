package com.stardust.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by Stardust on 2017/4/26.
 */

public class ScreenMetrics {

    private static int deviceScreenHeight;
    private static int deviceScreenWidth;
    private static boolean initialized = false;
    private static int deviceScreenDensity;

    public static void initIfNeeded(Activity activity) {
        if (!initialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            deviceScreenHeight = metrics.heightPixels;
            deviceScreenWidth = metrics.widthPixels;
            deviceScreenDensity = metrics.densityDpi;
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


    private int mScreenWidth;
    private int mScreenHeight;


    public void setScreenWidth(int screenWidth) {
        mScreenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight) {
        mScreenHeight = screenHeight;
    }

    public int scaleX(int x) {
        return scaleX(x, mScreenWidth);
    }

    public int scaleY(int y) {
        return scaleY(y, mScreenHeight);
    }


    public void setScreenMetrics(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }
}
