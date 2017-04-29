package com.stardust.scriptdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Keep;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.app.VolumeChangeObserver;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.CrashHandler;
import com.stardust.scriptdroid.tool.JsBeautifierFactory;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.ScreenMetrics;

import java.lang.ref.WeakReference;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends MultiDexApplication {

    private static final String TAG = "App";

    private static WeakReference<App> instance;
    private static WeakReference<Activity> currentActivity;

    public static App getApp() {
        return instance.get();
    }

    private VolumeChangeObserver mVolumeChangeObserver = new VolumeChangeObserver();

    public void onCreate() {
        super.onCreate();
        instance = new WeakReference<>(this);
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
        JsBeautifierFactory.initJsBeautify(this, "js/jsbeautify.js");
        initVolumeChangeObserver();
        startService(new Intent(this, AccessibilityWatchDogService.class));
    }

    private void initVolumeChangeObserver() {
        registerReceiver(mVolumeChangeObserver, new IntentFilter(VolumeChangeObserver.ACTION_VOLUME_CHANGE));
        mVolumeChangeObserver.addOnVolumeChangeListener(new VolumeChangeObserver.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange() {
                if (Pref.isRunningVolumeControlEnabled()) {
                    AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
                }
            }
        });
    }

    private void registerActivityLifecycleCallback() {
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                ScreenMetrics.initIfNeeded(activity);
                currentActivity = new WeakReference<>(activity);
            }

        });
    }

    @Keep
    public static Activity currentActivity() {
        return currentActivity.get();
    }

    public static String getResString(int id) {
        return getApp().getString(id);
    }

    public VolumeChangeObserver getVolumeChangeObserver() {
        return mVolumeChangeObserver;
    }
}
