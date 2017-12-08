package com.stardust.autojs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stardust on 2017/12/8.
 */

public class Pref {


    private static Pref sInstance;
    private SharedPreferences mSharedPreferences;

    public Pref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setInstance(Pref instance) {
        if (sInstance != null)
            throw new IllegalStateException();
        sInstance = instance;
    }

    public static Pref getInstance() {
        return sInstance;
    }


}
