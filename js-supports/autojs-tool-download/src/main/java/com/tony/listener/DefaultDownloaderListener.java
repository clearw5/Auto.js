package com.tony.listener;

import com.tony.ProgressInfo;
import com.tony.resolver.DefaultGSONResolver;
import com.tony.resolver.JSONResolver;

import java.util.Locale;

/**
 * @author TonyJiang 2019/11/22
 */
public class DefaultDownloaderListener implements DownloaderListener {

    private JSONResolver jsonResolver;

    public DefaultDownloaderListener(JSONResolver jsonResolver) {
        this.jsonResolver = jsonResolver;
    }

    public DefaultDownloaderListener() {
    }

    @Override
    public void updateGui(String string) {
        System.out.println(string);
    }

    @Override
    public void updateError(String errorInfo) {
        System.err.println(errorInfo);
    }

    @Override
    public void updateProgress(ProgressInfo progressInfo) {
        System.out.println("更新进度：" + getJsonResolver().toJSONString(progressInfo)
                + String.format(Locale.CHINA, " %.2f%%", progressInfo.getProgress()));
    }

    public DefaultDownloaderListener setJsonResolver(JSONResolver jsonResolver) {
        this.jsonResolver = jsonResolver;
        return this;
    }

    public JSONResolver getJsonResolver() {
        if (jsonResolver == null) {
            jsonResolver = new DefaultGSONResolver();
        }
        return jsonResolver;
    }
}
