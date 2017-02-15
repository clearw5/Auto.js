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

    private static final String EXTRA_SCRIPT = "EXTRA_SCRIPT";
    private static final String EXTRA_ON_RUN_FINISHED_LISTENER = "EXTRA_ON_RUN_FINISHED_LISTENER";
    private Object mResult;
    private String mScript;
    private Droid.OnRunFinishedListener mOnRunFinishedListener;


    public static void runScript(String script, Droid.OnRunFinishedListener listener, RunningConfig config) {
        App.getApp().startActivity(new Intent(App.getApp(), ScriptExecuteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_SCRIPT, script)
                .putExtra(EXTRA_ON_RUN_FINISHED_LISTENER, listener));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        runScript();
    }

    private void handleIntent(Intent intent) {
        mScript = intent.getStringExtra(EXTRA_SCRIPT);
        mOnRunFinishedListener = (Droid.OnRunFinishedListener) intent.getSerializableExtra(EXTRA_ON_RUN_FINISHED_LISTENER);
    }

    private void runScript() {
        JAVA_SCRIPT_ENGINE.set("activity", Activity.class, this);
        try {
            mResult = JAVA_SCRIPT_ENGINE.execute(mScript);
        } catch (Exception e) {
            mOnRunFinishedListener.onRunFinished(null, e);
            finish();
        }
    }

    @Override
    public void finish() {
        mOnRunFinishedListener.onRunFinished(mResult, null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JAVA_SCRIPT_ENGINE.set("activity", Activity.class, null);
        JAVA_SCRIPT_ENGINE.removeAndDestroy(Thread.currentThread());
    }
}
