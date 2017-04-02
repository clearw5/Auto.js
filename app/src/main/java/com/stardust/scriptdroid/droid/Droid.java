package com.stardust.scriptdroid.droid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.ScriptExecuteActivity;
import com.stardust.scriptdroid.service.VolumeChangeObverseService;
import com.stardust.scriptdroid.tool.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Stardust on 2017/1/23.
 */

public class Droid {

    public static final java.lang.String STAY = "\"stay\";";

    public static final String UI = "\"ui\";";

    public static final String AUTO = "\"auto\";";

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
    public static final JavaScriptEngine JAVA_SCRIPT_ENGINE = JavaScriptEngine.getDefault();
    private static final OnRunFinishedListener DEFAULT_LISTENER = new SimpleOnRunFinishedListener() {
        @Override
        public void onException(@NonNull Exception e) {
            if (!causedByInterruptedException(e)) {
                RUNTIME.toast(App.getApp().getString(R.string.text_error) + ": " + e.getMessage());
                Timber.e(e.getMessage());
            }
        }
    };
    private static Droid instance = new Droid();
    private VolumeChangeObverseService.OnVolumeChangeListener mOnVolumeChangeListener = new VolumeChangeObverseService.OnVolumeChangeListener() {
        @Override
        public void onVolumeChange() {
            if (Pref.isRunningVolumeControlEnabled()) {
                stopAllAndToast();
            }
        }
    };


    private Droid() {
        VolumeChangeObverseService.addOnVolumeChangeListener(mOnVolumeChangeListener);
    }

    public static Droid getInstance() {
        return instance;
    }

    public static boolean causedByInterruptedException(Throwable e) {
        while (e != null) {
            if (e instanceof InterruptedException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    public void runScriptFile(File file) {
        runScriptFile(file, null, new RunningConfig());
    }

    public void runScriptFile(File file, OnRunFinishedListener listener, RunningConfig config) {
        Timber.v(DateFormat.getTimeInstance().format(new Date()) + " " + App.getResString(R.string.text_start_running) + " " + file);
        listener = listener == null ? DEFAULT_LISTENER : listener;
        int errorMsgId = PathChecker.check(file.getPath());
        if(errorMsgId != PathChecker.CHECK_RESULT_OK){
            listener.onException(new IOException(App.getResString(errorMsgId)));
        }
        runScript(FileUtils.readString(file), listener, config.path(file.getPath()));
    }


    public void runScriptFile(String path) {
        runScriptFile(new File(path));
    }

    public void runScript(String script) {
        runScript(script, null, RunningConfig.getDefault());
    }

    public void runScript(String script, OnRunFinishedListener listener, RunningConfig config) {
        App.getApp().startService(new Intent(App.getApp(), VolumeChangeObverseService.class));
        listener = listener == null ? DEFAULT_LISTENER : listener;
        if (!TextUtils.isEmpty(config.prepareScript))
            script = config.prepareScript + "\n" + script;
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


    public void stopAllAndToast() {
        int n = stopAll();
        if (n > 0)
            RUNTIME.toast(String.format(App.getResString(R.string.text_already_stop_n_scripts), n));
        else
            RUNTIME.toast(App.getResString(R.string.text_no_running_script));
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
                if (mScript.startsWith(AUTO))
                    DroidRuntime.getRuntime().ensureAccessibilityServiceEnabled();
                mOnRunFinishedListener.onRunFinished(JAVA_SCRIPT_ENGINE.execute(mScript));
            } catch (Exception e) {
                mOnRunFinishedListener.onException(e);
            }
        }

    }
}
