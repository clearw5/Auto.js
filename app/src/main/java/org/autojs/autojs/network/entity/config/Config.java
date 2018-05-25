package org.autojs.autojs.network.entity.config;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Config{


	@SerializedName("socketioTransports")
	private List<String> socketioTransports;

	@SerializedName("allowGuestSearching")
	private boolean allowGuestSearching;

	@SerializedName("hasImageUploadPlugin")
	private boolean hasImageUploadPlugin;

	@SerializedName("showSiteTitle")
	private boolean showSiteTitle;

	@SerializedName("websocketAddress")
	private String websocketAddress;

	@SerializedName("maximumFileSize")
	private String maximumFileSize;

	@SerializedName("usePagination")
	private boolean usePagination;

	@SerializedName("minimumPostLength")
	private String minimumPostLength;

	@SerializedName("allowGuestUserSearching")
	private boolean allowGuestUserSearching;

	@SerializedName("allowTopicsThumbnail")
	private boolean allowTopicsThumbnail;

	@SerializedName("allowGuestHandles")
	private boolean allowGuestHandles;

	@SerializedName("disableChatMessageEditing")
	private boolean disableChatMessageEditing;

	@SerializedName("version")
	private String version;

	@SerializedName("minimumTitleLength")
	private String minimumTitleLength;

	@SerializedName("maximumTagsPerTopic")
	private int maximumTagsPerTopic;

	@SerializedName("topicPostSort")
	private String topicPostSort;

	@SerializedName("defaultBootswatchSkin")
	private String defaultBootswatchSkin;

	@SerializedName("allowFileUploads")
	private boolean allowFileUploads;

	@SerializedName("maximumPostLength")
	private String maximumPostLength;

	@SerializedName("loggedIn")
	private boolean loggedIn;

	@SerializedName("postsPerPage")
	private String postsPerPage;


	@SerializedName("relative_path")
	private String relativePath;

	@SerializedName("requireEmailConfirmation")
	private boolean requireEmailConfirmation;

	@SerializedName("defaultLang")
	private String defaultLang;

	@SerializedName("disableChat")
	private boolean disableChat;

	@SerializedName("userLang")
	private String userLang;

	@SerializedName("maxReconnectionAttempts")
	private int maxReconnectionAttempts;

	@SerializedName("timeagoCutoff")
	private String timeagoCutoff;

	@SerializedName("browserTitle")
	private String browserTitle;

	@SerializedName("siteTitle")
	private String siteTitle;

	@SerializedName("csrf_token")
	private String csrfToken;

	@SerializedName("categoryTopicSort")
	private String categoryTopicSort;

	@SerializedName("disableMasonry")
	private boolean disableMasonry;

	@SerializedName("theme:src")
	private String themeSrc;

	@SerializedName("cookies")
	private Cookies cookies;

	@SerializedName("markdown")
	private Markdown markdown;

	@SerializedName("minimumTagLength")
	private String minimumTagLength;

	@SerializedName("maximumTagLength")
	private String maximumTagLength;

	@SerializedName("maximumTitleLength")
	private String maximumTitleLength;

	@SerializedName("topicsPerPage")
	private String topicsPerPage;

	@SerializedName("useOutgoingLinksPage")
	private boolean useOutgoingLinksPage;

	@SerializedName("bootswatchSkin")
	private String bootswatchSkin;

	@SerializedName("minimumTagsPerTopic")
	private int minimumTagsPerTopic;

	@SerializedName("delayImageLoading")
	private boolean delayImageLoading;

	@SerializedName("cache-buster")
	private String cacheBuster;

	@SerializedName("titleLayout")
	private String titleLayout;

	@SerializedName("theme:id")
	private String themeId;

	@SerializedName("topicSearchEnabled")
	private boolean topicSearchEnabled;

	@SerializedName("searchEnabled")
	private boolean searchEnabled;

	@SerializedName("reconnectionDelay")
	private int reconnectionDelay;


	public void setSocketioTransports(List<String> socketioTransports){
		this.socketioTransports = socketioTransports;
	}

	public List<String> getSocketioTransports(){
		return socketioTransports;
	}

	public void setAllowGuestSearching(boolean allowGuestSearching){
		this.allowGuestSearching = allowGuestSearching;
	}

	public boolean isAllowGuestSearching(){
		return allowGuestSearching;
	}

	public void setHasImageUploadPlugin(boolean hasImageUploadPlugin){
		this.hasImageUploadPlugin = hasImageUploadPlugin;
	}

	public boolean isHasImageUploadPlugin(){
		return hasImageUploadPlugin;
	}

	public void setShowSiteTitle(boolean showSiteTitle){
		this.showSiteTitle = showSiteTitle;
	}

	public boolean isShowSiteTitle(){
		return showSiteTitle;
	}

	public void setWebsocketAddress(String websocketAddress){
		this.websocketAddress = websocketAddress;
	}

	public String getWebsocketAddress(){
		return websocketAddress;
	}

	public void setMaximumFileSize(String maximumFileSize){
		this.maximumFileSize = maximumFileSize;
	}

	public String getMaximumFileSize(){
		return maximumFileSize;
	}

	public void setUsePagination(boolean usePagination){
		this.usePagination = usePagination;
	}

	public boolean isUsePagination(){
		return usePagination;
	}

	public void setMinimumPostLength(String minimumPostLength){
		this.minimumPostLength = minimumPostLength;
	}

	public String getMinimumPostLength(){
		return minimumPostLength;
	}

	public void setAllowGuestUserSearching(boolean allowGuestUserSearching){
		this.allowGuestUserSearching = allowGuestUserSearching;
	}

	public boolean isAllowGuestUserSearching(){
		return allowGuestUserSearching;
	}

	public void setAllowTopicsThumbnail(boolean allowTopicsThumbnail){
		this.allowTopicsThumbnail = allowTopicsThumbnail;
	}

	public boolean isAllowTopicsThumbnail(){
		return allowTopicsThumbnail;
	}

	public void setAllowGuestHandles(boolean allowGuestHandles){
		this.allowGuestHandles = allowGuestHandles;
	}

	public boolean isAllowGuestHandles(){
		return allowGuestHandles;
	}

	public void setDisableChatMessageEditing(boolean disableChatMessageEditing){
		this.disableChatMessageEditing = disableChatMessageEditing;
	}

	public boolean isDisableChatMessageEditing(){
		return disableChatMessageEditing;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion(){
		return version;
	}

	public void setMinimumTitleLength(String minimumTitleLength){
		this.minimumTitleLength = minimumTitleLength;
	}

	public String getMinimumTitleLength(){
		return minimumTitleLength;
	}

	public void setMaximumTagsPerTopic(int maximumTagsPerTopic){
		this.maximumTagsPerTopic = maximumTagsPerTopic;
	}

	public int getMaximumTagsPerTopic(){
		return maximumTagsPerTopic;
	}

	public void setTopicPostSort(String topicPostSort){
		this.topicPostSort = topicPostSort;
	}

	public String getTopicPostSort(){
		return topicPostSort;
	}

	public void setDefaultBootswatchSkin(String defaultBootswatchSkin){
		this.defaultBootswatchSkin = defaultBootswatchSkin;
	}

	public String getDefaultBootswatchSkin(){
		return defaultBootswatchSkin;
	}

	public void setAllowFileUploads(boolean allowFileUploads){
		this.allowFileUploads = allowFileUploads;
	}

	public boolean isAllowFileUploads(){
		return allowFileUploads;
	}

	public void setMaximumPostLength(String maximumPostLength){
		this.maximumPostLength = maximumPostLength;
	}

	public String getMaximumPostLength(){
		return maximumPostLength;
	}

	public void setLoggedIn(boolean loggedIn){
		this.loggedIn = loggedIn;
	}

	public boolean isLoggedIn(){
		return loggedIn;
	}

	public void setPostsPerPage(String postsPerPage){
		this.postsPerPage = postsPerPage;
	}

	public String getPostsPerPage(){
		return postsPerPage;
	}

	public void setRelativePath(String relativePath){
		this.relativePath = relativePath;
	}

	public String getRelativePath(){
		return relativePath;
	}

	public void setRequireEmailConfirmation(boolean requireEmailConfirmation){
		this.requireEmailConfirmation = requireEmailConfirmation;
	}

	public boolean isRequireEmailConfirmation(){
		return requireEmailConfirmation;
	}

	public void setDefaultLang(String defaultLang){
		this.defaultLang = defaultLang;
	}

	public String getDefaultLang(){
		return defaultLang;
	}

	public void setDisableChat(boolean disableChat){
		this.disableChat = disableChat;
	}

	public boolean isDisableChat(){
		return disableChat;
	}

	public void setUserLang(String userLang){
		this.userLang = userLang;
	}

	public String getUserLang(){
		return userLang;
	}

	public void setMaxReconnectionAttempts(int maxReconnectionAttempts){
		this.maxReconnectionAttempts = maxReconnectionAttempts;
	}

	public int getMaxReconnectionAttempts(){
		return maxReconnectionAttempts;
	}

	public void setTimeagoCutoff(String timeagoCutoff){
		this.timeagoCutoff = timeagoCutoff;
	}

	public String getTimeagoCutoff(){
		return timeagoCutoff;
	}

	public void setBrowserTitle(String browserTitle){
		this.browserTitle = browserTitle;
	}

	public String getBrowserTitle(){
		return browserTitle;
	}

	public void setSiteTitle(String siteTitle){
		this.siteTitle = siteTitle;
	}

	public String getSiteTitle(){
		return siteTitle;
	}

	public void setCsrfToken(String csrfToken){
		this.csrfToken = csrfToken;
	}

	public String getCsrfToken(){
		return csrfToken;
	}

	public void setCategoryTopicSort(String categoryTopicSort){
		this.categoryTopicSort = categoryTopicSort;
	}

	public String getCategoryTopicSort(){
		return categoryTopicSort;
	}

	public void setDisableMasonry(boolean disableMasonry){
		this.disableMasonry = disableMasonry;
	}

	public boolean isDisableMasonry(){
		return disableMasonry;
	}

	public void setThemeSrc(String themeSrc){
		this.themeSrc = themeSrc;
	}

	public String getThemeSrc(){
		return themeSrc;
	}

	public void setCookies(Cookies cookies){
		this.cookies = cookies;
	}

	public Cookies getCookies(){
		return cookies;
	}

	public void setMarkdown(Markdown markdown){
		this.markdown = markdown;
	}

	public Markdown getMarkdown(){
		return markdown;
	}

	public void setMinimumTagLength(String minimumTagLength){
		this.minimumTagLength = minimumTagLength;
	}

	public String getMinimumTagLength(){
		return minimumTagLength;
	}

	public void setMaximumTagLength(String maximumTagLength){
		this.maximumTagLength = maximumTagLength;
	}

	public String getMaximumTagLength(){
		return maximumTagLength;
	}

	public void setMaximumTitleLength(String maximumTitleLength){
		this.maximumTitleLength = maximumTitleLength;
	}

	public String getMaximumTitleLength(){
		return maximumTitleLength;
	}

	public void setTopicsPerPage(String topicsPerPage){
		this.topicsPerPage = topicsPerPage;
	}

	public String getTopicsPerPage(){
		return topicsPerPage;
	}

	public void setUseOutgoingLinksPage(boolean useOutgoingLinksPage){
		this.useOutgoingLinksPage = useOutgoingLinksPage;
	}

	public boolean isUseOutgoingLinksPage(){
		return useOutgoingLinksPage;
	}

	public void setBootswatchSkin(String bootswatchSkin){
		this.bootswatchSkin = bootswatchSkin;
	}

	public String getBootswatchSkin(){
		return bootswatchSkin;
	}

	public void setMinimumTagsPerTopic(int minimumTagsPerTopic){
		this.minimumTagsPerTopic = minimumTagsPerTopic;
	}

	public int getMinimumTagsPerTopic(){
		return minimumTagsPerTopic;
	}

	public void setDelayImageLoading(boolean delayImageLoading){
		this.delayImageLoading = delayImageLoading;
	}

	public boolean isDelayImageLoading(){
		return delayImageLoading;
	}

	public void setCacheBuster(String cacheBuster){
		this.cacheBuster = cacheBuster;
	}

	public String getCacheBuster(){
		return cacheBuster;
	}

	public void setTitleLayout(String titleLayout){
		this.titleLayout = titleLayout;
	}

	public String getTitleLayout(){
		return titleLayout;
	}

	public void setThemeId(String themeId){
		this.themeId = themeId;
	}

	public String getThemeId(){
		return themeId;
	}

	public void setTopicSearchEnabled(boolean topicSearchEnabled){
		this.topicSearchEnabled = topicSearchEnabled;
	}

	public boolean isTopicSearchEnabled(){
		return topicSearchEnabled;
	}

	public void setSearchEnabled(boolean searchEnabled){
		this.searchEnabled = searchEnabled;
	}

	public boolean isSearchEnabled(){
		return searchEnabled;
	}

	public void setReconnectionDelay(int reconnectionDelay){
		this.reconnectionDelay = reconnectionDelay;
	}

	public int getReconnectionDelay(){
		return reconnectionDelay;
	}

	@Override
 	public String toString(){
		return 
			"Config{" + 
			",socketioTransports = '" + socketioTransports + '\'' +
			",allowGuestSearching = '" + allowGuestSearching + '\'' + 
			",hasImageUploadPlugin = '" + hasImageUploadPlugin + '\'' + 
			",showSiteTitle = '" + showSiteTitle + '\'' + 
			",websocketAddress = '" + websocketAddress + '\'' + 
			",maximumFileSize = '" + maximumFileSize + '\'' + 
			",usePagination = '" + usePagination + '\'' + 
			",minimumPostLength = '" + minimumPostLength + '\'' + 
			",allowGuestUserSearching = '" + allowGuestUserSearching + '\'' + 
			",allowTopicsThumbnail = '" + allowTopicsThumbnail + '\'' + 
			",allowGuestHandles = '" + allowGuestHandles + '\'' + 
			",disableChatMessageEditing = '" + disableChatMessageEditing + '\'' + 
			",version = '" + version + '\'' + 
			",minimumTitleLength = '" + minimumTitleLength + '\'' + 
			",maximumTagsPerTopic = '" + maximumTagsPerTopic + '\'' + 
			",topicPostSort = '" + topicPostSort + '\'' + 
			",defaultBootswatchSkin = '" + defaultBootswatchSkin + '\'' + 
			",allowFileUploads = '" + allowFileUploads + '\'' + 
			",maximumPostLength = '" + maximumPostLength + '\'' + 
			",loggedIn = '" + loggedIn + '\'' + 
			",postsPerPage = '" + postsPerPage + '\'' + 
			",relative_path = '" + relativePath + '\'' +
			",requireEmailConfirmation = '" + requireEmailConfirmation + '\'' + 
			",defaultLang = '" + defaultLang + '\'' + 
			",disableChat = '" + disableChat + '\'' + 
			",userLang = '" + userLang + '\'' + 
			",maxReconnectionAttempts = '" + maxReconnectionAttempts + '\'' + 
			",timeagoCutoff = '" + timeagoCutoff + '\'' + 
			",browserTitle = '" + browserTitle + '\'' + 
			",siteTitle = '" + siteTitle + '\'' + 
			",csrf_token = '" + csrfToken + '\'' + 
			",categoryTopicSort = '" + categoryTopicSort + '\'' + 
			",disableMasonry = '" + disableMasonry + '\'' + 
			",theme:src = '" + themeSrc + '\'' + 
			",cookies = '" + cookies + '\'' + 
			",markdown = '" + markdown + '\'' + 
			",minimumTagLength = '" + minimumTagLength + '\'' + 
			",maximumTagLength = '" + maximumTagLength + '\'' + 
			",maximumTitleLength = '" + maximumTitleLength + '\'' + 
			",topicsPerPage = '" + topicsPerPage + '\'' + 
			",useOutgoingLinksPage = '" + useOutgoingLinksPage + '\'' + 
			",bootswatchSkin = '" + bootswatchSkin + '\'' + 
			",minimumTagsPerTopic = '" + minimumTagsPerTopic + '\'' + 
			",delayImageLoading = '" + delayImageLoading + '\'' + 
			",cache-buster = '" + cacheBuster + '\'' + 
			",titleLayout = '" + titleLayout + '\'' + 
			",theme:id = '" + themeId + '\'' + 
			",topicSearchEnabled = '" + topicSearchEnabled + '\'' + 
			",searchEnabled = '" + searchEnabled + '\'' + 
			",reconnectionDelay = '" + reconnectionDelay + '\'' + 
			"}";
		}
}