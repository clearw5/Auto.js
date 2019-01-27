
package org.autojs.autojs.network.entity.topic;

import com.google.gson.annotations.SerializedName;

import org.autojs.autojs.network.entity.user.User;

@SuppressWarnings("unused")
public class Post {

    @SerializedName("content")
    private String mContent;
    @SerializedName("downvoted")
    private boolean mDownvoted;
    @SerializedName("downvotes")
    private long mDownvotes;
    @SerializedName("edited")
    private String mEdited;
    @SerializedName("editedISO")
    private String mEditedISO;
    @SerializedName("index")
    private long mIndex;
    @SerializedName("pid")
    private String mPid;
    @SerializedName("replies")
    private Replies mReplies;
    @SerializedName("selfPost")
    private boolean mSelfPost;
    @SerializedName("tid")
    private String mTid;
    @SerializedName("timestamp")
    private String mTimestamp;
    @SerializedName("timestampISO")
    private String mTimestampISO;
    @SerializedName("uid")
    private String mUid;
    @SerializedName("upvoted")
    private boolean mUpvoted;
    @SerializedName("upvotes")
    private long mUpvotes;
    @SerializedName("user")
    private User mUser;
    @SerializedName("votes")
    private long mVotes;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public boolean getDownvoted() {
        return mDownvoted;
    }

    public void setDownvoted(boolean downvoted) {
        mDownvoted = downvoted;
    }

    public long getDownvotes() {
        return mDownvotes;
    }

    public void setDownvotes(long downvotes) {
        mDownvotes = downvotes;
    }

    public String getEdited() {
        return mEdited;
    }

    public void setEdited(String edited) {
        mEdited = edited;
    }

    public String getEditedISO() {
        return mEditedISO;
    }

    public void setEditedISO(String editedISO) {
        mEditedISO = editedISO;
    }

    public long getIndex() {
        return mIndex;
    }

    public void setIndex(long index) {
        mIndex = index;
    }

    public String getPid() {
        return mPid;
    }

    public void setPid(String pid) {
        mPid = pid;
    }

    public Replies getReplies() {
        return mReplies;
    }

    public void setReplies(Replies replies) {
        mReplies = replies;
    }

    public boolean getSelfPost() {
        return mSelfPost;
    }

    public void setSelfPost(boolean selfPost) {
        mSelfPost = selfPost;
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

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean getUpvoted() {
        return mUpvoted;
    }

    public void setUpvoted(boolean upvoted) {
        mUpvoted = upvoted;
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

    public long getVotes() {
        return mVotes;
    }

    public void setVotes(long votes) {
        mVotes = votes;
    }

}
