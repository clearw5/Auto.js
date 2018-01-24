package com.stardust.auojs.inrt.launch;

import android.content.Context;

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
    private Context mContext;

    public AssetsProjectLauncher(String projectDir, Context context) {
        mAssetsProjectDir = projectDir;
        mContext = context;
        mProjectDir = new File(context.getFilesDir(), "project/").getPath();
        mProjectConfig = ProjectConfig.fromAssets(context, ProjectConfig.configFileOfDir(mAssetsProjectDir));
        mMainScriptFile = new File(mProjectDir, mProjectConfig.getMainScriptFile());
    }

    public void launch() {
        prepare();
        try {
            JavaScriptFileSource source = new JavaScriptFileSource(mMainScriptFile);
            AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                    .executePath(mProjectDir));
        } catch (Exception e) {
            AutoJs.getInstance().getGlobalConsole().error(e);
        }
    }

    private void prepare() {
        if (mMainScriptFile.exists()) {
            return;
        }
        PFiles.copyAsset(mContext, PFiles.join(mAssetsProjectDir, mProjectConfig.getMainScriptFile()),
                mMainScriptFile.getPath());
        for (String asset : mProjectConfig.getAssets()) {
            PFiles.copyAsset(mContext, PFiles.join(mAssetsProjectDir, asset), PFiles.join(mProjectDir, asset));
        }
    }

}
