package com.tony.downloader;

import com.tony.resolver.JSONResolver;

public class GithubHistoryTagDownloader extends AbstractDownloader {

    private String zipDownloadUrl;
    private String tagName;
    private String nodeId;

    public GithubHistoryTagDownloader(String tagApi) {
        super.setTargetReleasesApiUrl(tagApi);
    }

    public void setTargetTagInfo(String tagInfo) {
        getJsonResolver().setOrigin(tagInfo);
        tagName = getJsonResolver().getString("name");
        nodeId = getJsonResolver().getString("node_id");
        zipDownloadUrl = getJsonResolver().getString("zipball_url");
    }

    public String getTagInfoList() {
        return this.getLatestInfo();
    }

    @Override
    public String getUpdateSummary() {
        return null;
    }

    @Override
    protected String getZipDownloadUrl() {
        return zipDownloadUrl;
    }

    public void setZipDownloadUrl(String zipDownloadUrl) {
        this.zipDownloadUrl = zipDownloadUrl;
    }

    @Override
    protected void setLocalVersion() {
        JSONResolver versionInfo = getJsonResolver().newObject();
        versionInfo.put("version", tagName)
                .put("nodeId", nodeId);
        doWriteVersionInfo(versionInfo.toJSONString());
    }
}
