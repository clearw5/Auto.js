package com.stardust.scriptdroid.droid.script;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.script.file.AssetScript;

import java.io.IOException;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface JavaScriptEngine {

    Object execute(String script) throws IOException;

    <T> void set(String varName, Class<T> c, T value);

    int stopAll();

    void ensureNotStopped();

    class Init {
        public static final String INIT_SCRIPT = readInitScript();

        static String readInitScript() {
            return AssetScript.read(App.getApp(), "javasccript_engine_init.js");
        }
    }
}
