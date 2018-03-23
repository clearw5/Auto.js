package com.stardust.auojs.inrt;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stardust.app.GlobalAppContext;

/**
 * Created by Stardust on 2017/12/8.
 */

public class Pref {

    private static final String KEY_FIRST_USING = "key_first_using";
    private static SharedPreferences sPreferences;

    public static SharedPreferences getPreferences() {
        if (sPreferences == null)
            sPreferences = PreferenceManager.getDefaultSharedPreferences(GlobalAppContext.get());
        return sPreferences;
    }

    public static boolean isStableModeEnabled() {
        return getPreferences().getBoolean(getString(R.string.key_stable_mode), false);
    }

    private static String getString(int res) {
        return GlobalAppContext.getString(res);
    }

    public static boolean shouldEnableAccessibilityServiceByRoot() {
        return getPreferences().getBoolean(getString(R.string.key_enable_accessibility_service_by_root), false);
    }

    public static boolean shouldHideLogs() {
        return getPreferences().getBoolean(getString(R.string.key_dont_show_main_activity), false);
    }

    public static boolean shouldStopAllScriptsWhenVolumeUp() {
        return getPreferences().getBoolean(getString(R.string.key_use_volume_control_running), true);
    }

    public static boolean isFirstUsing() {
        boolean firstUsing = getPreferences().getBoolean(KEY_FIRST_USING, true);
        if (firstUsing) {
            getPreferences().edit().putBoolean(KEY_FIRST_USING, false).apply();
        }
        return firstUsing;
    }
}
