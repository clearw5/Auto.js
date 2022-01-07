package com.tony.downloader;

import android.annotation.SuppressLint;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.tony.ProgressInfo;
import com.tony.listener.DefaultDownloaderListener;
import com.tony.listener.DownloaderListener;
import com.tony.resolver.DefaultGSONResolver;
import com.tony.resolver.JSONResolver;
import com.tony.util.ZipUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author TonyJiang 2019/11/11
 */
public abstract class AbstractDownloader {

    private String targetReleasesApiUrl;
    private String outputDir;
    private HttpURLConnection urlConnection;
    private List<String> unzipSkipFiles;
    private List<String> backupIgnoreFiles;
    private String latestObj;
    private DownloaderListener listener;
    private ZipUtil zipUtil;
    private JSONResolver jsonResolver;
    /**
     * 尝试获取content-length的次数
     */
    private int tryCount;

    public AbstractDownloader() {
        tryCount = 5;
    }

    public String getLatestInfo() {
        return getLatestInfo(false);
    }

    public String getLatestInfo(boolean forceUpdate) {
        if (forceUpdate || latestObj == null) {
            ByteArrayOutputStream byteArrayOutputStream = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) new URL(targetReleasesApiUrl).openConnection();
                //设置连接时间，10秒
                urlConnection.setConnectTimeout(10 * 1000);
                urlConnection.setReadTimeout(10 * 1000);

                //数据编码格式，这里utf-8
                urlConnection.setRequestProperty("Charset", "utf-8");

                //设置返回结果的类型，这里是json
                urlConnection.setRequestProperty("accept", "application/json");

                //这里设置post传递的内容类型，这里json
                urlConnection.setRequestProperty("Content-Type", "application/json");

                System.setProperty("https.protocols", "TLSv1.2");
                urlConnection.connect();
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    byte[] buff = new byte[1024];
                    int l = 0;
                    while ((l = inputStream.read(buff)) > 0) {
                        byteArrayOutputStream.write(buff, 0, l);
                    }
                    byte[] contentBytes = byteArrayOutputStream.toByteArray();
                    String result = new String(contentBytes, StandardCharsets.UTF_8);

                    latestObj = result;

                    postGetLatestInfo(result);
                }
            } catch (IOException e) {
                getListener().updateGui("获取latestInfo请求异常：" + e.getMessage() + "，请多试几次或者尝试切换更新源");
                printExceptionInfo(e);
            } finally {
                finallyCloseStreams(inputStream, byteArrayOutputStream);
            }
        }
        return latestObj;
    }

    public void postGetLatestInfo(String releaseInfo) {
    }

    public abstract String getUpdateSummary();

    protected abstract String getZipDownloadUrl();


    private HttpURLConnection createDownloadConnection(String url) throws IOException {
        urlConnection = (HttpURLConnection) new URL(url).openConnection();
        //设置连接时间，100秒
        urlConnection.setConnectTimeout(100 * 1000);
        urlConnection.setReadTimeout(100 * 1000);

        //数据编码格式，这里utf-8
        urlConnection.setRequestProperty("Charset", "utf-8");
        urlConnection.setRequestProperty("Accept-Encoding", "identity");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
        urlConnection.connect();
        return urlConnection;
    }

    private double tryGetConnectionWithLength(String url, int tryCount) throws IOException {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final AtomicInteger triedTime = new AtomicInteger(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (!completed.get()) {
                    StringBuilder sb = new StringBuilder("第").append(triedTime.get()).append("次获取下载包总大小");
                    for (int i = 0; i <= count % 4; i++) {
                        sb.append(".");
                    }
                    getListener().updateGui(sb.toString());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        }).start();

        double totalLength = -1;
        do {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            createDownloadConnection(url);
            totalLength = urlConnection.getContentLength();
        } while (totalLength < 0 && triedTime.getAndAdd(1) < tryCount);
        completed.set(true);
        return totalLength;
    }


    @SuppressLint("DefaultLocale")
    public void downloadZip() {
        String zipUrl = getZipDownloadUrl();
        if (zipUrl != null) {
            getListener().updateGui("得到下载地址：" + zipUrl);
            ByteArrayOutputStream byteArrayOutputStream = null;
            InputStream inputStream = null;
            try {
                double totalLength = tryGetConnectionWithLength(zipUrl, tryCount);
                getListener().updateGui("最终获取http总大小：" + totalLength);
                byteArrayOutputStream = new ByteArrayOutputStream();
                inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    getListener().updateGui("无可下载内容");
                    return;
                }

                int l = -1;
                byte[] buff = new byte[1024];
                int readLength = 0;
                int readCount = 0;
                String content = null;
                boolean showRealProgress = totalLength > 0;
                while ((l = inputStream.read(buff)) > 0) {
                    readLength += l;
                    readCount++;
                    content = null;
                    // 每5次更新进度数据
                    boolean displayProgress = readCount % 5 == 0;
                    if (readCount == 1 && !showRealProgress) {
                        content = "未能获取文件总大小，请等待下载完成.";
                    }
                    if (displayProgress) {
                        if (showRealProgress) {
                            content = String.format("下载进度：%.2f%%", ((readLength / totalLength) * 100));
                            getListener().updateProgress(new ProgressInfo((int) totalLength, readLength));
                        } else {
                            // 模拟进度
                            int mockTotal = readLength + 10 * 1024;
                            getListener().updateProgress(new ProgressInfo(mockTotal, readLength));
                        }
                    }
                    if (content != null) {
                        getListener().updateGui(content);
                    }

                    byteArrayOutputStream.write(buff, 0, l);
                }

                // 下载完毕后 更新进度为百分百
                getListener().updateProgress(new ProgressInfo(10, 10));

                byte[] dataContent = byteArrayOutputStream.toByteArray();
                getListener().updateGui("数据总长度：" + dataContent.length);
                unzipBytes(dataContent);
            } catch (IOException e) {
                getListener().updateGui("下载异常：" + e.getMessage());
                printExceptionInfo(e);
            } finally {
                finallyCloseStreams(inputStream, byteArrayOutputStream);
            }
        }
    }

    private void printExceptionInfo(Exception e) {
        try (
                ByteArrayOutputStream baOs = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(baOs)
        ) {
            e.printStackTrace(pw);
            pw.flush();
            String exceptionInfo = new String(baOs.toByteArray());
            getListener().updateError(exceptionInfo);
        } catch (Exception ex) {
        }
    }

    private void unzipBytes(byte[] dataContent) {
        if (dataContent != null && dataContent.length > 0) {
            File file = saveBytesToFile(dataContent);
            unzipFile(file);
        }
    }


    private File saveBytesToFile(byte[] dataContent) {
        File tmpFile = new File(outputDir + "/origin.zip");
        boolean dirExist = tmpFile.getParentFile() != null && tmpFile.getParentFile().exists();
        if (!dirExist && tmpFile.getParentFile() != null) {
            dirExist = tmpFile.getParentFile().mkdirs();
        }
        if (dirExist) {
            getListener().updateGui("保存到临时文件：" + tmpFile.getAbsolutePath());
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(tmpFile);
                fileOutputStream.write(dataContent);
                fileOutputStream.flush();
                return tmpFile;
            } catch (IOException e) {
                getListener().updateGui("保存到临时文件异常：" + e.getMessage());
                printExceptionInfo(e);
            } finally {
                finallyCloseStreams(fileOutputStream);
            }
        }
        return null;
    }

    private void unzipFile(File file) {
        if (getZipUtil().decompress(file, outputDir)) {
            this.setLocalVersion();
            getListener().updateGui("解压成功！");
        } else {
            getListener().updateError("解压失败！");
        }
    }


    public boolean backup() {
        String localVersionName = this.getLocalVersion();
        String backupFileName = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        String dateStr = sdf.format(new Date());
        if (localVersionName == null) {
            backupFileName = "未知版本号-" + dateStr + ".zip";
        } else {
            backupFileName = localVersionName + "-" + dateStr + ".zip";
        }
        File backupFile = new File(outputDir + "/backup_zip/" + backupFileName);
        boolean backDstPathExist = true;
        if (!backupFile.getParentFile().exists()) {
            backDstPathExist = backupFile.getParentFile().mkdirs();
        }
        if (backDstPathExist && backupFile.getParentFile().isDirectory()) {
            return getZipUtil().compress(outputDir, backupFile.getPath());
        } else {
            getListener().updateGui("备份文件夹不存在:" + backupFile.getParent());
        }
        return false;
    }

    public String getLocalVersion() {
        String versionFilePath = this.outputDir + "/version.json";
        File versionFile = new File(versionFilePath);
        if (!versionFile.exists()) {
            versionFilePath = this.outputDir + "/project.json";
            versionFile = new File(versionFilePath);
        }
        if (versionFile.exists()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(versionFile);
                    BufferedReader buffReader = new BufferedReader(new InputStreamReader(fileInputStream))
            ) {
                String versionContent = buffReader.readLine();
                if (versionContent != null) {
                    try {

                        JSONResolver resolverGetter = getJsonResolver().setOrigin(versionContent);
                        String version = resolverGetter.getString("version");
                        if (version == null || version.equals("")) {
                            version = resolverGetter.getString("versionName");
                        }
                        String nodeId = resolverGetter.getString("nodeId");
                        getListener().updateGui("本地版本：" + version + " nodeId:" + nodeId);
                        return version;
                    } catch (Exception e) {
                        getListener().updateGui("解析版本文件异常" + e.getMessage());
                    }
                }

            } catch (IOException e) {
                getListener().updateGui("读取版本文件异常" + e.getMessage());
            }
        } else {
            getListener().updateGui("版本文件不存在");
        }
        return null;
    }


    protected DownloaderListener getListener() {
        if (listener == null) {
            listener = new DefaultDownloaderListener();
        }
        return listener;
    }

    protected ZipUtil getZipUtil() {
        if (zipUtil == null) {
            zipUtil = new ZipUtil();
        }
        zipUtil.setListener(getListener());
        zipUtil.setUnzipSkipFiles(unzipSkipFiles);
        zipUtil.setZipIgnoredFiles(getBackupIgnoreFiles("/backup_zip", ".DS_Store"));
        return zipUtil;
    }


    private List<String> getBackupIgnoreFiles(String... addIfNones) {
        if (addIfNones != null && addIfNones.length > 0) {
            for (String addIfNone : addIfNones) {
                addBackIgnoresIfNone(addIfNone);
            }
        }
        return backupIgnoreFiles;
    }

    private void addBackIgnoresIfNone(String addIfNone) {
        boolean needAdd = false;
        if (backupIgnoreFiles == null) {
            backupIgnoreFiles = new ArrayList<>();
            needAdd = true;
        } else if (!backupIgnoreFiles.contains(addIfNone)) {
            needAdd = true;
        }
        if (needAdd) {
            backupIgnoreFiles.add(addIfNone);
        }
    }

    private void finallyCloseStreams(Closeable... streams) {
        if (streams != null && streams.length > 0) {
            for (Closeable stream : streams) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected abstract void setLocalVersion();

    protected void doWriteVersionInfo(String versionInfoString) {
        String versionFilePath = this.getOutputDir() + "/version.json";
        File versionFile = new File(versionFilePath);
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(versionFile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        ) {
            bufferedWriter.write(versionInfoString);
            bufferedWriter.flush();
            getListener().updateGui("设置本地版本信息：" + versionInfoString);
        } catch (Exception e) {
            getListener().updateGui("更新版本文件异常");
        }
    }

    public void setListener(DownloaderListener listener) {
        this.listener = listener;
    }

    public List<String> getUnzipSkipFiles() {
        return unzipSkipFiles;
    }

    public void setUnzipSkipFiles(String... unzipSkipFiles) {
        if (unzipSkipFiles != null && unzipSkipFiles.length > 0) {
            this.unzipSkipFiles = new ArrayList<>(Arrays.asList(unzipSkipFiles));
        }
    }

    public void setBackupIgnoreFiles(String... backupIgnoreFiles) {
        if (backupIgnoreFiles != null && backupIgnoreFiles.length > 0) {
            this.backupIgnoreFiles = new ArrayList<>(Arrays.asList(backupIgnoreFiles));
        }
    }

    public String getTargetReleasesApiUrl() {
        return targetReleasesApiUrl;
    }

    public void setTargetReleasesApiUrl(String targetReleasesApiUrl) {
        this.targetReleasesApiUrl = targetReleasesApiUrl;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public void setJsonResolver(JSONResolver jsonResolver) {
        this.jsonResolver = jsonResolver;
    }

    public JSONResolver getJsonResolver() {
        if (jsonResolver == null) {
            jsonResolver = new DefaultGSONResolver();
        }
        return jsonResolver;
    }
}
