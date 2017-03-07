package com.stardust.scriptdroid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.squareup.leakcanary.LeakCanary;
import com.stardust.scriptdroid.bounds_assist.BoundsAssistant;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.scriptdroid.record.AccessibilityRecorderDelegate;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.CrashHandler;
import com.stardust.util.StateObserver;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.lang.ref.WeakReference;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

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
        AccessibilityWatchDogService.addDelegateIfNeeded(300, BoundsAssistant.class);

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
