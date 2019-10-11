package com.stardust.autojs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stardust on 2017/12/8.
 */

public class Config {

    private static Config sInstance;
    private SharedPreferences mSharedPreferences;
    private final Context mContext;

    public Config(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setInstance(Config instance) {
        if (sInstance != null)
            throw new IllegalStateException();
        sInstance = instance;
    }

    public static Config getInstance() {
        return sInstance;
    }

    public boolean isPrintJavaStackTraceEnabled() {
        return mSharedPreferences.getBoolean(getString(R.string.key_print_java_stack_trace), false);
    }

    private String getString(int resId) {
        return mContext.getString(resId);
    }


}
