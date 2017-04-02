package com.stardust.scriptdroid.tool;

/**
 * Created by Stardust on 2017/2/2.
 */


import android.content.Intent;
import android.util.Log;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static int crashCount = 0;
    private static long firstCrashMillis = 0;
    private final Class<?> mErrorReportClass;

    public CrashHandler(Class<?> errorReportClass) {
        this.mErrorReportClass = errorReportClass;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            Log.e(TAG, "Uncaught Exception", ex);
            if (crashTooManyTimes())
                return;
            String msg = App.getApp().getString(R.string.sorry_for_crash) + ex.toString();
            startErrorReportActivity(msg, throwableToString(ex));
            System.exit(0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void startErrorReportActivity(String msg, String detail) {
        Intent intent = new Intent(App.getApp(), this.mErrorReportClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", msg);
        intent.putExtra("error", detail);
        App.getApp().startActivity(intent);
    }

    private boolean crashTooManyTimes() {
        if (crashIntervalTooLong()) {
            resetCrashCount();
            return false;
        }
        crashCount++;
        return crashCount >= 5;
    }

    private void resetCrashCount() {
        firstCrashMillis = System.currentTimeMillis();
        crashCount = 0;
    }

    private boolean crashIntervalTooLong() {
        return System.currentTimeMillis() - firstCrashMillis > 3000;
    }

    public static String throwableToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace();
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}