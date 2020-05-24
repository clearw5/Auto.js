package com.stardust.autojs.project;


import com.google.gson.annotations.SerializedName;

import java.util.zip.CRC32;

public class BuildInfo {

    @SerializedName("build_time")
    private long mBuildTime;

    @SerializedName("build_id")
    private String mBuildId;

    @SerializedName("build_number")
    private long mBuildNumber;

    public BuildInfo() {
    }

    public long getBuildNumber() {
        return mBuildNumber;
    }

    public void setBuildNumber(long buildNumber) {
        mBuildNumber = buildNumber;
    }

    public long getBuildTime() {
        return mBuildTime;
    }

    public void setBuildTime(long buildTime) {
        mBuildTime = buildTime;
    }

    public String getBuildId() {
        return mBuildId;
    }

    public void setBuildId(String buildId) {
        mBuildId = buildId;
    }

    public static BuildInfo generate(long buildNumber) {
        BuildInfo info = new BuildInfo();
        info.setBuildNumber(buildNumber);
        info.setBuildTime(System.currentTimeMillis());
        info.setBuildId(generateBuildId(buildNumber, info.getBuildTime()));
        return info;
    }

    private static String generateBuildId(long buildNumber, long buildTime) {
        CRC32 crc32 = new CRC32();
        crc32.update((buildNumber + "" + buildTime).getBytes());
        return String.format("%08X", crc32.getValue()) + "-" + buildNumber;
    }
}
