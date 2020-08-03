
package org.autojs.autojs.network.entity.topic;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Privileges {

    @SerializedName("cid")
    private String mCid;
    @SerializedName("editable")
    private Boolean mEditable;
    @SerializedName("isAdminOrMod")
    private Boolean mIsAdminOrMod;
    @SerializedName("read")
    private Boolean mRead;
    @SerializedName("topics:create")
    private Boolean mTopicsCreate;
    @SerializedName("topics:read")
    private Boolean mTopicsRead;
    @SerializedName("topics:tag")
    private Boolean mTopicsTag;
    @SerializedName("uid")
    private Long mUid;
    @SerializedName("view_deleted")
    private Boolean mViewDeleted;

    public String getCid() {
        return mCid;
    }

    public void setCid(String cid) {
        mCid = cid;
    }

    public Boolean getEditable() {
        return mEditable;
    }

    public void setEditable(Boolean editable) {
        mEditable = editable;
    }

    public Boolean getIsAdminOrMod() {
        return mIsAdminOrMod;
    }

    public void setIsAdminOrMod(Boolean isAdminOrMod) {
        mIsAdminOrMod = isAdminOrMod;
    }

    public Boolean getRead() {
        return mRead;
    }

    public void setRead(Boolean read) {
        mRead = read;
    }

    public Boolean getTopicsCreate() {
        return mTopicsCreate;
    }

    public void setTopicsCreate(Boolean topicsCreate) {
        mTopicsCreate = topicsCreate;
    }

    public Boolean getTopicsRead() {
        return mTopicsRead;
    }

    public void setTopicsRead(Boolean topicsRead) {
        mTopicsRead = topicsRead;
    }

    public Boolean getTopicsTag() {
        return mTopicsTag;
    }

    public void setTopicsTag(Boolean topicsTag) {
        mTopicsTag = topicsTag;
    }

    public Long getUid() {
        return mUid;
    }

    public void setUid(Long uid) {
        mUid = uid;
    }

    public Boolean getViewDeleted() {
        return mViewDeleted;
    }

    public void setViewDeleted(Boolean viewDeleted) {
        mViewDeleted = viewDeleted;
    }

}
