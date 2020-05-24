package org.autojs.autojs.timing;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.autojs.autojs.App;
import org.autojs.autojs.external.ScriptIntents;

/**
 * Created by Stardust on 2017/11/27.
 */

public class TaskReceiver extends BroadcastReceiver {

    public static final String ACTION_TASK = "com.stardust.autojs.action.task";
    public static final String EXTRA_TASK_ID = "task_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        ScriptIntents.handleIntent(context, intent);
        long id = intent.getLongExtra(EXTRA_TASK_ID, -1);
        if (id >= 0) {
            TimedTaskManager.getInstance().notifyTaskFinished(id);
        }
    }
}
