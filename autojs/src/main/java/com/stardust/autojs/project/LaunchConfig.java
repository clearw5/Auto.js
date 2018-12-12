package com.stardust.autojs.project;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stardust on 2018/1/25.
 */

public class LaunchConfig {

    @SerializedName("hideLogs")
    private boolean mHideLogs = false;

    public boolean shouldHideLogs() {
        return mHideLogs;
    }

    public void setHideLogs(boolean hideLogs) {
        mHideLogs = hideLogs;
    }

}
