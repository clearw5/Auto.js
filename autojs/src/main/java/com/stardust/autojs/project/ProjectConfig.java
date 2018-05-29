package com.stardust.autojs.project;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2018/1/24.
 */

public class ProjectConfig {

    public static final String CONFIG_FILE_NAME = "project.json";

    private static final Gson GSON = new Gson();

    @SerializedName("name")
    private String mName;

    @SerializedName("versionName")
    private String mVersionName;

    @SerializedName("versionCode")
    private int mVersionCode;

    @SerializedName("packageName")
    private String mPackageName;

    @SerializedName("main")
    private String mMainScriptFile;

    @SerializedName("assets")
    private List<String> mAssets = new ArrayList<>();

    @SerializedName("launchConfig")
    private LaunchConfig mLaunchConfig;

    public static ProjectConfig fromJson(String json) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, ProjectConfig.class);
    }


    public static ProjectConfig fromAssets(Context context, String path) {
        try {
            return fromJson(PFiles.read(context.getAssets().open(path)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProjectConfig fromFile(String path) {
        try {
            return fromJson(PFiles.read(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProjectConfig fromProjectDir(String path) {
        return fromFile(configFileOfDir(path));
    }


    public static String configFileOfDir(String projectDir) {
        return PFiles.join(projectDir, CONFIG_FILE_NAME);
    }



    public String getName() {
        return mName;
    }

    public ProjectConfig setName(String name) {
        mName = name;
        return this;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public ProjectConfig setVersionName(String versionName) {
        mVersionName = versionName;
        return this;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public ProjectConfig setVersionCode(int versionCode) {
        mVersionCode = versionCode;
        return this;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public ProjectConfig setPackageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public String getMainScriptFile() {
        return mMainScriptFile;
    }

    public ProjectConfig setMainScriptFile(String mainScriptFile) {
        mMainScriptFile = mainScriptFile;
        return this;
    }

    public List<String> getAssets() {
        if (mAssets == null) {
            mAssets = new ArrayList<>();
        }
        return mAssets;
    }

    public void setAssets(List<String> assets) {
        mAssets = assets;
    }

    public LaunchConfig getLaunchConfig() {
        if (mLaunchConfig == null) {
            mLaunchConfig = new LaunchConfig();
        }
        return mLaunchConfig;
    }

    public void setLaunchConfig(LaunchConfig launchConfig) {
        mLaunchConfig = launchConfig;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

}
