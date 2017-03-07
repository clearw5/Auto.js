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
    private static final String KEY_MAX_TEXT_LENGTH_FOR_CODE_COMPLETION = "KEY_MAX_TEXT_LENGTH_FOR_CODE_COMPLETION";
    public static final String KEY_DRAWER_HEADER_IMAGE_PATH = "KEY_DRAWER_HEADER_IMAGE_PATH";

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
}
