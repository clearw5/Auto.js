
package org.autojs.autojs.network.entity.topic;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class AppInfo {

    public static final String PERMISSION_ROOT = "root";

    @SerializedName("details")
    private String mDetails;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("file")
    private String mFile;
    @SerializedName("minSdkVersion")
    private long mMinSdkVersion;
    @SerializedName("name")
    private String mName;
    @SerializedName("package")
    private String mPackage;
    @SerializedName("permissions")
    private List<String> mPermissions;
    @SerializedName("qq")
    private String mQq;
    @SerializedName("releaseNotes")
    private String mReleaseNotes;
    @SerializedName("screenshots")
    private List<String> mScreenshots;
    @SerializedName("summary")
    private String mSummary;
    @SerializedName("userGuide")
    private String mUserGuide;
    @SerializedName("version")
    private String mVersion;
    @SerializedName("versionCode")
    private long mVersionCode;

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFile() {
        return mFile;
    }

    public void setFile(String file) {
        mFile = file;
    }

    public long getMinSdkVersion() {
        return mMinSdkVersion;
    }

    public void setMinSdkVersion(long minSdkVersion) {
        mMinSdkVersion = minSdkVersion;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String packageName) {
        mPackage = packageName;
    }

    public List<String> getPermissions() {
        return mPermissions;
    }

    public void setPermissions(List<String> permissions) {
        mPermissions = permissions;
    }

    public String getQq() {
        return mQq;
    }

    public void setQq(String qq) {
        mQq = qq;
    }

    public String getReleaseNotes() {
        return mReleaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        mReleaseNotes = releaseNotes;
    }

    public List<String> getScreenshots() {
        return mScreenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        mScreenshots = screenshots;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getUserGuide() {
        return mUserGuide;
    }

    public void setUserGuide(String userGuide) {
        mUserGuide = userGuide;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public long getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(long versionCode) {
        mVersionCode = versionCode;
    }

    @Override
    public String toString() {
        return "ScriptApp{" +
                "mDetails='" + mDetails + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mFile='" + mFile + '\'' +
                ", mMinSdkVersion=" + mMinSdkVersion +
                ", mName='" + mName + '\'' +
                ", mPackage='" + mPackage + '\'' +
                ", mPermissions=" + mPermissions +
                ", mQq='" + mQq + '\'' +
                ", mReleaseNotes='" + mReleaseNotes + '\'' +
                ", mScreenshots=" + mScreenshots +
                ", mSummary='" + mSummary + '\'' +
                ", mUserGuide='" + mUserGuide + '\'' +
                ", mVersion='" + mVersion + '\'' +
                ", mVersionCode=" + mVersionCode +
                '}';
    }
}
