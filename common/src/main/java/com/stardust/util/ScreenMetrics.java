package com.stardust.util;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.stardust.pio.PFile;

/**
 * Created by Stardust on 2017/4/26.
 */

public class ScreenMetrics {

    private static int screenHeight;
    private static int screenWidth;
    private static boolean initialized = false;

    public static void initIfNeeded(Activity activity) {
        if (!initialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenHeight = metrics.heightPixels;
            screenWidth = metrics.widthPixels;
            initialized = true;
        }
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }
}
