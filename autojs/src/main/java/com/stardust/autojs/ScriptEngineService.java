package com.stardust.autojs;

import android.content.Context;
import android.util.Log;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.engine.ScriptExecuteActivity;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.script.ScriptSource;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptEngineService {

    private static final String LOG_TAG = "ScriptEngineService";
    private static ScriptEngineService instance;

    public static ScriptEngineService getInstance() {
        return instance;
    }

    public static void setInstance(ScriptEngineService service) {
        instance = service;
    }

    private final ScriptRuntime mRuntime;
    private final Context mContext;
    private final Console mConsole;
    private final JavaScriptEngineManager mJavaScriptEngineManager;
    private final EngineLifecycleObserver mEngineLifecycleObserver = new EngineLifecycleObserver();
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
        mJavaScriptEngineManager.setEngineLifecycleCallback(mEngineLifecycleObserver);
    }

    public ScriptRuntime getRuntime() {
        return mRuntime;
    }

    public Console getConsole() {
        return mConsole;
    }

    public ScriptExecutionListener getDefaultListener() {
        return mDefaultListener;
    }

    public JavaScriptEngine createScriptEngine() {
        JavaScriptEngine engine = mJavaScriptEngineManager.createEngine();
        Log.i(LOG_TAG, Arrays.toString(Thread.currentThread().getStackTrace()));
        return engine;
    }

    public void registerEngineLifecycleCallback(JavaScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.registerCallback(engineLifecycleCallback);
    }

    public void unregisterEngineLifecycleCallback(JavaScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.unregisterCallback(engineLifecycleCallback);
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

    public ScriptExecution execute(ScriptExecutionTask task) {
        ScriptExecutionListener listener = task.getExecutionListenerOrDefault(mDefaultListener);
        ScriptSource source = task.getSource();
        int mode = source.getExecutionMode();
        if ((mode & ScriptSource.EXECUTION_MODE_UI) != 0) {
            ScriptExecuteActivity.execute(mContext, task);
            return null;
        } else {
            RunnableScriptExecution scriptExecution = new RunnableScriptExecution(source, listener, task.getConfig());
            if (task.getConfig().runInNewThread) {
                new Thread(scriptExecution).start();
            } else {
                scriptExecution.run();
            }
            return scriptExecution;
        }
    }

    public ScriptExecution execute(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
        return execute(new ScriptExecutionTask(source, listener, config));
    }

    public ScriptExecution execute(ScriptSource source, ScriptExecutionListener listener) {
        return execute(source, listener, ExecutionConfig.getDefault());
    }

    public ScriptExecution execute(ScriptSource source) {
        return execute(source, mDefaultListener, ExecutionConfig.getDefault());
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

    public String[] getGlobalFunctions() {
        return mJavaScriptEngineManager.getGlobalFunctions();
    }

    public Set<JavaScriptEngine> getEngines() {
        return mJavaScriptEngineManager.getEngines();
    }

    private class RunnableScriptExecution extends ScriptExecutionTask implements ScriptExecution, Runnable {

        private JavaScriptEngine mJavaScriptEngine;

        RunnableScriptExecution(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
            super(source, listener, config);
        }

        @Override
        public void run() {
            mJavaScriptEngine = createScriptEngine();
            execute(mRuntime, mJavaScriptEngine);
        }

        @Override
        public JavaScriptEngine getEngine() {
            return mJavaScriptEngine;
        }

        @Override
        public ScriptRuntime getRuntime() {
            return mRuntime;
        }
    }

    private static class EngineLifecycleObserver implements JavaScriptEngineManager.EngineLifecycleCallback {

        private final Set<JavaScriptEngineManager.EngineLifecycleCallback> mEngineLifecycleCallbacks = new LinkedHashSet<>();

        @Override
        public void onEngineCreate(JavaScriptEngine engine) {
            synchronized (mEngineLifecycleCallbacks) {
                for (JavaScriptEngineManager.EngineLifecycleCallback callback : mEngineLifecycleCallbacks) {
                    callback.onEngineCreate(engine);
                }
            }
        }

        @Override
        public void onEngineRemove(JavaScriptEngine engine) {
            synchronized (mEngineLifecycleCallbacks) {
                for (JavaScriptEngineManager.EngineLifecycleCallback callback : mEngineLifecycleCallbacks) {
                    callback.onEngineRemove(engine);
                }
            }
        }

        void registerCallback(JavaScriptEngineManager.EngineLifecycleCallback callback) {
            synchronized (mEngineLifecycleCallbacks) {
                mEngineLifecycleCallbacks.add(callback);
            }
        }

        void unregisterCallback(JavaScriptEngineManager.EngineLifecycleCallback callback) {
            synchronized (mEngineLifecycleCallbacks) {
                mEngineLifecycleCallbacks.remove(callback);
            }
        }
    }
}
