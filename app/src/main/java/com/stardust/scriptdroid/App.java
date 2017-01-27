package com.stardust.scriptdroid;

import android.app.Application;

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
        instance = this;
    }
}
