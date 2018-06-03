package org.autojs.autojs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;

import org.autojs.autojs.autojs.key.GlobalKeyObserver;

import java.util.concurrent.TimeUnit;

/**
 * Created by Stardust on 2017/1/31.
 */
public class Pref {

    private static final SharedPreferences DISPOSABLE_BOOLEAN = GlobalAppContext.get().getSharedPreferences("DISPOSABLE_BOOLEAN", Context.MODE_PRIVATE);
    private static final String KEY_SERVER_ADDRESS = "Still love you...17.5.14";
    private static final String KEY_SHOULD_SHOW_ANNUNCIATION = "Sing about all the things you forgot, things you are not";
    private static final String KEY_FIRST_SHOW_AD = "En, Today is 17.7.7, but, I'm still love you so....";
    private static final String KEY_LAST_SHOW_AD_MILLIS = "But... it seems that...you will not come back any more...";
    private static final String KEY_FLOATING_MENU_SHOWN = "17.10.28 I have idea of what you think...maybe...I'm overthinking...";
    private static final String KEY_EDITOR_THEME = "editor.theme";
    private static final String KEY_EDITOR_TEXT_SIZE = "editor.textSize";

    private static SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences p, String key) {
            if (key.equals(getString(R.string.key_guard_mode))) {
                AccessibilityConfig.setIsUnintendedGuardEnabled(p.getBoolean(getString(R.string.key_guard_mode), false));
            } else if ((key.equals(getString(R.string.key_use_volume_control_record)) || key.equals(getString(R.string.key_use_volume_control_running)))
                    && p.getBoolean(key, false)) {
                GlobalKeyObserver.init();
            }
        }
    };

    static {
        AccessibilityConfig.setIsUnintendedGuardEnabled(def().getBoolean(getString(R.string.key_guard_mode), false));
    }

    private static SharedPreferences def() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalAppContext.get());
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

    public static boolean isRunningVolumeControlEnabled() {
        return def().getBoolean(getString(R.string.key_use_volume_control_running), false);
    }

    public static boolean shouldEnableAccessibilityServiceByRoot() {
        return def().getBoolean(getString(R.string.key_enable_accessibility_service_by_root), false);
    }

    private static String getString(int id) {
        return GlobalAppContext.getString(id);
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

    public static boolean shouldShowAnnunciation() {
        return getDisposableBoolean(KEY_SHOULD_SHOW_ANNUNCIATION, true);
    }

    public static boolean shouldShowAd() {
        String adShowingMode = def().getString(getString(R.string.key_ad_showing_mode), "Default");
        switch (adShowingMode) {
            case "Default":
                return true;
            case "OncePerDay":
                long lastShowMillis = def().getLong(KEY_LAST_SHOW_AD_MILLIS, 0);
                if (System.currentTimeMillis() - lastShowMillis < TimeUnit.DAYS.toMillis(1)) {
                    return false;
                }
                def().edit().putLong(KEY_LAST_SHOW_AD_MILLIS, System.currentTimeMillis()).apply();
                return true;
        }
        return true;
    }

    public static boolean isFirstShowingAd() {
        return getDisposableBoolean(KEY_FIRST_SHOW_AD, true);
    }

    public static boolean isRecordWithRootEnabled() {
        //always return true after version 3.0.0
        //record without root has been deprecated
        return true;
    }

    public static boolean isRecordToastEnabled() {
        return def().getBoolean(getString(R.string.key_record_toast), true);
    }

    public static boolean rootRecordGeneratesBinary() {
        return def().getString(getString(R.string.key_root_record_out_file_type), "binary")
                .equals("binary");
    }

    public static boolean isObservingKeyEnabled() {
        return def().getBoolean(getString(R.string.key_enable_observe_key), false);
    }

    public static boolean isStableModeEnabled() {
        return def().getBoolean(getString(R.string.key_stable_mode), false);
    }

    public static String getDocumentationUrl() {
        String docSource = def().getString(getString(R.string.key_documentation_source), null);
        if (docSource == null || docSource.equals("Local")) {
            return "file:///android_asset/docs/";
        } else {
            return "https://www.autojs.org/assets/autojs/docs/";
        }
    }

    public static boolean isFloatingMenuShown() {
        return def().getBoolean(KEY_FLOATING_MENU_SHOWN, false);
    }

    public static void setFloatingMenuShown(boolean checked) {
        def().edit().putBoolean(KEY_FLOATING_MENU_SHOWN, checked).apply();
    }

    public static String getCurrentTheme() {
        return def().getString(KEY_EDITOR_THEME, null);
    }

    public static void setCurrentTheme(String theme) {
        def().edit().putString(KEY_EDITOR_THEME, theme).apply();
    }

    public static void setEditorTextSize(int value) {
        def().edit().putInt(KEY_EDITOR_TEXT_SIZE, value).apply();
    }

    public static int getEditorTextSize(int defValue) {
        return def().getInt(KEY_EDITOR_TEXT_SIZE, defValue);
    }

    public static String getScriptDirPath() {
        return def().getString(getString(R.string.key_script_dir_path),
                getString(R.string.default_value_script_dir_path));
    }
}
