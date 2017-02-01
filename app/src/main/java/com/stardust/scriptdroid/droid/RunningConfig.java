package com.stardust.scriptdroid.droid;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Stardust on 2017/2/1.
 */
public class RunningConfig {

    private static final RunningConfig RUNNING_CONFIG = new RunningConfig();

    public static RunningConfig getDefault() {
        return RUNNING_CONFIG;
    }

    public boolean runInNewThread = true;
    public Activity activity;
    public Context context;

    public RunningConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public RunningConfig activity(Activity activity) {
        this.activity = activity;
        this.context = activity;
        return this;
    }

    public RunningConfig context(Context context) {
        this.context = context;
        return this;
    }


}
