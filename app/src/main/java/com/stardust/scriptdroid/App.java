package com.stardust.scriptdroid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.stardust.util.CrashHandler;
import com.stardust.util.StateObserver;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends Application {

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
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
        instance = this;
        stateObserver = new StateObserver(PreferenceManager.getDefaultSharedPreferences(this));
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

        });
    }

    public static Activity currentActivity() {
        return currentActivity;
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
