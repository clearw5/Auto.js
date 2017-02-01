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
        runScript(FileUtils.readString(file), listener, RunningConfig.getDefault());
    }

    private void runScript(String script) {
        runScript(script, null, RunningConfig.getDefault());
    }

    public void runScriptFile(String path) {
        runScriptFile(new File(path));
    }

    public void runScript(final String script, OnRunFinishedListener listener, RunningConfig config) {
        if (config.runInNewThread) {
            new Thread(new RunScriptRunnable(script, listener, config)).start();
        }else {
            new RunScriptRunnable(script, listener, config).run();
        }

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


    private static class RunScriptRunnable implements Runnable {

        private final String mScript;
        private OnRunFinishedListener mOnRunFinishedListener;

        public RunScriptRunnable(String script, OnRunFinishedListener listener, RunningConfig config) {
            mOnRunFinishedListener = listener == null ? DEFAULT_LISTENER : listener;
            mScript = script;
        }

        @Override
        public void run() {
            try {
                mOnRunFinishedListener.onRunFinished(JAVA_SCRIPT_ENGINE.execute(mScript), null);
            } catch (Exception e) {
                mOnRunFinishedListener.onRunFinished(null, e);
            }
        }
    }
}
