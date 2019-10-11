
package org.autojs.autojs.network.entity.topic;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Category {

    @SerializedName("cid")
    private String mCid;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("descriptionParsed")
    private String mDescriptionParsed;
    @SerializedName("link")
    private String mLink;
    @SerializedName("loggedIn")
    private Boolean mLoggedIn;
    @SerializedName("name")
    private String mName;
    @SerializedName("nextStart")
    private Long mNextStart;
    @SerializedName("numRecentReplies")
    private String mNumRecentReplies;
    @SerializedName("pagination")
    private Pagination mPagination;
    @SerializedName("parentCid")
    private String mParentCid;
    @SerializedName("post_count")
    private String mPostCount;
    @SerializedName("privileges")
    private Privileges mPrivileges;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("topic_count")
    private String mTopicCount;
    @SerializedName("topics")
    private List<Topic> mTopics;
    @SerializedName("totalPostCount")
    private Long mTotalPostCount;
    @SerializedName("totalTopicCount")
    private Long mTotalTopicCount;
    @SerializedName("url")
    private String mUrl;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescriptionParsed() {
        return mDescriptionParsed;
    }

    public void setDescriptionParsed(String descriptionParsed) {
        mDescriptionParsed = descriptionParsed;
    }
    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public Boolean getLoggedIn() {
        return mLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        mLoggedIn = loggedIn;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getNextStart() {
        return mNextStart;
    }

    public void setNextStart(Long nextStart) {
        mNextStart = nextStart;
    }

    public String getNumRecentReplies() {
        return mNumRecentReplies;
    }

    public void setNumRecentReplies(String numRecentReplies) {
        mNumRecentReplies = numRecentReplies;
    }

    public String getParentCid() {
        return mParentCid;
    }

    public void setParentCid(String parentCid) {
        mParentCid = parentCid;
    }

    public String getPostCount() {
        return mPostCount;
    }

    public void setPostCount(String postCount) {
        mPostCount = postCount;
    }

    public Privileges getPrivileges() {
        return mPrivileges;
    }

    public void setPrivileges(Privileges privileges) {
        mPrivileges = privileges;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTopicCount() {
        return mTopicCount;
    }

    public void setTopicCount(String topicCount) {
        mTopicCount = topicCount;
    }

    public List<Topic> getTopics() {
        return mTopics;
    }

    public void setTopics(List<Topic> topics) {
        mTopics = topics;
    }

    public Long getTotalPostCount() {
        return mTotalPostCount;
    }

    public void setTotalPostCount(Long totalPostCount) {
        mTotalPostCount = totalPostCount;
    }

    public Long getTotalTopicCount() {
        return mTotalTopicCount;
    }

    public void setTotalTopicCount(Long totalTopicCount) {
        mTotalTopicCount = totalTopicCount;
    }

}
