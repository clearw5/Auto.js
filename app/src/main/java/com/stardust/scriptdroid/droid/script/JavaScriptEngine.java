package com.stardust.scriptdroid.droid.script;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.droid.script.file.AssetScript;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class JavaScriptEngine {

    Map<String, Object> mVariableMap = new HashMap<>();

    protected String preprocess(String script) {
        return script;
    }

    public abstract Object execute(String script);

    public void set(String varName, Object value) {
        mVariableMap.put(varName, value);
    }

    public abstract void removeAndDestroy();

    public abstract int stopAll();

    static class Init {
        private static final String INIT_SCRIPT = readInitScript();

        private static String readInitScript() {
            return AssetScript.read(App.getApp(), "javasccript_engine_init.js");
        }

        static String getInitScript() {
            if (BuildConfig.DEBUG) {
                return readInitScript();
            } else {
                return INIT_SCRIPT;
            }
        }
    }
}
