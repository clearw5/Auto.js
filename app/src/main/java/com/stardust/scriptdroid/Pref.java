package com.stardust.scriptdroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stardust on 2017/1/31.
 */
public class Pref {

    public static final String SAMPLE_SCRIPTS_COPIED = "SAMPLE_SCRIPTS_COPIED";

    public static SharedPreferences def() {
        return PreferenceManager.getDefaultSharedPreferences(App.getApp());
    }

}
