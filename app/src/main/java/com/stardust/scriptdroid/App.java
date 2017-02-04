package com.stardust.scriptdroid;

import android.app.Application;
import android.preference.PreferenceManager;

import com.stardust.util.CrashHandler;
import com.stardust.util.StateObserver;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

    private static App instance;
    private static StateObserver stateObserver;

    public static App getApp() {
        return instance;
    }

    public static StateObserver getStateObserver() {
        return stateObserver;
    }


    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
        instance = this;
        stateObserver = new StateObserver(PreferenceManager.getDefaultSharedPreferences(this));
    }
}
