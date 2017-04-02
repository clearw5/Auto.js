package com.stardust.autojs;

import android.content.Context;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.ScriptExecuteActivity;
import com.stardust.autojs.engine.ScriptExecutionListener;
import com.stardust.autojs.engine.ScriptExecutionTask;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.engine.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.engine.JavaScriptEngineManager;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptEngineService {

    private static ScriptEngineService instance;

    public static ScriptEngineService getInstance() {
        return instance;
    }

    public static void setInstance(ScriptEngineService service) {
        instance = service;
    }

    private ScriptRuntime mRuntime;
    private Context mContext;
    private Console mConsole;
    private final JavaScriptEngineManager mJavaScriptEngineManager;
    private final ScriptExecutionListener mDefaultListener = new SimpleScriptExecutionListener() {

        @Override
        public void onStart(JavaScriptEngine engine, ScriptSource source) {
            mConsole.v(DateFormat.getTimeInstance().format(new Date()) + " " + mContext.getString(R.string.text_start_running) + " " + source);
        }

        @Override
        public void onException(JavaScriptEngine engine, ScriptSource source, Exception e) {
            if (!causedByInterruptedException(e)) {
                mRuntime.toast(mContext.getString(R.string.text_error) + ": " + e.getMessage());
                mConsole.e(e.getMessage());
            }
        }

    };

    ScriptEngineService(ScriptEngineServiceBuilder builder) {
        mRuntime = builder.mRuntime;
        mContext = builder.mContext;
        mJavaScriptEngineManager = builder.mJavaScriptEngineManager;
        mConsole = builder.mConsole;
    }

    public ScriptRuntime getRuntime() {
        return mRuntime;
    }

    public JavaScriptEngineManager getJavaScriptEngineManager() {
        return mJavaScriptEngineManager;
    }

    public Console getConsole() {
        return mConsole;
    }

    public ScriptExecutionListener getDefaultListener() {
        return mDefaultListener;
    }

    public JavaScriptEngine createScriptEngine() {
        return mJavaScriptEngineManager.createEngine();
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

    public void execute(ScriptExecutionTask task) {
        ScriptExecutionListener listener = task.getExecutionListenerOrDefault(mDefaultListener);
        ScriptSource source = task.getScriptSource();
        int mode = source.getExecutionMode();
        if ((mode & ScriptSource.EXECUTION_MODE_UI) != 0) {
            ScriptExecuteActivity.execute(mContext, task);
        } else {
            new Thread(new ScriptExecution(source, listener, task.getExecutionConfig())).start();
        }
    }

    public void execute(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
        execute(new ScriptExecutionTask(source, listener, config));
    }

    public int stopAll() {
        return mJavaScriptEngineManager.stopAll();
    }


    public void stopAllAndToast() {
        int n = stopAll();
        if (n > 0)
            mRuntime.toast(String.format(mContext.getString(R.string.text_already_stop_n_scripts), n));
        else
            mRuntime.toast(mContext.getString(R.string.text_no_running_script));
    }

    public void execute(ScriptSource source, ScriptExecutionListener listener) {
        execute(source, listener, ExecutionConfig.getDefault());
    }

    public void execute(ScriptSource source) {
        execute(source, mDefaultListener, ExecutionConfig.getDefault());
    }

    private class ScriptExecution extends ScriptExecutionTask implements Runnable {

        public ScriptExecution(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
            super(source, listener, config);
        }

        @Override
        public void run() {
            execute(mRuntime, mJavaScriptEngineManager.createEngine());
        }

    }
}
