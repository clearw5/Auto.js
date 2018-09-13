package org.autojs.autojs.external.boot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stardust.autojs.execution.ExecutionConfig;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.timing.IntentTask;
import org.autojs.autojs.timing.TimedTaskManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "BootCompleteReceiver";

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(LOG_TAG, "on boot complete");
            TimedTaskManager.getInstance().getIntentTaskOfAction(intent.getAction())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(intentTask -> runTask(context, intent, intentTask), Throwable::printStackTrace);
        }
    }

    private void runTask(Context context, Intent intent, IntentTask task) {
        ScriptFile file = new ScriptFile(task.getScriptPath());
        ExecutionConfig config = new ExecutionConfig();
        config.setArgument("intent", intent.clone());
        config.executePath(file.getParent());
        AutoJs.getInstance().getScriptEngineService().execute(file.toSource(), config);
    }
}
