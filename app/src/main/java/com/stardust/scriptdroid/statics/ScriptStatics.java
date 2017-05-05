package com.stardust.scriptdroid.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.flurry.android.FlurryAgent;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.scriptdroid.BuildConfig;

/**
 * Created by Stardust on 2017/5/5.
 */

public class ScriptStatics {


    private static final String KEY_MILLIS = "Sorry, I should have left";
    private static ScriptStaticsStorage storage;
    private static SharedPreferences preferences;

    public static void init(Context context) {
        storage = new SQLiteStaticsStorage(context);
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .build(context, "D42MH48ZN4PJC5TKNYZD");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void recordScript(ScriptSource source) {
        storage.record(source);
        sendStaticsIfNeeded();
    }

    private static void sendStaticsIfNeeded() {
        long millis = preferences.getLong(KEY_MILLIS, 0);
        if (!DateUtils.isToday(millis)) {
            preferences.edit().putLong(KEY_MILLIS, System.currentTimeMillis()).apply();
            FlurryAgent.logEvent("ScriptStatics", storage.getMax(10));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        storage.close();
    }
}
