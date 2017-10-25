package com.stardust.scriptdroid.network.entity;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("websiteLink")
	private String websiteLink;

	@SerializedName("reputation")
	private String reputation;

	@SerializedName("sso")
	private List<Object> sso;

	@SerializedName("icon:text")
	private String iconText;

	@SerializedName("isGlobalModerator")
	private boolean isGlobalModerator;

	@SerializedName("joindate")
	private String joindate;

	@SerializedName("profile_links")
	private List<Object> profileLinks;

	@SerializedName("reputation:disabled")
	private boolean reputationDisabled;

	@SerializedName("isAdmin")
	private boolean isAdmin;

	@SerializedName("moderationNote")
	private String moderationNote;

	@SerializedName("ips")
	private List<String> ips;

	@SerializedName("aboutme")
	private String aboutme;

	@SerializedName("isTargetAdmin")
	private boolean isTargetAdmin;

	@SerializedName("email:confirmed")
	private boolean emailConfirmed;

	@SerializedName("isAdminOrGlobalModerator")
	private boolean isAdminOrGlobalModerator;

	@SerializedName("emailClass")
	private String emailClass;

	@SerializedName("downvote:disabled")
	private boolean downvoteDisabled;

	@SerializedName("topiccount")
	private String topiccount;

	@SerializedName("isSelf")
	private boolean isSelf;

	@SerializedName("status")
	private String status;

	@SerializedName("birthday")
	private String birthday;

	@SerializedName("showHidden")
	private boolean showHidden;

	@SerializedName("yourid")
	private int yourid;

	@SerializedName("lastposttime")
	private String lastposttime;

	@SerializedName("isModerator")
	private boolean isModerator;

	@SerializedName("signature")
	private String signature;

	@SerializedName("icon:bgColor")
	private String iconBgColor;

	@SerializedName("canEdit")
	private boolean canEdit;

	@SerializedName("groupTitle")
	private String groupTitle;

	@SerializedName("followingCount")
	private int followingCount;

	@SerializedName("lastonlineISO")
	private String lastonlineISO;

	@SerializedName("email:disableEdit")
	private boolean emailDisableEdit;

	@SerializedName("uid")
	private String uid;

	@SerializedName("canChangePassword")
	private boolean canChangePassword;

	@SerializedName("profileviews")
	private String profileviews;

	@SerializedName("cover:url")
	private String coverUrl;

	@SerializedName("banned")
	private boolean banned;

	@SerializedName("userslug")
	private String userslug;

	@SerializedName("followerCount")
	private int followerCount;

	@SerializedName("email")
	private String email;

	@SerializedName("website")
	private String website;

	@SerializedName("isFollowing")
	private boolean isFollowing;

	@SerializedName("uploadedpicture")
	private String uploadedpicture;

	@SerializedName("passwordExpiry")
	private String passwordExpiry;

	@SerializedName("canBan")
	private boolean canBan;

	@SerializedName("lastonline")
	private String lastonline;

	@SerializedName("disableSignatures")
	private boolean disableSignatures;

	@SerializedName("groups")
	private List<GroupsItem> groups;

	@SerializedName("username:disableEdit")
	private boolean usernameDisableEdit;

	@SerializedName("picture")
	private String picture;

	@SerializedName("joindateISO")
	private String joindateISO;

	@SerializedName("isSelfOrAdminOrGlobalModerator")
	private boolean isSelfOrAdminOrGlobalModerator;

	@SerializedName("websiteName")
	private String websiteName;

	@SerializedName("isAdminOrGlobalModeratorOrModerator")
	private boolean isAdminOrGlobalModeratorOrModerator;

	@SerializedName("cover:position")
	private String coverPosition;

	@SerializedName("postcount")
	private String postcount;

	@SerializedName("location")
	private String location;

	@SerializedName("fullname")
	private String fullname;

	@SerializedName("age")
	private int age;

	@SerializedName("theirid")
	private String theirid;

	@SerializedName("username")
	private String username;

	public void setWebsiteLink(String websiteLink){
		this.websiteLink = websiteLink;
	}

	public String getWebsiteLink(){
		return websiteLink;
	}

	public void setReputation(String reputation){
		this.reputation = reputation;
	}

	public String getReputation(){
		return reputation;
	}

	public void setSso(List<Object> sso){
		this.sso = sso;
	}

	public List<Object> getSso(){
		return sso;
	}

	public void setIconText(String iconText){
		this.iconText = iconText;
	}

	public String getIconText(){
		return iconText;
	}

	public void setIsGlobalModerator(boolean isGlobalModerator){
		this.isGlobalModerator = isGlobalModerator;
	}

	public boolean isIsGlobalModerator(){
		return isGlobalModerator;
	}

	public void setJoindate(String joindate){
		this.joindate = joindate;
	}

	public String getJoindate(){
		return joindate;
	}

	public void setProfileLinks(List<Object> profileLinks){
		this.profileLinks = profileLinks;
	}

	public List<Object> getProfileLinks(){
		return profileLinks;
	}

	public void setReputationDisabled(boolean reputationDisabled){
		this.reputationDisabled = reputationDisabled;
	}

	public boolean isReputationDisabled(){
		return reputationDisabled;
	}

	public void setIsAdmin(boolean isAdmin){
		this.isAdmin = isAdmin;
	}

	public boolean isIsAdmin(){
		return isAdmin;
	}

	public void setModerationNote(String moderationNote){
		this.moderationNote = moderationNote;
	}

	public String getModerationNote(){
		return moderationNote;
	}

	public void setIps(List<String> ips){
		this.ips = ips;
	}

	public List<String> getIps(){
		return ips;
	}

	public void setAboutme(String aboutme){
		this.aboutme = aboutme;
	}

	public String getAboutme(){
		return aboutme;
	}

	public void setIsTargetAdmin(boolean isTargetAdmin){
		this.isTargetAdmin = isTargetAdmin;
	}

	public boolean isIsTargetAdmin(){
		return isTargetAdmin;
	}

	public void setEmailConfirmed(boolean emailConfirmed){
		this.emailConfirmed = emailConfirmed;
	}

	public boolean isEmailConfirmed(){
		return emailConfirmed;
	}

	public void setIsAdminOrGlobalModerator(boolean isAdminOrGlobalModerator){
		this.isAdminOrGlobalModerator = isAdminOrGlobalModerator;
	}

	public boolean isIsAdminOrGlobalModerator(){
		return isAdminOrGlobalModerator;
	}

	public void setEmailClass(String emailClass){
		this.emailClass = emailClass;
	}

	public String getEmailClass(){
		return emailClass;
	}

	public void setDownvoteDisabled(boolean downvoteDisabled){
		this.downvoteDisabled = downvoteDisabled;
	}

	public boolean isDownvoteDisabled(){
		return downvoteDisabled;
	}

	public void setTopiccount(String topiccount){
		this.topiccount = topiccount;
	}

	public String getTopiccount(){
		return topiccount;
	}

	public void setIsSelf(boolean isSelf){
		this.isSelf = isSelf;
	}

	public boolean isIsSelf(){
		return isSelf;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setBirthday(String birthday){
		this.birthday = birthday;
	}

	public String getBirthday(){
		return birthday;
	}

	public void setShowHidden(boolean showHidden){
		this.showHidden = showHidden;
	}

	public boolean isShowHidden(){
		return showHidden;
	}

	public void setYourid(int yourid){
		this.yourid = yourid;
	}

	public int getYourid(){
		return yourid;
	}

	public void setLastposttime(String lastposttime){
		this.lastposttime = lastposttime;
	}

	public String getLastposttime(){
		return lastposttime;
	}

	public void setIsModerator(boolean isModerator){
		this.isModerator = isModerator;
	}

	public boolean isIsModerator(){
		return isModerator;
	}

	public void setSignature(String signature){
		this.signature = signature;
	}

	public String getSignature(){
		return signature;
	}

	public void setIconBgColor(String iconBgColor){
		this.iconBgColor = iconBgColor;
	}

	public String getIconBgColor(){
		return iconBgColor;
	}

	public void setCanEdit(boolean canEdit){
		this.canEdit = canEdit;
	}

	public boolean isCanEdit(){
		return canEdit;
	}

	public void setGroupTitle(String groupTitle){
		this.groupTitle = groupTitle;
	}

	public String getGroupTitle(){
		return groupTitle;
	}

	public void setFollowingCount(int followingCount){
		this.followingCount = followingCount;
	}

	public int getFollowingCount(){
		return followingCount;
	}

	public void setLastonlineISO(String lastonlineISO){
		this.lastonlineISO = lastonlineISO;
	}

	public String getLastonlineISO(){
		return lastonlineISO;
	}

	public void setEmailDisableEdit(boolean emailDisableEdit){
		this.emailDisableEdit = emailDisableEdit;
	}

	public boolean isEmailDisableEdit(){
		return emailDisableEdit;
	}

	public void setUid(String uid){
		this.uid = uid;
	}

	public String getUid(){
		return uid;
	}

	public void setCanChangePassword(boolean canChangePassword){
		this.canChangePassword = canChangePassword;
	}

	public boolean isCanChangePassword(){
		return canChangePassword;
	}

	public void setProfileviews(String profileviews){
		this.profileviews = profileviews;
	}

	public String getProfileviews(){
		return profileviews;
	}

	public void setCoverUrl(String coverUrl){
		this.coverUrl = coverUrl;
	}

	public String getCoverUrl(){
		return coverUrl;
	}

	public void setBanned(boolean banned){
		this.banned = banned;
	}

	public boolean isBanned(){
		return banned;
	}

	public void setUserslug(String userslug){
		this.userslug = userslug;
	}

	public String getUserslug(){
		return userslug;
	}

	public void setFollowerCount(int followerCount){
		this.followerCount = followerCount;
	}

	public int getFollowerCount(){
		return followerCount;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setWebsite(String website){
		this.website = website;
	}

	public String getWebsite(){
		return website;
	}

	public void setIsFollowing(boolean isFollowing){
		this.isFollowing = isFollowing;
	}

	public boolean isIsFollowing(){
		return isFollowing;
	}

	public void setUploadedpicture(String uploadedpicture){
		this.uploadedpicture = uploadedpicture;
	}

	public String getUploadedpicture(){
		return uploadedpicture;
	}

	public void setPasswordExpiry(String passwordExpiry){
		this.passwordExpiry = passwordExpiry;
	}

	public String getPasswordExpiry(){
		return passwordExpiry;
	}

	public void setCanBan(boolean canBan){
		this.canBan = canBan;
	}

	public boolean isCanBan(){
		return canBan;
	}

	public void setLastonline(String lastonline){
		this.lastonline = lastonline;
	}

	public String getLastonline(){
		return lastonline;
	}

	public void setDisableSignatures(boolean disableSignatures){
		this.disableSignatures = disableSignatures;
	}

	public boolean isDisableSignatures(){
		return disableSignatures;
	}

	public void setGroups(List<GroupsItem> groups){
		this.groups = groups;
	}

	public List<GroupsItem> getGroups(){
		return groups;
	}

	public void setUsernameDisableEdit(boolean usernameDisableEdit){
		this.usernameDisableEdit = usernameDisableEdit;
	}

	public boolean isUsernameDisableEdit(){
		return usernameDisableEdit;
	}

	public void setPicture(String picture){
		this.picture = picture;
	}

	public String getPicture(){
		return picture;
	}

	public void setJoindateISO(String joindateISO){
		this.joindateISO = joindateISO;
	}

	public String getJoindateISO(){
		return joindateISO;
	}

	public void setIsSelfOrAdminOrGlobalModerator(boolean isSelfOrAdminOrGlobalModerator){
		this.isSelfOrAdminOrGlobalModerator = isSelfOrAdminOrGlobalModerator;
	}

	public boolean isIsSelfOrAdminOrGlobalModerator(){
		return isSelfOrAdminOrGlobalModerator;
	}

	public void setWebsiteName(String websiteName){
		this.websiteName = websiteName;
	}

	public String getWebsiteName(){
		return websiteName;
	}

	public void setIsAdminOrGlobalModeratorOrModerator(boolean isAdminOrGlobalModeratorOrModerator){
		this.isAdminOrGlobalModeratorOrModerator = isAdminOrGlobalModeratorOrModerator;
	}

	public boolean isIsAdminOrGlobalModeratorOrModerator(){
		return isAdminOrGlobalModeratorOrModerator;
	}

	public void setCoverPosition(String coverPosition){
		this.coverPosition = coverPosition;
	}

	public String getCoverPosition(){
		return coverPosition;
	}

	public void setPostcount(String postcount){
		this.postcount = postcount;
	}

	public String getPostcount(){
		return postcount;
	}

	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}

	public void setFullname(String fullname){
		this.fullname = fullname;
	}

	public String getFullname(){
		return fullname;
	}

	public void setAge(int age){
		this.age = age;
	}

	public int getAge(){
		return age;
	}

	public void setTheirid(String theirid){
		this.theirid = theirid;
	}

	public String getTheirid(){
		return theirid;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"User{" + 
			"websiteLink = '" + websiteLink + '\'' + 
			",reputation = '" + reputation + '\'' + 
			",sso = '" + sso + '\'' + 
			",icon:text = '" + iconText + '\'' + 
			",isGlobalModerator = '" + isGlobalModerator + '\'' + 
			",joindate = '" + joindate + '\'' + 
			",profile_links = '" + profileLinks + '\'' + 
			",reputation:disabled = '" + reputationDisabled + '\'' + 
			",isAdmin = '" + isAdmin + '\'' + 
			",moderationNote = '" + moderationNote + '\'' + 
			",ips = '" + ips + '\'' + 
			",aboutme = '" + aboutme + '\'' + 
			",isTargetAdmin = '" + isTargetAdmin + '\'' + 
			",email:confirmed = '" + emailConfirmed + '\'' + 
			",isAdminOrGlobalModerator = '" + isAdminOrGlobalModerator + '\'' + 
			",emailClass = '" + emailClass + '\'' + 
			",downvote:disabled = '" + downvoteDisabled + '\'' + 
			",topiccount = '" + topiccount + '\'' + 
			",isSelf = '" + isSelf + '\'' + 
			",status = '" + status + '\'' + 
			",birthday = '" + birthday + '\'' + 
			",showHidden = '" + showHidden + '\'' + 
			",yourid = '" + yourid + '\'' + 
			",lastposttime = '" + lastposttime + '\'' + 
			",isModerator = '" + isModerator + '\'' + 
			",signature = '" + signature + '\'' + 
			",icon:bgColor = '" + iconBgColor + '\'' + 
			",canEdit = '" + canEdit + '\'' + 
			",groupTitle = '" + groupTitle + '\'' + 
			",followingCount = '" + followingCount + '\'' + 
			",lastonlineISO = '" + lastonlineISO + '\'' + 
			",email:disableEdit = '" + emailDisableEdit + '\'' + 
			",uid = '" + uid + '\'' + 
			",canChangePassword = '" + canChangePassword + '\'' + 
			",profileviews = '" + profileviews + '\'' + 
			",cover:url = '" + coverUrl + '\'' + 
			",banned = '" + banned + '\'' + 
			",userslug = '" + userslug + '\'' + 
			",followerCount = '" + followerCount + '\'' + 
			",email = '" + email + '\'' + 
			",website = '" + website + '\'' + 
			",isFollowing = '" + isFollowing + '\'' + 
			",uploadedpicture = '" + uploadedpicture + '\'' + 
			",passwordExpiry = '" + passwordExpiry + '\'' + 
			",canBan = '" + canBan + '\'' + 
			",lastonline = '" + lastonline + '\'' + 
			",disableSignatures = '" + disableSignatures + '\'' + 
			",groups = '" + groups + '\'' + 
			",username:disableEdit = '" + usernameDisableEdit + '\'' + 
			",picture = '" + picture + '\'' + 
			",joindateISO = '" + joindateISO + '\'' + 
			",isSelfOrAdminOrGlobalModerator = '" + isSelfOrAdminOrGlobalModerator + '\'' + 
			",websiteName = '" + websiteName + '\'' + 
			",isAdminOrGlobalModeratorOrModerator = '" + isAdminOrGlobalModeratorOrModerator + '\'' + 
			",cover:position = '" + coverPosition + '\'' + 
			",postcount = '" + postcount + '\'' + 
			",location = '" + location + '\'' + 
			",fullname = '" + fullname + '\'' + 
			",age = '" + age + '\'' + 
			",theirid = '" + theirid + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}