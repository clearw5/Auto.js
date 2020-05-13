package com.tony.downloader;

import com.tony.resolver.JSONResolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class GithubReleaseDownloader extends AbstractDownloader {
    @Override
    public String getUpdateSummary() {
        String updateInfo = getLatestInfo();
        if (updateInfo != null) {
            JSONResolver resolver = getJsonResolver().setOrigin(updateInfo);
            String body = resolver.getString("body");
            String nodeId = resolver.getString("node_id");
            String tagName = resolver.getString("tag_name");
            resolver = getJsonResolver().newObject();
            return resolver.put("body", body)
                    .put("nodeId", nodeId)
                    .put("tagName", tagName)
                    .toJSONString();
        }
        return null;
    }

    @Override
    protected String getZipDownloadUrl() {
        String resultString = getLatestInfo();
        if (resultString != null) {
            String downUrl = getJsonResolver().getString(resultString, "zipball_url");
            getListener().updateGui("download zip url: " + downUrl);
            return downUrl;
        }
        return null;
    }

    @Override
    protected void setLocalVersion() {
        String latestInfo = this.getLatestInfo();

        if (latestInfo == null || getJsonResolver().getString(latestInfo, "tag_name") == null) {
            getListener().updateGui("无法获取最新版本信息");
            return;
        }
        JSONResolver getResolver = getJsonResolver().setOrigin(latestInfo);
        JSONResolver versionInfo = getJsonResolver().newObject();
        versionInfo.put("version", getResolver.getString("tag_name"))
                .put("nodeId", getResolver.getString("node_id"));
        doWriteVersionInfo(versionInfo.toJSONString());
    }
}
