package com.stardust.scriptdroid;

import android.app.Activity;
import android.app.Application;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.Log;

import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.scriptdroid.accessibility.AccessibilityInfoProvider;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.record.AccessibilityRecorderDelegate;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.squareup.leakcanary.LeakCanary;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.scriptdroid.tool.ViewTool;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.CrashHandler;
import com.stardust.util.StateObserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

    private static final String TAG = "App";

    private static WeakReference<App> instance;
    private static StateObserver stateObserver;
    private static WeakReference<Activity> currentActivity;

    public static App getApp() {
        return instance.get();
    }

    public static StateObserver getStateObserver() {
        return stateObserver;
    }


    public void onCreate() {
        super.onCreate();
        ThemeColorManager.init(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        if (!BuildConfig.DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
        instance = new WeakReference<>(this);
        stateObserver = new StateObserver(PreferenceManager.getDefaultSharedPreferences(this));
        registerActivityLifecycleCallback();
        initAccessibilityServiceDelegates();
    }


    private void initAccessibilityServiceDelegates() {
        AccessibilityWatchDogService.addDelegateIfNeeded(100, ActionPerformAccessibilityDelegate.class);
        AccessibilityWatchDogService.addDelegateIfNeeded(200, AccessibilityRecorderDelegate.getInstance());
        AccessibilityWatchDogService.addDelegateIfNeeded(300, AccessibilityEventCommandHost.instance);
        AccessibilityWatchDogService.addDelegateIfNeeded(400, AccessibilityInfoProvider.instance);
        AccessibilityWatchDogService.addDelegateIfNeeded(500, LayoutInspector.getInstance());

    }

    private void registerActivityLifecycleCallback() {
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                currentActivity = new WeakReference<>(activity);
            }


            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }

        });
    }

    public static Activity currentActivity() {
        return currentActivity.get();
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
