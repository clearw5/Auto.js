package com.stardust.scriptdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stardust on 2017/1/31.
 */
public class Pref {

    private static final SharedPreferences DISPOSABLE_BOOLEAN = App.getApp().getSharedPreferences("DISPOSABLE_BOOLEAN", Context.MODE_PRIVATE);
    public static final String SAMPLE_SCRIPTS_COPIED = "SAMPLE_SCRIPTS_COPIED";

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

    public static int oldVersion(){
        return 0;
    }
}
