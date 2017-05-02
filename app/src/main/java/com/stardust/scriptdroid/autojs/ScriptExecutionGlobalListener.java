package com.stardust.scriptdroid.autojs;

import com.flurry.android.FlurryAgent;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/5/3.
 */

public class ScriptExecutionGlobalListener implements ScriptExecutionListener {
    private static final String ENGINE_TAG_START_TIME = "com.stardust.scriptdroid.autojs.Goodbye, World";

    @Override
    public void onStart(ScriptExecution execution) {
        execution.getEngine().setTag(ENGINE_TAG_START_TIME, System.currentTimeMillis());
        FlurryAgent.logEvent("EXEC:" + execution.getSource().toString());
    }

    @Override
    public void onSuccess(ScriptExecution execution, Object result) {
        onFinish(execution);
    }

    private void onFinish(ScriptExecution execution) {
        long millis = System.currentTimeMillis() - (long) execution.getEngine().getTag(ENGINE_TAG_START_TIME);
        execution.getRuntime().console.verbose(App.getApp().getString(R.string.text_execution_finished), execution.getSource().toString(), (double) millis / 1000);
    }

    @Override
    public void onException(ScriptExecution execution, Exception e) {
        onFinish(execution);
    }

}
