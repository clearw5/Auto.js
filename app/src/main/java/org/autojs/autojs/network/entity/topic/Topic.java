
package org.autojs.autojs.network.entity.topic;

import android.text.Html;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.autojs.autojs.network.entity.user.User;

@SuppressWarnings("unused")
public class Topic {

    private static final Gson GSON = new Gson();

    @SerializedName("category")
    private Category mCategory;
    @SerializedName("cid")
    private String mCid;
    @SerializedName("deleted")
    private boolean mDeleted;
    @SerializedName("downvotes")
    private long mDownvotes;
    @SerializedName("icons")
    private List<Object> mIcons;
    @SerializedName("ignored")
    private boolean mIgnored;
    @SerializedName("index")
    private long mIndex;
    @SerializedName("isOwner")
    private boolean mIsOwner;
    @SerializedName("lastposttime")
    private String mLastposttime;
    @SerializedName("lastposttimeISO")
    private String mLastposttimeISO;
    @SerializedName("locked")
    private boolean mLocked;
    @SerializedName("mainPid")
    private String mMainPid;
    @SerializedName("pinned")
    private boolean mPinned;
    @SerializedName("postcount")
    private String mPostcount;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("tags")
    private List<Object> mTags;
    @SerializedName("thumb")
    private String mThumb;
    @SerializedName("tid")
    private String mTid;
    @SerializedName("timestamp")
    private String mTimestamp;
    @SerializedName("timestampISO")
    private String mTimestampISO;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("titleRaw")
    private String mTitleRaw;
    @SerializedName("uid")
    private String mUid;
    @SerializedName("unread")
    private boolean mUnread;
    @SerializedName("unreplied")
    private boolean mUnreplied;
    @SerializedName("upvotes")
    private long mUpvotes;
    @SerializedName("user")
    private User mUser;
    @SerializedName("viewcount")
    private String mViewcount;
    @SerializedName("votes")
    private long mVotes;
    @SerializedName("posts")
    private List<Post> mPosts = new ArrayList<>();
    @SerializedName("teaser")
    private Post mTeaser;

    private AppInfo mAppInfo;
    private Post mMainPost;

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public String getCid() {
        return mCid;
    }

    public void setCid(String cid) {
        mCid = cid;
    }

    public boolean getDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public long getDownvotes() {
        return mDownvotes;
    }

    public void setDownvotes(long downvotes) {
        mDownvotes = downvotes;
    }

    public List<Object> getIcons() {
        return mIcons;
    }

    public void setIcons(List<Object> icons) {
        mIcons = icons;
    }

    public boolean getIgnored() {
        return mIgnored;
    }

    public void setIgnored(boolean ignored) {
        mIgnored = ignored;
    }

    public long getIndex() {
        return mIndex;
    }

    public void setIndex(long index) {
        mIndex = index;
    }

    public boolean getIsOwner() {
        return mIsOwner;
    }

    public void setIsOwner(boolean isOwner) {
        mIsOwner = isOwner;
    }

    public String getLastposttime() {
        return mLastposttime;
    }

    public void setLastposttime(String lastposttime) {
        mLastposttime = lastposttime;
    }

    public String getLastposttimeISO() {
        return mLastposttimeISO;
    }

    public void setLastposttimeISO(String lastposttimeISO) {
        mLastposttimeISO = lastposttimeISO;
    }

    public boolean getLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    public String getMainPid() {
        return mMainPid;
    }

    public void setMainPid(String mainPid) {
        mMainPid = mainPid;
    }

    public boolean getPinned() {
        return mPinned;
    }

    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }

    public String getPostcount() {
        return mPostcount;
    }

    public void setPostcount(String postcount) {
        mPostcount = postcount;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public List<Object> getTags() {
        return mTags;
    }

    public void setTags(List<Object> tags) {
        mTags = tags;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getTid() {
        return mTid;
    }

    public void setTid(String tid) {
        mTid = tid;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getTimestampISO() {
        return mTimestampISO;
    }

    public void setTimestampISO(String timestampISO) {
        mTimestampISO = timestampISO;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitleRaw() {
        return mTitleRaw;
    }

    public void setTitleRaw(String titleRaw) {
        mTitleRaw = titleRaw;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean getUnread() {
        return mUnread;
    }

    public void setUnread(boolean unread) {
        mUnread = unread;
    }

    public boolean getUnreplied() {
        return mUnreplied;
    }

    public void setUnreplied(boolean unreplied) {
        mUnreplied = unreplied;
    }

    public long getUpvotes() {
        return mUpvotes;
    }

    public void setUpvotes(long upvotes) {
        mUpvotes = upvotes;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getViewcount() {
        return mViewcount;
    }

    public void setViewcount(String viewcount) {
        mViewcount = viewcount;
    }

    public long getVotes() {
        return mVotes;
    }

    public void setVotes(long votes) {
        mVotes = votes;
    }

    public List<Post> getPosts() {
        return mPosts;
    }

    public void setPosts(List<Post> posts) {
        mPosts = posts;
    }

    public AppInfo getAppInfo() {
        if (mAppInfo != null) {
            return mAppInfo;
        }
        if (mTeaser == null || mTeaser.getContent() == null) {
            return null;
        }
        try {
            String decoded = Html.fromHtml(mTeaser.getContent()).toString();
            AppInfo appInfo = GSON.fromJson(decoded, AppInfo.class);
            mAppInfo = appInfo;
            return appInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Post getTeaser() {
        return mTeaser;
    }

    public void setTeaser(Post teaser) {
        mTeaser = teaser;
    }

    public Post getMainPost() {
        return mMainPost;
    }

    public void setMainPost(Post mainPost) {
        mMainPost = mainPost;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "mCategory=" + mCategory +
                ", mCid='" + mCid + '\'' +
                ", mDeleted=" + mDeleted +
                ", mDownvotes=" + mDownvotes +
                ", mIcons=" + mIcons +
                ", mIgnored=" + mIgnored +
                ", mIndex=" + mIndex +
                ", mIsOwner=" + mIsOwner +
                ", mLastposttime='" + mLastposttime + '\'' +
                ", mLastposttimeISO='" + mLastposttimeISO + '\'' +
                ", mLocked=" + mLocked +
                ", mMainPid='" + mMainPid + '\'' +
                ", mPinned=" + mPinned +
                ", mPostcount='" + mPostcount + '\'' +
                ", mSlug='" + mSlug + '\'' +
                ", mTags=" + mTags +
                ", mThumb='" + mThumb + '\'' +
                ", mTid='" + mTid + '\'' +
                ", mTimestamp='" + mTimestamp + '\'' +
                ", mTimestampISO='" + mTimestampISO + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mTitleRaw='" + mTitleRaw + '\'' +
                ", mUid='" + mUid + '\'' +
                ", mUnread=" + mUnread +
                ", mUnreplied=" + mUnreplied +
                ", mUpvotes=" + mUpvotes +
                ", mUser=" + mUser +
                ", mViewcount='" + mViewcount + '\'' +
                ", mVotes=" + mVotes +
                ", mPosts=" + mPosts +
                ", mTeaser=" + mTeaser +
                ", mAppInfo=" + mAppInfo +
                '}';
    }
}
