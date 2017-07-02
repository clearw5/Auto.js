package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stardust.scriptdroid.BuildConfig;
import com.stardust.util.NetworkUtils;

/**
 * Created by Stardust on 2017/4/9.
 */

public class VersionInfo {


    private static final String KEY_DEPRECATED = "Still loving you...Can we go back...";
    private static final String KEY_DEPRECATED_VERSION_CODE = "I miss you so much tonight...Baby don't let me cry...";

    public interface OnReceiveUpdateResultCallback {
        void onReceive(UpdateChecker.UpdateInfo info, boolean isCurrentVersionDeprecated);
    }


    private static VersionInfo instance = new VersionInfo();

    public static VersionInfo getInstance() {
        return instance;
    }

    private boolean mDeprecated = false;
    private UpdateChecker.UpdateInfo mUpdateInfo;
    private OnReceiveUpdateResultCallback mOnReceiveUpdateResultCallback;
    private SharedPreferences mSharedPreferences;

    public void readDeprecatedFromPref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (mSharedPreferences.getInt(KEY_DEPRECATED_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            mSharedPreferences.edit().remove(KEY_DEPRECATED_VERSION_CODE)
                    .putBoolean(KEY_DEPRECATED, false)
                    .apply();
        }
        mDeprecated = mSharedPreferences.getBoolean(KEY_DEPRECATED, false);
    }


    public void readDeprecatedFromPrefIfNeeded(Context context) {
        if (mSharedPreferences == null) {
            readDeprecatedFromPref(context);
        }
    }

    public boolean isCurrentVersionDeprecated() {
        return mDeprecated;
    }

    public UpdateChecker.UpdateInfo getUpdateInfo() {
        return mUpdateInfo;
    }

    public String getCurrentVersionIssues() {
        if (mUpdateInfo == null)
            return null;
        UpdateChecker.OldVersion oldVersion = mUpdateInfo.getOldVersion(BuildConfig.VERSION_CODE);
        if (oldVersion == null)
            return null;
        return oldVersion.issues;
    }

    public void checkUpdateIfNeeded(Context context) {
        if (mUpdateInfo == null) {
            checkUpdateIfUsingWifi(context);
        }
    }

    public void setOnReceiveUpdateResultCallback(OnReceiveUpdateResultCallback onReceiveUpdateResultCallback) {
        mOnReceiveUpdateResultCallback = onReceiveUpdateResultCallback;
    }

    private void checkUpdateIfUsingWifi(Context context) {
        if (NetworkUtils.isWifiAvailable(context)) {
            checkUpdate(context);
        }

    }

    public void checkUpdate(Context context) {
        checkUpdateInner(context);
    }

    private void checkUpdateInner(final Context context) {
        new UpdateChecker(context).check(new UpdateChecker.Callback() {

            @Override
            public void onSuccess(UpdateChecker.UpdateInfo result) {
                if (result.isValid()) {
                    setUpdateInfo(result);
                }
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void setUpdateInfo(UpdateChecker.UpdateInfo result) {
        mDeprecated = BuildConfig.VERSION_CODE <= result.deprecated;
        mUpdateInfo = result;
        if (mDeprecated) {
            mSharedPreferences.edit().putBoolean(KEY_DEPRECATED, mDeprecated)
                    .putInt(KEY_DEPRECATED_VERSION_CODE, BuildConfig.VERSION_CODE)
                    .apply();
        }
        if (mOnReceiveUpdateResultCallback != null) {
            mOnReceiveUpdateResultCallback.onReceive(result, mDeprecated);
        }
    }

}
