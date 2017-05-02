package com.stardust.autojs;

import android.content.Context;
import android.util.Log;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.engine.ScriptExecuteActivity;
import com.stardust.autojs.runtime.ScriptInterrupptedException;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.lang.ThreadCompat;
import com.stardust.util.Supplier;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptEngineService {

    private static final String LOG_TAG = "ScriptEngineService";
    private static final EventBus EVENT_BUS = new EventBus();
    private static final ScriptExecutionListener DEFAULT_LISTENER = new SimpleScriptExecutionListener() {
        @Override
        public void onStart(JavaScriptEngine engine, ScriptSource source) {
            EVENT_BUS.post(new ScriptExecutionEvent(ScriptExecutionEvent.ON_START, source.toString()));
        }

        @Override
        public void onException(JavaScriptEngine engine, ScriptSource source, Exception e) {
            if (!causedByInterrupted(e)) {
                EVENT_BUS.post(new ScriptExecutionEvent(ScriptExecutionEvent.ON_EXCEPTION, e.getMessage()));
            }
        }
    };


    private final Supplier<ScriptRuntime> mRuntimeSupplier;
    private final Context mContext;
    private ScriptRuntime mGlobalScriptRuntime;
    private final Console mGlobalConsole;
    private final JavaScriptEngineManager mJavaScriptEngineManager;
    private final EngineLifecycleObserver mEngineLifecycleObserver = new EngineLifecycleObserver();
    private ScriptExecutionListener mDefaultScriptExecutionListener = DEFAULT_LISTENER;

    ScriptEngineService(ScriptEngineServiceBuilder builder) {
        mRuntimeSupplier = builder.mRuntimeSupplier;
        mContext = builder.mContext;
        mJavaScriptEngineManager = builder.mJavaScriptEngineManager;
        mGlobalScriptRuntime = mRuntimeSupplier.get();
        mGlobalConsole = mGlobalScriptRuntime.console;
        mJavaScriptEngineManager.setEngineLifecycleCallback(mEngineLifecycleObserver);
        EVENT_BUS.register(this);
    }

    public Console getGlobalConsole() {
        return mGlobalConsole;
    }

    public ScriptExecutionListener getDefaultScriptExecutionListener() {
        return mDefaultScriptExecutionListener;
    }

    public void setDefaultScriptExecutionListener(ScriptExecutionListener defaultScriptExecutionListener) {
        mDefaultScriptExecutionListener = defaultScriptExecutionListener;
    }

    public JavaScriptEngine createScriptEngine() {
        JavaScriptEngine engine = mJavaScriptEngineManager.createEngine();
        Log.d(LOG_TAG, Arrays.toString(Thread.currentThread().getStackTrace()));
        return engine;
    }

    public ScriptRuntime createScriptRuntime() {
        return mRuntimeSupplier.get();
    }

    public void registerEngineLifecycleCallback(JavaScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.registerCallback(engineLifecycleCallback);
    }

    public void unregisterEngineLifecycleCallback(JavaScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.unregisterCallback(engineLifecycleCallback);
    }

    public static boolean causedByInterrupted(Throwable e) {
        return e instanceof ScriptInterrupptedException || e.getCause() instanceof ScriptInterrupptedException;
    }

    public ScriptExecution execute(ScriptExecutionTask task) {
        task.setDefaultListenerIfNeeded(mDefaultScriptExecutionListener);
        if (isUiMode(task)) {
            return ScriptExecuteActivity.execute(mContext, this, task);
        } else {
            RunnableScriptExecution scriptExecution = new RunnableScriptExecution(this, task);
            if (task.getConfig().runInNewThread) {
                new ThreadCompat(scriptExecution).start();
            } else {
                scriptExecution.run();
            }
            return scriptExecution;
        }
    }

    private boolean isUiMode(ScriptExecutionTask task) {
        ScriptSource source = task.getSource();
        int mode = source.getExecutionMode();
        return (mode & ScriptSource.EXECUTION_MODE_UI) != 0;
    }

    public ScriptExecution execute(ScriptSource source, ScriptExecutionListener listener, ExecutionConfig config) {
        return execute(new ScriptExecutionTask(source, listener, config));
    }

    public ScriptExecution execute(ScriptSource source, ScriptExecutionListener listener) {
        return execute(source, listener, ExecutionConfig.getDefault());
    }

    public ScriptExecution execute(ScriptSource source) {
        return execute(source, mDefaultScriptExecutionListener, ExecutionConfig.getDefault());
    }

    @Subscribe
    public void onScriptExecution(ScriptExecutionEvent event) {
        if (event.getCode() == ScriptExecutionEvent.ON_START) {
            mGlobalConsole.verbose(DateFormat.getTimeInstance().format(new Date()) + " " + mContext.getString(R.string.text_start_running) + " " + event.getMessage());
        } else if (event.getCode() == ScriptExecutionEvent.ON_EXCEPTION) {
            mGlobalScriptRuntime.toast(mContext.getString(R.string.text_error) + ": " + event.getMessage());
            mGlobalConsole.error(event.getMessage());
        }
    }

    public int stopAll() {
        return mJavaScriptEngineManager.stopAll();
    }


    public void stopAllAndToast() {
        int n = stopAll();
        if (n > 0)
            mGlobalScriptRuntime.toast(String.format(mContext.getString(R.string.text_already_stop_n_scripts), n));
        else
            mGlobalScriptRuntime.toast(mContext.getString(R.string.text_no_running_script));
    }

    public String[] getGlobalFunctions() {
        return mJavaScriptEngineManager.getGlobalFunctions();
    }

    public Set<JavaScriptEngine> getEngines() {
        return mJavaScriptEngineManager.getEngines();
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


    private static class ScriptExecutionEvent {

        static final int ON_START = "Eating...".hashCode();
        static final int ON_SUCCESS = "I...lov...".hashCode();
        static final int ON_EXCEPTION = "...Sorry...I should not have said it...".hashCode();

        private final int mCode;
        private final String mMessage;

        ScriptExecutionEvent(int code, String message) {
            mCode = code;
            mMessage = message;
        }

        public int getCode() {
            return mCode;
        }

        public String getMessage() {
            return mMessage;
        }
    }
}
