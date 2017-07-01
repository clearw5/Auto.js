package com.stardust.auojs.inrt;

import android.app.Application;

import com.stardust.auojs.inrt.rt.AutoJs;

/**
 * Created by Stardust on 2017/7/1.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AutoJs.initInstance(this);
    }
}
