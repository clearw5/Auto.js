package org.autojs.autojs.network.entity;

import android.text.TextUtils;


import org.autojs.autojs.BuildConfig;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Stardust on 2017/9/20.
 */

public class VersionInfo {

    public int versionCode;
    public String releaseNotes;
    public String versionName;
    public List<Download> downloads;
    public List<OldVersion> oldVersions;
    public int deprecated;
    public String downloadUrl;

    public boolean isValid() {
        return downloads != null && !downloads.isEmpty() && versionCode > 0
                && !TextUtils.isEmpty(versionName) && !TextUtils.isEmpty(releaseNotes);
    }

    public OldVersion getOldVersion(int versionCode) {
        for (OldVersion oldVersion : oldVersions) {
            if (oldVersion.versionCode == versionCode) {
                return oldVersion;
            }
        }
        return null;
    }

    public boolean isNewer() {
        return versionCode > BuildConfig.VERSION_CODE;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "versionCode=" + versionCode +
                ", releaseNotes='" + releaseNotes + '\'' +
                ", versionName='" + versionName + '\'' +
                ", downloads=" + downloads +
                ", oldVersions=" + oldVersions +
                ", deprecated=" + deprecated +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }


    public static class OldVersion extends JSONObject {

        public int versionCode;
        public String issues;

        @Override
        public String toString() {
            return "OldVersion{" +
                    "versionCode=" + versionCode +
                    ", issues='" + issues + '\'' +
                    '}';
        }
    }

    public static class Download extends JSONObject {

        public String name;
        public String url;

        @Override
        public String toString() {
            return "Download{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
