package com.stardust.scriptdroid.droid;

import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.droid.script.GBJDuktapeJavaScriptEngine;
import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.file.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Stardust on 2017/1/23.
 */

public class Droid {

    private static final DroidRuntime RUNTIME = DroidRuntime.getRuntime();
    private static final JavaScriptEngine JAVA_SCRIPT_ENGINE = new GBJDuktapeJavaScriptEngine(RUNTIME);
    private static Droid instance = new Droid();

    protected Droid() {

    }

    public static Droid getInstance() {
        return instance;
    }

    public void runScriptFile(File file) {
        checkFile(file);
        runScript(FileUtils.readString(file));
    }

    public void runScriptFile(String path) {
        runScriptFile(new File(path));
    }

    public void runScript(String script) {
        new Thread(() -> {
            try {
                JAVA_SCRIPT_ENGINE.execute(script);
            } catch (Exception e) {
                RUNTIME.toast("错误" + e.getMessage());
            }
        }).start();
    }

    private void checkFile(File file) {
        if (file == null) {
            throw new NullPointerException("file = null");
        }
        if (!file.exists()) {
            throw new RuntimeException(new FileNotFoundException(file.getAbsolutePath()));
        }
        if (!file.canRead()) {
            throw new RuntimeException("file is not readable: path=" + file.getAbsolutePath());
        }
    }


}
