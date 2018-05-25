package org.autojs.autojs.network.download;

/**
 * Created by Stardust on 2017/10/20.
 */

public class DownloadFailedException extends Throwable {
    private final String mUrl;
    private final String mPath;

    public DownloadFailedException(String url, String path) {
        mUrl = url;
        mPath = path;
    }

    @Override
    public String toString() {
        return "DownloadFailedException{" +
                "url='" + mUrl + '\'' +
                ", path='" + mPath + '\'' +
                "} " + super.toString();
    }
}
