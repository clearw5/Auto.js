package com.stardust.util;

/**
 * Created by Stardust on 2017/2/2.
 */


import android.content.Intent;
import android.util.Log;

import com.stardust.scriptdroid.App;

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
            String t = "很抱歉，程序遇到未知错误，即将停止运行\n错误代码：" + ex.toString();
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
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
