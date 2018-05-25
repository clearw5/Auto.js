package org.autojs.autojs.autojs.build;

import android.graphics.Bitmap;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.apkbuilder.ApkBuilder;
import com.stardust.autojs.apkbuilder.ManifestEditor;
import com.stardust.autojs.apkbuilder.util.StreamUtils;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.pio.PFiles;
import org.autojs.autojs.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

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
        String packageName;
        Callable<Bitmap> icon;

        public AppConfig setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public AppConfig setVersionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public AppConfig setVersionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public AppConfig setJsPath(String jsPath) {
            this.jsPath = jsPath;
            return this;
        }

        public AppConfig setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }


        public AppConfig setIcon(Callable<Bitmap> icon) {
            this.icon = icon;
            return this;
        }
    }

    private ProgressCallback mProgressCallback;
    private ManifestEditor mManifestEditor;
    private String mWorkspacePath;
    private AppConfig mAppConfig;

    public AutoJsApkBuilder(InputStream apkInputStream, File outApkFile, String workspacePath) {
        super(apkInputStream, outApkFile, workspacePath);
        mWorkspacePath = workspacePath;
    }

    public AutoJsApkBuilder(File inFile, File outFile, String workspacePath) throws FileNotFoundException {
        super(inFile, outFile, workspacePath);
        mWorkspacePath = workspacePath;
    }

    public AutoJsApkBuilder setProgressCallback(ProgressCallback callback) {
        mProgressCallback = callback;
        return this;
    }

    @Override
    public AutoJsApkBuilder prepare() throws IOException {
        if (mProgressCallback != null) {
            GlobalAppContext.post(() -> mProgressCallback.onPrepare(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.prepare();
    }

    public AutoJsApkBuilder setScriptFile(String path) throws IOException {
        if (PFiles.isDir(path)) {
            copyDir("assets/project/", path);
        } else {
            replaceFile("assets/project/main.js", path);
        }
        return this;
    }

    public void copyDir(String relativePath, String path) throws IOException {
        File fromDir = new File(path);
        File toDir = new File(this.mWorkspacePath, relativePath);
        toDir.mkdir();
        for (File child : fromDir.listFiles()) {
            if (child.isFile()) {
                StreamUtils.write(new FileInputStream(child),
                        new FileOutputStream(new File(toDir, child.getName())));
            } else {
                copyDir(PFiles.join(relativePath, child.getName() + "/"), child.getPath());
            }
        }
    }

    @Override
    public ApkBuilder replaceFile(String relativePath, String newFilePath) throws IOException {
        StreamUtils.write(new FileInputStream(newFilePath), new FileOutputStream(new File(this.mWorkspacePath, relativePath)));
        return this;
    }

    public AutoJsApkBuilder withConfig(AppConfig config) throws IOException {
        mAppConfig = config;
        mManifestEditor = editManifest()
                .setAppName(config.appName)
                .setVersionName(config.versionName)
                .setVersionCode(config.versionCode)
                .setPackageName(config.packageName);
        setArscPackageName(config.packageName);
        updateProjectConfig(config);
        setScriptFile(config.jsPath);
        return this;
    }

    private void updateProjectConfig(AppConfig config) {
        if (!PFiles.isDir(config.jsPath)) {
            return;
        }
        ProjectConfig projectConfig = ProjectConfig.fromProjectDir(config.jsPath);
        if (projectConfig == null)
            projectConfig = new ProjectConfig();
        projectConfig.setName(config.appName)
                .setPackageName(config.packageName)
                .setVersionCode(config.versionCode)
                .setVersionName(config.versionName)
                .setMainScriptFile("main.js");
        updateProjectConfigAssets(projectConfig, config.jsPath, config.jsPath);
        PFiles.write(ProjectConfig.configFileOfDir(config.jsPath), projectConfig.toJson());
    }

    private void updateProjectConfigAssets(ProjectConfig config, String projectDir, String dir) {
        File main = new File(projectDir, config.getMainScriptFile());
        for (File file : new File(dir).listFiles()) {
            if (file.isDirectory()) {
                updateProjectConfigAssets(config, projectDir, file.getPath());
                continue;
            }
            if (file.equals(main)) {
                continue;
            }
            String relative = new File(projectDir).toURI().relativize(file.toURI()).getPath();
            config.getAssets().add(relative);
        }
    }

    @Override
    public AutoJsApkBuilder build() throws IOException {
        if (mProgressCallback != null) {
            GlobalAppContext.post(() -> mProgressCallback.onBuild(AutoJsApkBuilder.this));
        }
        mManifestEditor.commit();
        if (mAppConfig.icon != null) {
            try {
                mAppConfig.icon.call().compress(Bitmap.CompressFormat.PNG, 100,
                        new FileOutputStream(new File(mWorkspacePath, "res/mipmap/ic_launcher.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (AutoJsApkBuilder) super.build();
    }

    @Override
    public AutoJsApkBuilder sign() throws Exception {
        if (mProgressCallback != null) {
            GlobalAppContext.post(() -> mProgressCallback.onSign(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.sign();
    }

    @Override
    public AutoJsApkBuilder cleanWorkspace() {
        if (mProgressCallback != null) {
            GlobalAppContext.post(() -> mProgressCallback.onClean(AutoJsApkBuilder.this));
        }
        return (AutoJsApkBuilder) super.cleanWorkspace();
    }
}
