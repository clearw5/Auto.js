package com.stardust.scriptdroid;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.service.VolumeChangeObverseService;
import com.stardust.scriptdroid.tool.CrashHandler;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

    private static final String TAG = "App";

    private static App instance;
    private static Activity currentActivity;

    public static App getApp() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        setUpDebugEnvironment();
        init();
        registerActivityLifecycleCallback();
    }

    private void setUpDebugEnvironment() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        if (!BuildConfig.DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
    }

    private void init() {
        ThemeColorManager.setDefaultThemeColor(new ThemeColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent)));
        ThemeColorManager.init(this);
        AutoJs.initInstance(this);
        VolumeChangeObverseService.addOnVolumeChangeListener(new VolumeChangeObverseService.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange() {
                if (Pref.isRunningVolumeControlEnabled()) {
                    AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
                }
            }
        });
        startService(new Intent(this, VolumeChangeObverseService.class));
    }

    private void registerActivityLifecycleCallback() {
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                currentActivity = null;
            }

        });
    }

    public static Activity currentActivity() {
        return currentActivity;
    }

    public static String getResString(int id) {
        return getApp().getString(id);
    }

}
