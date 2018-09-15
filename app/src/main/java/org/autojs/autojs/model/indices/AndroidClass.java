package org.autojs.autojs.model.indices;

import com.google.gson.annotations.SerializedName;

public class AndroidClass {

    @SerializedName("class_name")
    private String mClassName;
    @SerializedName("package_name")
    private String mPackageName;
    @SerializedName("full_name")
    private String mFullName;

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }
}
