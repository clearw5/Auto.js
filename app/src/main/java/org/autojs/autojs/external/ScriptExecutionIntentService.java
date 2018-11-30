package org.autojs.autojs.external;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

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
        try {
            ScriptIntents.handleIntent(this, intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
