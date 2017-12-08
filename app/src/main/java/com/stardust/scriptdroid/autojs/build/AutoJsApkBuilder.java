package com.stardust.scriptdroid.autojs.build;

import com.stardust.autojs.apkbuilder.ApkBuilder;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.ui.build.BuildActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Stardust on 2017/10/24.
 */

public class AutoJsApkBuilder extends ApkBuilder {


    public interface ProgressCallback {
        void onPrepare(AutoJsApkBuilder builder);

        void onBuild(AutoJsApkBuilder builder);

        void onSign(AutoJsApkBuilder builder);

        void onClean(AutoJsApkBuilder builder);

    }

    public static class AppConfig {
        String appName;
        String versionName;
        int versionCode;
        String jsPath;

        public AppConfig(String appName, String versionName, int versionCode, String jsPath) {
            this.appName = appName;
            this.versionName = versionName;
            this.versionCode = versionCode;
            this.jsPath = jsPath;
        }
    }

    private ProgressCallback mProgressCallback;

    public AutoJsApkBuilder(InputStream apkInputStream, File outApkFile, String workspacePath) {
        super(apkInputStream, outApkFile, workspacePath);
    }

    public AutoJsApkBuilder(File inFile, File outFile, String workspacePath) throws FileNotFoundException {
        super(inFile, outFile, workspacePath);
    }

    public AutoJsApkBuilder setProgressCallback(ProgressCallback callback) {
        mProgressCallback = callback;
        return this;
    }

    @Override
    public AutoJsApkBuilder prepare() throws IOException {
        if (mProgressCallback != null) {
            App.getApp().getUiHandler().post(() -> mProgressCallback.onPrepare(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.prepare();
    }

    public AutoJsApkBuilder setScriptFile(String path) throws IOException {
        replaceFile("assets/script.js", path);
        return this;
    }

    public AutoJsApkBuilder withConfig(AppConfig config) throws IOException {
        editManifest().setAppName(config.appName)
                .setVersionName(config.versionName)
                .setVersionCode(config.versionCode)
                .commit();
        setScriptFile(config.jsPath);
        return this;
    }

    @Override
    public AutoJsApkBuilder build() throws IOException {
        if (mProgressCallback != null) {
            App.getApp().getUiHandler().post(() -> mProgressCallback.onBuild(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.build();
    }

    @Override
    public AutoJsApkBuilder sign() throws Exception {
        if (mProgressCallback != null) {
            App.getApp().getUiHandler().post(() -> mProgressCallback.onSign(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.sign();
    }

    @Override
    public AutoJsApkBuilder cleanWorkspace() {
        if (mProgressCallback != null) {
            App.getApp().getUiHandler().post(() -> mProgressCallback.onClean(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.cleanWorkspace();
    }
}
