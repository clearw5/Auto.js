package com.stardust.scriptdroid.droid;

import android.support.annotation.NonNull;

import com.jraska.console.timber.ConsoleTree;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.droid.runtime.ScriptStopException;
import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.RhinoJavaScriptEngine;
import com.stardust.scriptdroid.droid.script.ScriptExecuteActivity;
import com.stardust.scriptdroid.tool.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import timber.log.Timber;

/**
 * Created by Stardust on 2017/1/23.
 */

public class Droid {

    public static final java.lang.String STAY = "\"stay\";";

    static {
        Timber.plant(new ConsoleTree.Builder()
                .infoColor(0xcc000000)
                .build());
    }

    public static final String UI = "\"ui\";";

    public interface OnRunFinishedListener extends Serializable {
        void onRunFinished(Object result);

        void onException(@NonNull Exception e);
    }

    public static class SimpleOnRunFinishedListener implements OnRunFinishedListener {

        @Override
        public void onRunFinished(Object result) {

        }

        @Override
        public void onException(@NonNull Exception e) {

        }
    }

    private static final DroidRuntime RUNTIME = DroidRuntime.getRuntime();
    public static final JavaScriptEngine JAVA_SCRIPT_ENGINE = new RhinoJavaScriptEngine(RUNTIME);
    private static final OnRunFinishedListener DEFAULT_LISTENER = new SimpleOnRunFinishedListener() {
        @Override
        public void onException(@NonNull Exception e) {
            if (!(e instanceof ScriptStopException)) {
                RUNTIME.toast(App.getApp().getString(R.string.text_error) + ": " + e.getMessage());
                Timber.e(e, App.getApp().getString(R.string.text_error));
            }
        }
    };
    private static Droid instance = new Droid();

    private Droid() {
    }

    public static Droid getInstance() {
        return instance;
    }

    public void runScriptFile(File file) {
        runScriptFile(file, null);
    }

    public void runScriptFile(File file, OnRunFinishedListener listener) {
        Timber.v("开始运行脚本文件: " + file);
        listener = listener == null ? DEFAULT_LISTENER : listener;
        try {
            checkFile(file);
        } catch (Exception e) {
            listener.onException(e);
            return;
        }
        runScript(FileUtils.readString(file), listener, RunningConfig.getDefault());
    }


    public void runScriptFile(String path) {
        runScriptFile(new File(path));
    }

    private void runScript(String script) {
        runScript(script, null, RunningConfig.getDefault());
    }

    public void runScript(final String script, OnRunFinishedListener listener, RunningConfig config) {
        listener = listener == null ? DEFAULT_LISTENER : listener;
        if (config.runInNewThread) {
            if (script.startsWith(UI)) {
                ScriptExecuteActivity.runScript(script, listener, config);
            } else {
                new Thread(new RunScriptRunnable(script, listener, config)).start();
            }
        } else {
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

        RunScriptRunnable(String script, OnRunFinishedListener listener, RunningConfig config) {
            mOnRunFinishedListener = listener;
            mScript = script;
        }

        @Override
        public void run() {
            try {
                mOnRunFinishedListener.onRunFinished(JAVA_SCRIPT_ENGINE.execute(mScript));
            } catch (Exception e) {
                mOnRunFinishedListener.onException(e);
            }
        }

    }
}
