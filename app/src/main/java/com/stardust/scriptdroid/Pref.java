package com.stardust.scriptdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.scriptdroid.autojs.AutoJs;

/**
 * Created by Stardust on 2017/1/31.
 */
public class Pref {

    private static final SharedPreferences DISPOSABLE_BOOLEAN = App.getApp().getSharedPreferences("DISPOSABLE_BOOLEAN", Context.MODE_PRIVATE);
    public static final String KEY_DRAWER_HEADER_IMAGE_PATH = "KEY_DRAWER_HEADER_IMAGE_PATH";
    public static final String KEY_APP_BAR_IMAGE_PATH = "KEY_APP_BAR_IMAGE_PATH";
    private static SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(App.getResString(R.string.key_run_mode))) {
                AutoJs.getInstance().getCommandHost().setRunMode(getRunModeFromValue(sharedPreferences.getString(key, null)));
            }
        }
    };

    private static int getRunModeFromValue(String value) {
        switch (value) {
            case "KEY_THREAD_POOL":
                return AccessibilityEventCommandHost.RUN_MODE_THREAD_POOL;
            case "KEY_NEW_THREAD_EVERY_TIME":
                return AccessibilityEventCommandHost.RUN_MODE_NEW_THREAD_EVERY_TIME;
            default:
                return AccessibilityEventCommandHost.RUN_MODE_SINGLE_THREAD;
        }
    }

    public static SharedPreferences def() {
        return PreferenceManager.getDefaultSharedPreferences(App.getApp());
    }

    public static boolean isFirstEnableAssistMode() {
        return getDisposableBoolean("isFirstEnableAssistMode", true);
    }

    private static boolean getDisposableBoolean(String key, boolean defaultValue) {
        boolean b = DISPOSABLE_BOOLEAN.getBoolean(key, defaultValue);
        if (b == defaultValue) {
            DISPOSABLE_BOOLEAN.edit().putBoolean(key, !defaultValue).apply();
        }
        return b;
    }

    public static boolean isFirstGoToAccessibilitySetting() {
        return getDisposableBoolean("isFirstGoToAccessibilitySetting", true);
    }

    public static int oldVersion() {
        return 0;
    }

    public static boolean isRecordVolumeControlEnable() {
        return def().getBoolean(getString(R.string.key_use_volume_control_record), false);
    }

    public static boolean isRunningVolumeControlEnabled() {
        return def().getBoolean(getString(R.string.key_use_volume_control_running), false);
    }


    private static String getString(int id) {
        return App.getApp().getString(id);
    }

    public static int MaxTextLengthForCodeCompletion() {
        try {
            return Integer.parseInt(def().getString(App.getApp().getString(R.string.key_max_length_for_code_completion), "2000"));
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public static boolean isFirstUsing() {
        return getDisposableBoolean("isFirstUsing", true);
    }

    public static String getDrawerHeaderImagePath() {
        return def().getString(KEY_DRAWER_HEADER_IMAGE_PATH, null);
    }

    public static void setDrawerHeaderImagePath(String path) {
        def().edit().putString(KEY_DRAWER_HEADER_IMAGE_PATH, path).apply();
    }

    public static String getAppBarImagePath() {
        return def().getString(KEY_APP_BAR_IMAGE_PATH, null);
    }

    public static void setAppBarImagePath(String path) {
        def().edit().putString(KEY_APP_BAR_IMAGE_PATH, path).apply();
    }

    static {
        def().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
}
