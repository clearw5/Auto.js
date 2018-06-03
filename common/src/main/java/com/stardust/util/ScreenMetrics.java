package com.stardust.util;

import android.app.Activity;
import android.content.res.Configuration;
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
        if (initialized)
            return;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        deviceScreenHeight = metrics.heightPixels;
        deviceScreenWidth = metrics.widthPixels;
        deviceScreenDensity = metrics.densityDpi;
        display = activity.getWindowManager().getDefaultDisplay();
        initialized = true;
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

    public static int getOrientationAwareScreenWidth(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getDeviceScreenHeight();
        } else {
            return getDeviceScreenWidth();
        }
    }

    public static int getOrientationAwareScreenHeight(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getDeviceScreenWidth();
        } else {
            return getDeviceScreenHeight();
        }
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
        return scaleX(x, mDesignWidth);
    }

    public int scaleY(int y) {
        return scaleY(y, mDesignHeight);
    }


    public void setScreenMetrics(int width, int height) {
        mDesignWidth = width;
        mDesignHeight = height;
    }

    public int rescaleX(int x) {
        return rescaleX(x, mDesignWidth);
    }


    public int rescaleY(int y) {
        return rescaleY(y, mDesignHeight);
    }
}
