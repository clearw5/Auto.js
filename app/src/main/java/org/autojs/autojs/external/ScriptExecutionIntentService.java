package org.autojs.autojs.external;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Stardust on 2017/5/15.
 */

public class ScriptExecutionIntentService extends IntentService {

    public ScriptExecutionIntentService() {
        super("ScriptExecutionIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        ScriptIntents.handleIntent(this, intent);
    }
}
