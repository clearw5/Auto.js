package com.stardust.autojs.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.autojs.ExecutionConfig;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.ScriptExecution;
import com.stardust.autojs.ScriptExecutionListener;
import com.stardust.autojs.ScriptExecutionTask;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/2/5.
 */

public class ScriptExecuteActivity extends Activity {


    private static ActivityScriptExecution execution;
    private Object mResult;
    private JavaScriptEngine mJavaScriptEngine;
    private ScriptExecutionListener mExecutionListener;
    private ScriptSource mScriptSource;

    public static ActivityScriptExecution execute(Context context, ScriptEngineService service, ScriptExecutionTask task) {
        if (execution != null) {
            return null;
        }
        execution = new ActivityScriptExecution(service, task);
        context.startActivity(new Intent(context, ScriptExecuteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        return execution;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScriptSource = execution.getSource();
        mJavaScriptEngine = execution.getEngine();
        mExecutionListener = execution.getListener();
        runScript();
    }

    private void runScript() {
        try {
            prepare();
            doExecution();
        } catch (Exception e) {
            mExecutionListener.onException(mJavaScriptEngine, mScriptSource, e);
            super.finish();
        }
    }

    private void doExecution() {
        mJavaScriptEngine.setTag("script", mScriptSource);
        mExecutionListener.onStart(mJavaScriptEngine, mScriptSource);
        mResult = mJavaScriptEngine.execute(mScriptSource);
    }

    private void prepare() {
        mJavaScriptEngine.put("activity", this);
        mJavaScriptEngine.put("__runtime__", execution.getRuntime());
        mJavaScriptEngine.init();
    }

    @Override
    public void finish() {
        mExecutionListener.onSuccess(mJavaScriptEngine, mScriptSource, mResult);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJavaScriptEngine.put("activity", null);
        mJavaScriptEngine.destroy();
        execution = null;
    }

    private static class ActivityScriptExecution extends ScriptExecution.AbstarctScriptExecution {

        private JavaScriptEngine mJavaScriptEngine;
        private ScriptRuntime mScriptRuntime;

        ActivityScriptExecution(ScriptEngineService service, ScriptExecutionTask task) {
            super(task);
            mJavaScriptEngine = service.createScriptEngine();
            mScriptRuntime = service.createScriptRuntime();
        }

        @Override
        public JavaScriptEngine getEngine() {
            return mJavaScriptEngine;
        }

        @Override
        public ScriptRuntime getRuntime() {
            return mScriptRuntime;
        }

    }


}
