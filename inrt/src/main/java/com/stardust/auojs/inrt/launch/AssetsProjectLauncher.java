package com.stardust.auojs.inrt.launch;

import android.app.Activity;

import com.stardust.auojs.inrt.Pref;
import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.pio.PFiles;

import java.io.File;

/**
 * Created by Stardust on 2018/1/24.
 */

public class AssetsProjectLauncher {

    private String mAssetsProjectDir;
    private String mProjectDir;
    private File mMainScriptFile;
    private ProjectConfig mProjectConfig;
    private Activity mActivity;

    public AssetsProjectLauncher(String projectDir, Activity activity) {
        mAssetsProjectDir = projectDir;
        mActivity = activity;
        mProjectDir = new File(activity.getFilesDir(), "project/").getPath();
        mProjectConfig = ProjectConfig.fromAssets(activity, ProjectConfig.configFileOfDir(mAssetsProjectDir));
        mMainScriptFile = new File(mProjectDir, mProjectConfig.getMainScriptFile());
    }

    public void launch() {
        prepare();
        if (mProjectConfig.getLaunchConfig().shouldHideLogs() || Pref.shouldHideLogs()) {
            mActivity.runOnUiThread(mActivity::finish);
        }
        mActivity = null;
        runScript();

    }

    private void runScript() {
        try {
            JavaScriptFileSource source = new JavaScriptFileSource("main", mMainScriptFile);
            AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                    .executePath(mProjectDir));
        } catch (Exception e) {
            AutoJs.getInstance().getGlobalConsole().error(e);
        }
    }

    private void prepare() {
        String projectConfigPath = PFiles.join(mProjectDir, ProjectConfig.CONFIG_FILE_NAME);
        ProjectConfig projectConfig = ProjectConfig.fromFile(projectConfigPath);
        if (projectConfig != null && projectConfig.getVersionCode() == mProjectConfig.getVersionCode()) {
            return;
        }
        PFiles.copyAsset(mActivity, PFiles.join(mAssetsProjectDir, ProjectConfig.CONFIG_FILE_NAME), projectConfigPath);
        PFiles.copyAsset(mActivity, PFiles.join(mAssetsProjectDir, mProjectConfig.getMainScriptFile()),
                mMainScriptFile.getPath());
        for (String asset : mProjectConfig.getAssets()) {
            PFiles.copyAsset(mActivity, PFiles.join(mAssetsProjectDir, asset), PFiles.join(mProjectDir, asset));
        }
    }

}
