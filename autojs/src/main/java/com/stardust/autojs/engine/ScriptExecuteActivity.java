package com.stardust.autojs.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/2/5.
 */

public class ScriptExecuteActivity extends Activity {

    private static final String EXTRA_TASK = "EXTRA_TASK";
    private Object mResult;
    private JavaScriptEngine mJavaScriptEngine;
    private ScriptExecutionListener mExecutionListener;
    private ScriptSource mScriptSource;


    public static void execute(Context context, ScriptExecutionTask task) {
        context.startActivity(new Intent(context, ScriptExecuteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_TASK, task));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        mJavaScriptEngine = ScriptEngineService.getInstance().createScriptEngine();
        runScript();
    }

    private void handleIntent(Intent intent) {
        ScriptExecutionTask scriptExecutionTask = (ScriptExecutionTask) intent.getSerializableExtra(EXTRA_TASK);
        mExecutionListener = scriptExecutionTask.getExecutionListener();
        mScriptSource = scriptExecutionTask.getScriptSource();
    }

    private void runScript() {
        mJavaScriptEngine.put("activity", this);
        try {
            mResult = mJavaScriptEngine.execute(mScriptSource);
        } catch (Exception e) {
            mExecutionListener.onException(mJavaScriptEngine, mScriptSource, e);
            super.finish();
        }
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
        mJavaScriptEngine.stop();
    }

}
