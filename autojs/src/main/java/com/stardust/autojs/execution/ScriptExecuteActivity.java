package com.stardust.autojs.execution;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.IntentExtras;

/**
 * Created by Stardust on 2017/2/5.
 */

public class ScriptExecuteActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler {


    private static final String EXTRA_EXECUTION = ScriptExecuteActivity.class.getName() + ".execution";
    private Object mResult;
    private ScriptEngine mScriptEngine;
    private ScriptExecutionListener mExecutionListener;
    private ScriptSource mScriptSource;
    private ScriptExecution mScriptExecution;

    public static ActivityScriptExecution execute(Context context, ScriptEngineManager manager, ScriptExecutionTask task) {
        ActivityScriptExecution execution = new ActivityScriptExecution(manager, task);
        Intent i = new Intent(context, ScriptExecuteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put(EXTRA_EXECUTION, execution)
                .putInIntent(i);
        context.startActivity(i);
        return execution;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentExtras extras = IntentExtras.fromIntent(getIntent());
        if (extras.get(EXTRA_EXECUTION) == null) {
            finish();
            return;
        }
        mScriptExecution = extras.get(EXTRA_EXECUTION);
        mScriptSource = mScriptExecution.getSource();
        mScriptEngine = mScriptExecution.getEngine();
        mExecutionListener = mScriptExecution.getListener();
        ((RhinoJavaScriptEngine) mScriptEngine).setUncaughtExceptionHandler((t, e) -> onException((Exception) e));
        runScript();
    }

    private void runScript() {
        try {
            prepare();
            doExecution();
        } catch (Exception e) {
            onException(e);
        }
    }

    private void onException(Exception e) {
        mExecutionListener.onException(mScriptExecution, e);
        super.finish();
    }

    @SuppressWarnings("unchecked")
    private void doExecution() {
        mScriptEngine.setTag(ScriptEngine.TAG_SOURCE, mScriptSource);
        mExecutionListener.onStart(mScriptExecution);
        ((LoopBasedJavaScriptEngine) mScriptEngine).execute(mScriptSource, new LoopBasedJavaScriptEngine.ExecuteCallback() {
            @Override
            public void onResult(Object r) {
                mResult = r;
            }

            @Override
            public void onException(Exception e) {
                ScriptExecuteActivity.this.onException(e);
            }
        });
    }

    private void prepare() {
        mScriptEngine.put("activity", this);
        mScriptEngine.setTag(ScriptEngine.TAG_ENV_PATH, mScriptExecution.getConfig().getRequirePath());
        mScriptEngine.setTag(ScriptEngine.TAG_EXECUTE_PATH, mScriptExecution.getConfig().getExecutePath());
        mScriptEngine.init();
    }

    @Override
    public void finish() {
        mExecutionListener.onSuccess(mScriptExecution, mResult);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScriptEngine.put("activity", null);
        mScriptEngine.destroy();
        mScriptExecution = null;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        onException((Exception) e);
    }

    private static class ActivityScriptExecution extends ScriptExecution.AbstractScriptExecution {

        private ScriptEngine mScriptEngine;
        private ScriptEngineManager mScriptEngineManager;

        ActivityScriptExecution(ScriptEngineManager manager, ScriptExecutionTask task) {
            super(task);
            mScriptEngineManager = manager;
        }


        @Override
        public ScriptEngine getEngine() {
            if (mScriptEngine == null) {
                mScriptEngine = mScriptEngineManager.createEngineOfSourceOrThrow(getSource());
            }
            return mScriptEngine;
        }

    }


}
