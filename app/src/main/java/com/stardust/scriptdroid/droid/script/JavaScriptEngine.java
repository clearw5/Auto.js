package com.stardust.scriptdroid.droid.script;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.script.file.AssetScript;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface JavaScriptEngine {

    void execute(String script);

    <T> void set(String varName, Class<T> c, T value);

    class Init {
        static String readInitScript() {
            return AssetScript.read(App.getApp(), "javasccript_engine_init.js");
        }
    }
}
