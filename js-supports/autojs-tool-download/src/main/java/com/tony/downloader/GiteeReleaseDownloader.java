package com.tony.downloader;

import com.tony.resolver.JSONResolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * gitee无法直接通过release api下载，但是可以整合两个仓库
 * 一个发布更新信息，一个保存更新包zip文件 ${repoName}-${tagName}.zip
 */
public class GiteeReleaseDownloader extends AbstractDownloader {

    private String tagName;
    private String nodeId;
    private String body;

    private String giteeUpdateRepoUrl;
    private String prefixName;
    private String prepend;

    public GiteeReleaseDownloader(String prefixName, String giteeUpdateRepoUrl) {
        this.prefixName = prefixName;
        this.giteeUpdateRepoUrl = giteeUpdateRepoUrl;
        this.prepend = ".zip";
        if (!this.giteeUpdateRepoUrl.endsWith("/")) {
            this.giteeUpdateRepoUrl += "/";
        }
    }

    @Override
    public void postGetLatestInfo(String latestInfo) {
        JSONResolver resolver = getJsonResolver().setOrigin(latestInfo);
        body = resolver.getString("body");
        nodeId = resolver.getString("target_commitish");
        tagName = resolver.getString("tag_name");
    }

    @Override
    public String getUpdateSummary() {
        String updateInfo = getLatestInfo();
        if (updateInfo != null) {
            return getJsonResolver().newObject()
                    .put("body", body)
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
            return giteeUpdateRepoUrl + prefixName + tagName + this.prepend;
        }
        getListener().updateGui("failed to get download url");
        return null;
    }

    @Override
    protected void setLocalVersion() {
        String latestInfo = this.getLatestInfo();

        if (latestInfo == null || getJsonResolver().getString(latestInfo, "tag_name") == null) {
            getListener().updateGui("无法获取最新版本信息");
            return;
        }
        JSONResolver versionInfo = getJsonResolver().newObject();
        versionInfo.put("version", tagName).put("nodeId", nodeId);
        doWriteVersionInfo(versionInfo.toJSONString());
    }
}
