package org.autojs.autojs.timing;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.external.ScriptIntents;

/**
 * Created by Stardust on 2017/11/27.
 */

public class TaskReceiver extends BroadcastReceiver {

    public static final String ACTION_TASK = "org.autojs.autojs.action.task";
    public static final String EXTRA_TASK_ID = "task_id";
    private static final String LOG_TAG = "TaskReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "receive intent:" + intent.getAction());
        Log.d(LOG_TAG, "taskInfo [id=" + intent.getLongExtra(TaskReceiver.EXTRA_TASK_ID, -1)
                + ", path=" + intent.getStringExtra(ScriptIntents.EXTRA_KEY_PATH)
                + "]");
        AutoJs.getInstance().debugInfo("receive intent:" + intent.getAction());
        ScriptIntents.handleIntent(context, intent);
        long id = intent.getLongExtra(EXTRA_TASK_ID, -1);
        if (id >= 0) {
            TimedTaskManager.getInstance().notifyTaskFinished(id);
        }
    }
}
