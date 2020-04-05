package com.stardust.util;

import android.util.Log;

import java.io.File;

public class LogHelper {

    private static final String LOG_PATH = "/sdcard/脚本";

    private static boolean ENABLE_LOG_FILE = new File(LOG_PATH).exists();


    public static int v(String tag, String msg) {
        writeLogFile(tag, msg);
        return Log.v(tag, msg);
    }

    public static int d(String tag, String msg) {
        writeLogFile(tag, msg);
        return Log.d(tag, msg);
    }

    public static int i(String tag, String msg) {
        writeLogFile(tag, msg);
        return Log.i(tag, msg);
    }

    public static int w(String tag, String msg) {
        writeLogFile(tag, msg);
        return Log.w(tag, msg);
    }

    public static int e(String tag, String msg) {
        writeLogFile(tag, msg);
        return Log.e(tag, msg);
    }

    private static void writeLogFile(String tag, String message) {
        if (ENABLE_LOG_FILE) {

        }
    }


}
