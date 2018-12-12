package com.stardust.autojs.project;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.script.JavaScriptFileSource;

import java.io.File;

public class ProjectLauncher {

    private String mProjectDir;
    private File mMainScriptFile;
    private ProjectConfig mProjectConfig;

    public ProjectLauncher(String projectDir) {
        mProjectDir = projectDir;
        mProjectConfig = ProjectConfig.fromProjectDir(projectDir);
        mMainScriptFile = new File(mProjectDir, mProjectConfig.getMainScriptFile());
    }

    public void launch(ScriptEngineService service) {
        ExecutionConfig config = new ExecutionConfig();
        config.setWorkingDirectory(mProjectDir);
        config.getScriptConfig().setFeatures(mProjectConfig.getFeatures());
        service.execute(new JavaScriptFileSource(mMainScriptFile), config);
    }

}
