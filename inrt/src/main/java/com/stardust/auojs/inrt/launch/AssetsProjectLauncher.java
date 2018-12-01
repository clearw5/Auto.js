package com.stardust.auojs.inrt.launch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.stardust.auojs.inrt.BuildConfig;
import com.stardust.auojs.inrt.LogActivity;
import com.stardust.auojs.inrt.Pref;
import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.MD5;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

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
            runScript(activity);
        } else {
            //如果不隐藏日志界面
            //如果当前已经是日志界面则直接运行脚本
            if (activity instanceof LogActivity) {
                runScript(null);
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

    private void runScript(Activity activity) {
        if (mScriptExecution != null && mScriptExecution.getEngine() != null &&
                !mScriptExecution.getEngine().isDestroyed()) {
            return;
        }
        try {
            JavaScriptFileSource source = new JavaScriptFileSource("main", mMainScriptFile);
            ExecutionConfig config = new ExecutionConfig()
                    .executePath(mProjectDir);
            if ((source.getExecutionMode() & JavaScriptSource.EXECUTION_MODE_UI) != 0) {
                config.setIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                if (activity != null) {
                    activity.finish();
                }
            }
            mScriptExecution = AutoJs.getInstance().getScriptEngineService().execute(source, config);
        } catch (Exception e) {
            AutoJs.getInstance().getGlobalConsole().error(e);
        }
    }

    private void prepare() {
        String projectConfigPath = PFiles.join(mProjectDir, ProjectConfig.CONFIG_FILE_NAME);
        ProjectConfig projectConfig = ProjectConfig.fromFile(projectConfigPath);
        initKey(projectConfig);
        if (!BuildConfig.DEBUG && projectConfig != null &&
                TextUtils.equals(projectConfig.getBuildInfo().getBuildId(), mProjectConfig.getBuildInfo().getBuildId())) {
            return;
        }
        PFiles.deleteRecursively(new File(mProjectDir));
        try {
            PFiles.copyAssetDir(mActivity.getAssets(), mAssetsProjectDir, mProjectDir, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void initKey(ProjectConfig projectConfig) {
        if (projectConfig == null) {
            return;
        }
        String key = MD5.md5(projectConfig.getPackageName() + projectConfig.getVersionName() + projectConfig.getMainScriptFile());
        String vec = MD5.md5(projectConfig.getBuildInfo().getBuildId() + projectConfig.getName()).substring(0, 16);
        try {
            Field fieldKey = AutoJs.class.getDeclaredField("mKey");
            fieldKey.setAccessible(true);
            fieldKey.set(AutoJs.getInstance(), key);
            Field fieldVector = AutoJs.class.getDeclaredField("mInitVector");
            fieldVector.setAccessible(true);
            fieldVector.set(AutoJs.getInstance(), vec);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
