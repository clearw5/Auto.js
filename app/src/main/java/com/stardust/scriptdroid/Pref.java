package com.stardust.scriptdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stardust.autojs.runtime.api.AutomatorConfig;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.scriptdroid.autojs.AutoJs;

import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.GuiCallback;

/**
 * Created by Stardust on 2017/1/31.
 */
public class Pref {

    private static final SharedPreferences DISPOSABLE_BOOLEAN = App.getApp().getSharedPreferences("DISPOSABLE_BOOLEAN", Context.MODE_PRIVATE);
    private static final String KEY_SERVER_ADDRESS = "Still love you...17.5.14";
    private static SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.key_run_mode))) {
                AutoJs.getInstance().getCommandHost().setRunMode(getRunModeFromValue(sharedPreferences.getString(key, null)));
            } else if (key.equals(getString(R.string.key_guard_mode))) {
                AutomatorConfig.setIsUnintendedGuardEnabled(sharedPreferences.getBoolean(getString(R.string.key_guard_mode), false));
            }
        }
    };

    static {
        AutomatorConfig.setIsUnintendedGuardEnabled(def().getBoolean(getString(R.string.key_guard_mode), false));
    }

    private static int getRunModeFromValue(String value) {
        if (value == null)
            return AccessibilityEventCommandHost.RUN_MODE_THREAD_POOL;
        switch (value) {
            case "KEY_THREAD_POOL":
                return AccessibilityEventCommandHost.RUN_MODE_THREAD_POOL;
            case "KEY_NEW_THREAD_EVERY_TIME":
                return AccessibilityEventCommandHost.RUN_MODE_NEW_THREAD_EVERY_TIME;
            default:
                return AccessibilityEventCommandHost.RUN_MODE_SINGLE_THREAD;
        }
    }

    private static SharedPreferences def() {
        return PreferenceManager.getDefaultSharedPreferences(App.getApp());
    }

    private static boolean getDisposableBoolean(String key, boolean defaultValue) {
        boolean b = DISPOSABLE_BOOLEAN.getBoolean(key, defaultValue);
        if (b == defaultValue) {
            DISPOSABLE_BOOLEAN.edit().putBoolean(key, !defaultValue).apply();
        }
        return b;
    }

    public static boolean isFirstGoToAccessibilitySetting() {
        return getDisposableBoolean("I miss you so much ...", true);
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

    public static String getStartRecordTrigger() {
        return def().getString(getString(R.string.key_start_record_trigger), null);
    }

    public static String getStopRecordTrigger() {
        return def().getString(getString(R.string.key_stop_record_trigger), null);
    }

    public static boolean hasRecordTrigger() {
        String startTrigger = getStartRecordTrigger();
        String stopTrigger = getStartRecordTrigger();
        return startTrigger != null && !startTrigger.equals("NONE")
                && stopTrigger != null && !startTrigger.equals("NONE");
    }

    public static boolean enableAccessibilityServiceByRoot() {
        return def().getBoolean(getString(R.string.key_enable_accessibility_service_by_root), false);
    }

    private static String getString(int id) {
        return App.getResString(id);
    }

    public static int getMaxTextLengthForCodeCompletion() {
        try {
            return Integer.parseInt(def().getString(App.getApp().getString(R.string.key_max_length_for_code_completion), "2000"));
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public static boolean isFirstUsing() {
        return getDisposableBoolean("isFirstUsing", true);
    }

    static {
        def().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static boolean isEditActivityFirstUsing() {
        return getDisposableBoolean("Still Love Eating 17.4.6", true);
    }

    public static String getServerAddressOrDefault(String defaultAddress) {
        return def().getString(KEY_SERVER_ADDRESS, defaultAddress);
    }

    public static void saveServerAddress(String address) {
        def().edit().putString(KEY_SERVER_ADDRESS, address).apply();
    }
}
