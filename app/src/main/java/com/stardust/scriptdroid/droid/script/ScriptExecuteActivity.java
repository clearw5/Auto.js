package com.stardust.scriptdroid.droid.script;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.RunningConfig;

import static com.stardust.scriptdroid.droid.Droid.JAVA_SCRIPT_ENGINE;

/**
 * Created by Stardust on 2017/2/5.
 */

public class ScriptExecuteActivity extends Activity {

    private static String script;
    private static Droid.OnRunFinishedListener onRunFinishedListener;
    private static RunningConfig runningConfig;
    private Object mResult;


    public static void runScript(String script, Droid.OnRunFinishedListener listener, RunningConfig config) {
        ScriptExecuteActivity.script = script;
        ScriptExecuteActivity.onRunFinishedListener = listener;
        ScriptExecuteActivity.runningConfig = config;
        App.getApp().startActivity(new Intent(App.getApp(), ScriptExecuteActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runScript();
    }

    private void runScript() {
        JAVA_SCRIPT_ENGINE.set("activity", Activity.class, this);
        try {
            mResult = JAVA_SCRIPT_ENGINE.execute(script);
        } catch (Exception e) {
            onRunFinishedListener.onRunFinished(null, e);
            finish();
        }
    }

    @Override
    public void finish() {
        onRunFinishedListener.onRunFinished(mResult, null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JAVA_SCRIPT_ENGINE.removeAndDestroy(Thread.currentThread());
    }
}
