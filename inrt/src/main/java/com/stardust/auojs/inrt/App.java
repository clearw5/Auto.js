package com.stardust.auojs.inrt;

import android.app.Application;

import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.auojs.inrt.autojs.GlobalKeyObserver;

/**
 * Created by Stardust on 2017/7/1.
 */

public class App extends Application {

    private static App sApp;

    public static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        AutoJs.initInstance(this);
        GlobalKeyObserver.init();
    }

}
