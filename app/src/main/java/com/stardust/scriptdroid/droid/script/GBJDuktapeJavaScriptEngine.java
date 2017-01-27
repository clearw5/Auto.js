package com.stardust.scriptdroid.droid.script;

import com.efurture.script.JSTransformer;
import com.furture.react.DuktapeEngine;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Stardust on 2017/1/27.
 */

public class GBJDuktapeJavaScriptEngine implements JavaScriptEngine {


    private DuktapeEngine mDuktapeEngine = new DuktapeEngine();
    private boolean mRecycled = false;
    private String init = Init.readInitScript();

    public GBJDuktapeJavaScriptEngine(IDroidRuntime runtime) {
        setRuntime(runtime);
        mDuktapeEngine.put("context", App.getApp());
        execute(init);
    }

    private void setRuntime(IDroidRuntime runtime) {
        set("droid", IDroidRuntime.class, runtime);
    }

    @Override
    public void execute(String script) {
        try {
            mDuktapeEngine.execute(JSTransformer.parse(new StringReader(script)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void set(String varName, Class<T> c, T value) {
        mDuktapeEngine.put(varName, value);
    }

}
