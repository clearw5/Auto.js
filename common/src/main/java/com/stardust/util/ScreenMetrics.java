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

    public static void initIfNeeded(Activity activity) {
        if (!initialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            deviceScreenHeight = metrics.heightPixels;
            deviceScreenWidth = metrics.widthPixels;
            initialized = true;
        }
    }

    public static int getDeviceScreenHeight() {
        return deviceScreenHeight;
    }

    public static int getDeviceScreenWidth() {
        return deviceScreenWidth;
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
        if (mScreenWidth == 0 || !initialized)
            return x;
        return x * deviceScreenWidth / mScreenWidth;
    }

    public int scaleY(int y) {
        if (mScreenHeight == 0 || !initialized)
            return y;
        return y * deviceScreenHeight / mScreenHeight;
    }

    public void setScreenMetrics(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }
}
