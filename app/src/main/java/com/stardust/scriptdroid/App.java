package com.stardust.scriptdroid;

import android.app.Application;

import com.stardust.util.CrashHandler;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

    private static App instance;

    public static App getApp() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
        instance = this;
    }
}
