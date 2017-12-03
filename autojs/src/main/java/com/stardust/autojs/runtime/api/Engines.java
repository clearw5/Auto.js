package com.stardust.autojs.runtime.api;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.StringScriptSource;

/**
 * Created by Stardust on 2017/8/4.
 */

public class Engines {

    private ScriptEngineService mEngineService;
    private ScriptEngine<JavaScriptSource> mScriptEngine;

    public Engines(ScriptEngineService engineService) {
        mEngineService = engineService;
    }

    public ScriptExecution execScript(String name, String script, ExecutionConfig config) {
        return mEngineService.execute(new StringScriptSource(name, script), config);
    }

    public ScriptExecution execScriptFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new JavaScriptFileSource(path), config);
    }

    public ScriptExecution execAutoFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new AutoFileSource(path), config);
    }

    public int stopAll() {
        return mEngineService.stopAll();
    }

    public void stopAllAndToast() {
        mEngineService.stopAllAndToast();
    }


    public void setCurrentEngine(ScriptEngine<JavaScriptSource> engine) {
        mScriptEngine = engine;
    }

    public ScriptEngine<JavaScriptSource> myEngine() {
        return mScriptEngine;
    }
}
