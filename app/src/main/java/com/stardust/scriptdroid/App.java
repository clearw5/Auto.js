package com.stardust.scriptdroid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.scriptdroid.accessibility.AccessibilityInfoProvider;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.RhinoJavaScriptEngine;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.record.accessibility.AccessibilityActionRecorder;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.squareup.leakcanary.LeakCanary;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.CrashHandler;
import com.stardust.util.StateObserver;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

    private static final String TAG = "App";

    private static App instance;
    private static StateObserver stateObserver;
    private static Activity currentActivity;

    public static App getApp() {
        return instance;
    }

    public static StateObserver getStateObserver() {
        return stateObserver;
    }


    public void onCreate() {
        super.onCreate();
        setUpDebugEnvironment();
        init();
        configApp();
        registerActivityLifecycleCallback();
        initAccessibilityServiceDelegates();
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
        instance = this;
        stateObserver = new StateObserver(PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void configApp() {
        ScriptFileList.setImpl(SharedPrefScriptFileList.getInstance());
        JavaScriptEngine.setDefault(new RhinoJavaScriptEngine(DroidRuntime.getRuntime()));
    }


    private void initAccessibilityServiceDelegates() {
        AccessibilityWatchDogService.addDelegateIfNeeded(100, ActionPerformAccessibilityDelegate.class);
        AccessibilityWatchDogService.addDelegateIfNeeded(200, AccessibilityActionRecorder.getInstance());
        AccessibilityWatchDogService.addDelegateIfNeeded(300, AccessibilityEventCommandHost.getInstance());
        AccessibilityWatchDogService.addDelegateIfNeeded(400, AccessibilityInfoProvider.getInstance());
        AccessibilityWatchDogService.addDelegateIfNeeded(500, LayoutInspector.getInstance());

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


    private static class SimpleActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
