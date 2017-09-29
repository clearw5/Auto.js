package com.stardust.scriptdroid.network.entity;

import com.google.gson.annotations.SerializedName;

public class VerifyResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("lastposttime")
    private String lastposttime;

    @SerializedName("signature")
    private String signature;

    @SerializedName("icon:bgColor")
    private String iconBgColor;

    @SerializedName("groupTitle")
    private String groupTitle;

    @SerializedName("reputation")
    private String reputation;

    @SerializedName("followingCount")
    private String followingCount;

    @SerializedName("lastonlineISO")
    private String lastonlineISO;

    @SerializedName("uid")
    private String uid;

    @SerializedName("profileviews")
    private String profileviews;

    @SerializedName("icon:text")
    private String iconText;

    @SerializedName("banned")
    private String banned;

    @SerializedName("userslug")
    private String userslug;

    @SerializedName("followerCount")
    private String followerCount;

    @SerializedName("email")
    private String email;

    @SerializedName("joindate")
    private String joindate;

    @SerializedName("website")
    private String website;

    @SerializedName("uploadedpicture")
    private String uploadedpicture;

    @SerializedName("passwordExpiry")
    private String passwordExpiry;

    @SerializedName("lastonline")
    private String lastonline;

    @SerializedName("picture")
    private String picture;

    @SerializedName("joindateISO")
    private String joindateISO;

    @SerializedName("email:confirmed")
    private Object emailConfirmed;

    @SerializedName("postcount")
    private String postcount;

    @SerializedName("location")
    private String location;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("topiccount")
    private String topiccount;

    @SerializedName("username")
    private String username;

    @SerializedName("status")
    private String status;

    public boolean isSuccessful() {
        return message == null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setLastposttime(String lastposttime) {
        this.lastposttime = lastposttime;
    }

    public String getLastposttime() {
        return lastposttime;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setIconBgColor(String iconBgColor) {
        this.iconBgColor = iconBgColor;
    }

    public String getIconBgColor() {
        return iconBgColor;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation;
    }

    public String getReputation() {
        return reputation;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setLastonlineISO(String lastonlineISO) {
        this.lastonlineISO = lastonlineISO;
    }

    public String getLastonlineISO() {
        return lastonlineISO;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setProfileviews(String profileviews) {
        this.profileviews = profileviews;
    }

    public String getProfileviews() {
        return profileviews;
    }

    public void setIconText(String iconText) {
        this.iconText = iconText;
    }

    public String getIconText() {
        return iconText;
    }

    public void setBanned(String banned) {
        this.banned = banned;
    }

    public String getBanned() {
        return banned;
    }

    public void setUserslug(String userslug) {
        this.userslug = userslug;
    }

    public String getUserslug() {
        return userslug;
    }

    public void setFollowerCount(String followerCount) {
        this.followerCount = followerCount;
    }

    public String getFollowerCount() {
        return followerCount;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setJoindate(String joindate) {
        this.joindate = joindate;
    }

    public String getJoindate() {
        return joindate;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setUploadedpicture(String uploadedpicture) {
        this.uploadedpicture = uploadedpicture;
    }

    public String getUploadedpicture() {
        return uploadedpicture;
    }

    public void setPasswordExpiry(String passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    public String getPasswordExpiry() {
        return passwordExpiry;
    }

    public void setLastonline(String lastonline) {
        this.lastonline = lastonline;
    }

    public String getLastonline() {
        return lastonline;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public void setJoindateISO(String joindateISO) {
        this.joindateISO = joindateISO;
    }

    public String getJoindateISO() {
        return joindateISO;
    }

    public void setEmailConfirmed(Object emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public Object getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setPostcount(String postcount) {
        this.postcount = postcount;
    }

    public String getPostcount() {
        return postcount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setTopiccount(String topiccount) {
        this.topiccount = topiccount;
    }

    public String getTopiccount() {
        return topiccount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "VerifyResponse{" +
                        "birthday = '" + birthday + '\'' +
                        ",lastposttime = '" + lastposttime + '\'' +
                        ",signature = '" + signature + '\'' +
                        ",icon:bgColor = '" + iconBgColor + '\'' +
                        ",groupTitle = '" + groupTitle + '\'' +
                        ",reputation = '" + reputation + '\'' +
                        ",followingCount = '" + followingCount + '\'' +
                        ",lastonlineISO = '" + lastonlineISO + '\'' +
                        ",uid = '" + uid + '\'' +
                        ",profileviews = '" + profileviews + '\'' +
                        ",icon:text = '" + iconText + '\'' +
                        ",banned = '" + banned + '\'' +
                        ",userslug = '" + userslug + '\'' +
                        ",followerCount = '" + followerCount + '\'' +
                        ",email = '" + email + '\'' +
                        ",joindate = '" + joindate + '\'' +
                        ",website = '" + website + '\'' +
                        ",uploadedpicture = '" + uploadedpicture + '\'' +
                        ",passwordExpiry = '" + passwordExpiry + '\'' +
                        ",lastonline = '" + lastonline + '\'' +
                        ",picture = '" + picture + '\'' +
                        ",joindateISO = '" + joindateISO + '\'' +
                        ",email:confirmed = '" + emailConfirmed + '\'' +
                        ",postcount = '" + postcount + '\'' +
                        ",location = '" + location + '\'' +
                        ",fullname = '" + fullname + '\'' +
                        ",topiccount = '" + topiccount + '\'' +
                        ",username = '" + username + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}