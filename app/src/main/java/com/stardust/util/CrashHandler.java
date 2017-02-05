package com.stardust.util;

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
    private final Class<?> mErrorReportClass;

    public CrashHandler(Class<?> errorReportClass) {
        this.mErrorReportClass = errorReportClass;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            Log.e("CrashHandler", "Uncaught Exception!!!");
            ex.printStackTrace();
            String t = App.getApp().getString(R.string.sorry_for_crash) + ex.toString();
            Intent intent = new Intent(App.getApp(), this.mErrorReportClass);
            intent.putExtra("message", t);
            intent.putExtra("error", throwableToString(ex));
            App.getApp().startActivity(intent);
            System.exit(0);
        } catch (Throwable var6) {
            var6.printStackTrace();
        }

    }

    public static String throwableToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace();
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
