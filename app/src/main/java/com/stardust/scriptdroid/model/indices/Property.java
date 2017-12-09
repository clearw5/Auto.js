package com.stardust.scriptdroid.model.indices;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stardust on 2017/12/9.
 */

public class Property {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("key")
    private String mKey;

    @SerializedName("summary")
    private String mSummary;

    @SerializedName("global")
    private boolean mGlobal;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public boolean isGlobal() {
        return mGlobal;
    }

    public void setGlobal(boolean global) {
        mGlobal = global;
    }
}
