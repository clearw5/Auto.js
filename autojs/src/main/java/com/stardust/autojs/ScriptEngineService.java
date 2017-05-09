package com.stardust.autojs;

import android.content.Context;

import com.stardust.autojs.engine.AbstractScriptEngineManager;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.execution.ScriptExecuteActivity;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.RunnableScriptExecution;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.ScriptExecutionObserver;
import com.stardust.autojs.execution.ScriptExecutionTask;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.lang.ThreadCompat;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.stardust.autojs.runtime.ScriptInterruptedException.causedByInterrupted;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptEngineService {

    private static final String LOG_TAG = "ScriptEngineService";
    private static final EventBus EVENT_BUS = new EventBus();
    private static final ScriptExecutionListener GLOBAL_LISTENER = new SimpleScriptExecutionListener() {
        @Override
        public void onStart(ScriptExecution execution) {
            execution.getRuntime().console.setTitle(execution.getSource().getName());
            EVENT_BUS.post(new ScriptExecutionEvent(ScriptExecutionEvent.ON_START, execution.getSource().toString()));
        }

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            onFinish(execution);
        }

        private void onFinish(ScriptExecution execution) {
            execution.getRuntime().onStop();
        }

        @Override
        public void onException(ScriptExecution execution, Exception e) {
            e.printStackTrace();
            onFinish(execution);
            if (!causedByInterrupted(e)) {
                execution.getRuntime().console.error(e.getMessage());
                EVENT_BUS.post(new ScriptExecutionEvent(ScriptExecutionEvent.ON_EXCEPTION, e.getMessage()));
            }
        }
    };


    private final Supplier<? extends ScriptRuntime> mRuntimeSupplier;
    private final Context mContext;
    private UiHandler mUiHandler;
    private final Console mGlobalConsole;
    private final ScriptEngineManager mScriptEngineManager;
    private final EngineLifecycleObserver mEngineLifecycleObserver = new EngineLifecycleObserver();
    private ScriptExecutionObserver mScriptExecutionObserver = new ScriptExecutionObserver();

    ScriptEngineService(ScriptEngineServiceBuilder builder) {
        mRuntimeSupplier = builder.mRuntimeSupplier;
        mUiHandler = builder.mUiHandler;
        mContext = mUiHandler.getContext();
        mScriptEngineManager = builder.mScriptEngineManager;
        mGlobalConsole = builder.mGlobalConsole;
        mScriptEngineManager.setEngineLifecycleCallback(mEngineLifecycleObserver);
        mScriptExecutionObserver.registerScriptExecutionListener(GLOBAL_LISTENER);
        EVENT_BUS.register(this);
        mScriptEngineManager.putGlobal("context", mUiHandler.getContext());
    }

    public Console getGlobalConsole() {
        return mGlobalConsole;
    }

    public ScriptEngine createScriptEngine() {
        ScriptEngine engine = mScriptEngineManager.createEngine();
        return engine;
    }

    public ScriptRuntime createScriptRuntime() {
        return mRuntimeSupplier.get();
    }

    public void registerEngineLifecycleCallback(AbstractScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.registerCallback(engineLifecycleCallback);
    }

    public void unregisterEngineLifecycleCallback(AbstractScriptEngineManager.EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleObserver.unregisterCallback(engineLifecycleCallback);
    }

    public boolean registerGlobalScriptExecutionListener(ScriptExecutionListener listener) {
        return mScriptExecutionObserver.registerScriptExecutionListener(listener);
    }

    public boolean unregisterGlobalScriptExecutionListener(ScriptExecutionListener listener) {
        return mScriptExecutionObserver.removeScriptExecutionListener(listener);
    }

    public ScriptExecution execute(ScriptExecutionTask task) {
        if (task.getListener() != null) {
            task.setExecutionListener(new ScriptExecutionObserver.Wrapper(mScriptExecutionObserver, task.getListener()));
        } else {
            task.setExecutionListener(mScriptExecutionObserver);
        }
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

    public ScriptExecution execute(ScriptSource source, ExecutionConfig config) {
        return execute(new ScriptExecutionTask(source, null, config));
    }

    public ScriptExecution execute(ScriptSource source, ScriptExecutionListener listener) {
        return execute(source, listener, ExecutionConfig.getDefault());
    }

    public ScriptExecution execute(ScriptSource source) {
        return execute(source, null, ExecutionConfig.getDefault());
    }

    @Subscribe
    public void onScriptExecution(ScriptExecutionEvent event) {
        if (event.getCode() == ScriptExecutionEvent.ON_START) {
            mGlobalConsole.verbose(mContext.getString(R.string.text_start_running) + "[" + event.getMessage() + "]");
        } else if (event.getCode() == ScriptExecutionEvent.ON_EXCEPTION) {
            mUiHandler.toast(mContext.getString(R.string.text_error) + ": " + event.getMessage());
        }
    }

    public int stopAll() {
        return mScriptEngineManager.stopAll();
    }


    public void stopAllAndToast() {
        int n = stopAll();
        if (n > 0)
            mUiHandler.toast(String.format(mContext.getString(R.string.text_already_stop_n_scripts), n));
        else
            mUiHandler.toast(mContext.getString(R.string.text_no_running_script));
    }

    public String[] getGlobalFunctions() {
        return mScriptEngineManager.getGlobalFunctions();
    }

    public Set<ScriptEngine> getEngines() {
        return mScriptEngineManager.getEngines();
    }

    private static class EngineLifecycleObserver implements AbstractScriptEngineManager.EngineLifecycleCallback {

        private final Set<AbstractScriptEngineManager.EngineLifecycleCallback> mEngineLifecycleCallbacks = new LinkedHashSet<>();

        @Override
        public void onEngineCreate(ScriptEngine engine) {
            synchronized (mEngineLifecycleCallbacks) {
                for (AbstractScriptEngineManager.EngineLifecycleCallback callback : mEngineLifecycleCallbacks) {
                    callback.onEngineCreate(engine);
                }
            }
        }

        @Override
        public void onEngineRemove(ScriptEngine engine) {
            synchronized (mEngineLifecycleCallbacks) {
                for (AbstractScriptEngineManager.EngineLifecycleCallback callback : mEngineLifecycleCallbacks) {
                    callback.onEngineRemove(engine);
                }
            }
        }

        void registerCallback(AbstractScriptEngineManager.EngineLifecycleCallback callback) {
            synchronized (mEngineLifecycleCallbacks) {
                mEngineLifecycleCallbacks.add(callback);
            }

        }

        void unregisterCallback(AbstractScriptEngineManager.EngineLifecycleCallback callback) {
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
