package com.stardust.autojs.runtime.api;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.StringScriptSource;

/**
 * Created by Stardust on 2017/8/4.
 */

public class Engines {

    private ScriptEngineService mEngineService;
    private JavaScriptEngine mScriptEngine;
    private ScriptRuntime mScriptRuntime;

    public Engines(ScriptEngineService engineService, ScriptRuntime scriptRuntime) {
        mEngineService = engineService;
        mScriptRuntime = scriptRuntime;
    }

    public ScriptExecution execScript(String name, String script, ExecutionConfig config) {
        return mEngineService.execute(new StringScriptSource(name, script), config);
    }

    public ScriptExecution execScriptFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new JavaScriptFileSource(mScriptRuntime.files.path(path)), config);
    }

    public ScriptExecution execAutoFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new AutoFileSource(mScriptRuntime.files.path(path)), config);
    }

    public Object all() {
        return mScriptRuntime.bridges.toArray(mEngineService.getEngines());
    }

    public int stopAll() {
        return mEngineService.stopAll();
    }

    public void stopAllAndToast() {
        mEngineService.stopAllAndToast();
    }


    public void setCurrentEngine(JavaScriptEngine engine) {
        if (mScriptEngine != null)
            throw new IllegalStateException();
        mScriptEngine = engine;
    }

    public JavaScriptEngine myEngine() {
        return mScriptEngine;
    }
}
