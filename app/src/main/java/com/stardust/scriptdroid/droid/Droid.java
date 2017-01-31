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

    public interface OnRunFinishedListener {
        void onRunFinished(Object result, Exception e);
    }

    private static final DroidRuntime RUNTIME = DroidRuntime.getRuntime();
    private static final JavaScriptEngine JAVA_SCRIPT_ENGINE = new GBJDuktapeJavaScriptEngine(RUNTIME);
    private static final OnRunFinishedListener DEFAULT_LISTENER = new OnRunFinishedListener() {
        @Override
        public void onRunFinished(Object result, Exception e) {
            if (e != null) {
                RUNTIME.toast("错误: " + e.getMessage());
            }
        }
    };
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

    public void runScriptFile(File file, OnRunFinishedListener listener) {
        checkFile(file);
        runScript(FileUtils.readString(file), listener);
    }

    private void runScript(String script) {
        runScript(script, null);
    }

    public void runScriptFile(String path) {
        runScriptFile(new File(path));
    }

    public void runScript(final String script, OnRunFinishedListener listener) {
        if (listener == null)
            listener = DEFAULT_LISTENER;
        final OnRunFinishedListener finalListener = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalListener.onRunFinished(JAVA_SCRIPT_ENGINE.execute(script), null);
                } catch (Exception e) {
                    finalListener.onRunFinished(null, e);
                }
            }
        }).start();
    }


    public int stopAll() {
        return JAVA_SCRIPT_ENGINE.stopAll();
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
