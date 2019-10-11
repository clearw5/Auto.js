package com.stardust.autojs.execution;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/5/3.
 */

public class ScriptExecutionObserver implements ScriptExecutionListener {
    private CopyOnWriteArraySet<ScriptExecutionListener> mScriptExecutionListeners = new CopyOnWriteArraySet<>();

    @Override
    public void onStart(ScriptExecution execution) {
        for (ScriptExecutionListener listener : mScriptExecutionListeners) {
            listener.onStart(execution);
        }
    }

    @Override
    public void onSuccess(ScriptExecution execution, Object result) {
        for (ScriptExecutionListener listener : mScriptExecutionListeners) {
            listener.onSuccess(execution, result);
        }
    }

    @Override
    public void onException(ScriptExecution execution, Throwable e) {
        for (ScriptExecutionListener listener : mScriptExecutionListeners) {
            listener.onException(execution, e);
        }
    }

    public boolean registerScriptExecutionListener(ScriptExecutionListener listener) {
        return mScriptExecutionListeners.add(listener);
    }

    public boolean removeScriptExecutionListener(ScriptExecutionListener listener) {
        return mScriptExecutionListeners.remove(listener);
    }

    public static class Wrapper implements ScriptExecutionListener {

        private final ScriptExecutionObserver mScriptExecutionObserver;
        private final ScriptExecutionListener mScriptExecutionListener;

        public Wrapper(ScriptExecutionObserver scriptExecutionObserver, ScriptExecutionListener scriptExecutionListener) {
            mScriptExecutionObserver = scriptExecutionObserver;
            mScriptExecutionListener = scriptExecutionListener;
        }

        @Override
        public void onStart(ScriptExecution execution) {
            mScriptExecutionListener.onStart(execution);
            mScriptExecutionObserver.onStart(execution);
        }

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            mScriptExecutionListener.onSuccess(execution, result);
            mScriptExecutionObserver.onSuccess(execution, result);
        }

        @Override
        public void onException(ScriptExecution execution, Throwable e) {
            mScriptExecutionListener.onException(execution, e);
            mScriptExecutionObserver.onException(execution, e);
        }
    }

}
