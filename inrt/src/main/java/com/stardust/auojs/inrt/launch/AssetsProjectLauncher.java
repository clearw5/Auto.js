package com.stardust.auojs.inrt.launch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.stardust.auojs.inrt.BuildConfig;
import com.stardust.auojs.inrt.LogActivity;
import com.stardust.auojs.inrt.Pref;
import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
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
    private Context mActivity;
    private Handler mHandler;
    private ScriptExecution mScriptExecution;

    public AssetsProjectLauncher(String projectDir, Context context) {
        mAssetsProjectDir = projectDir;
        mActivity = context;
        mProjectDir = new File(context.getFilesDir(), "project/").getPath();
        mProjectConfig = ProjectConfig.fromAssets(context, ProjectConfig.configFileOfDir(mAssetsProjectDir));
        mMainScriptFile = new File(mProjectDir, mProjectConfig.getMainScriptFile());
        mHandler = new Handler(Looper.getMainLooper());
        prepare();
    }

    public void launch(Activity activity) {
        //如果需要隐藏日志界面，则直接运行脚本
        if (mProjectConfig.getLaunchConfig().shouldHideLogs() || Pref.shouldHideLogs()) {
            activity.finish();
            runScript();
        } else {
            //如果不隐藏日志界面
            //如果当前已经是日志界面则直接运行脚本
            if (activity instanceof LogActivity) {
                runScript();
            } else {
                //否则显示日志界面并在日志界面中运行脚本
                mHandler.post(() -> {
                    activity.startActivity(new Intent(mActivity, LogActivity.class)
                            .putExtra(LogActivity.EXTRA_LAUNCH_SCRIPT, true));
                    activity.finish();
                });
            }
        }
    }

    private void runScript() {
        if (mScriptExecution != null && mScriptExecution.getEngine() != null &&
                !mScriptExecution.getEngine().isDestroyed()) {
            return;
        }
        try {
            JavaScriptFileSource source = new JavaScriptFileSource("main", mMainScriptFile);
            mScriptExecution = AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                    .executePath(mProjectDir));
        } catch (Exception e) {
            AutoJs.getInstance().getGlobalConsole().error(e);
        }
    }

    private void prepare() {
        String projectConfigPath = PFiles.join(mProjectDir, ProjectConfig.CONFIG_FILE_NAME);
        ProjectConfig projectConfig = ProjectConfig.fromFile(projectConfigPath);
        if (!BuildConfig.DEBUG && projectConfig != null && projectConfig.getVersionCode() == mProjectConfig.getVersionCode()) {
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
