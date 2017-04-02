package com.stardust.scriptdroid.external.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.PathChecker;
import com.stardust.scriptdroid.droid.RunningConfig;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.R;

import java.io.File;

/**
 * Created by Stardust on 2017/2/22.
 */

public class RunIntentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            handleIntent();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.edit_and_run_handle_intent_error, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String path = getPath(intent);
        String script = intent.getStringExtra(CommonUtils.EXTRA_KEY_PREPARE_SCRIPT);
        if (path == null && script != null) {
            Droid.getInstance().runScript(script);
        } else {
            if (new PathChecker(this).checkAndToastError(path)) {
                Droid.getInstance().runScriptFile(new File(path), null, new RunningConfig().prepareScript(script));
            }
        }
    }

    private String getPath(Intent intent) {
        if (intent.getData() != null && intent.getData().getPath() != null)
            return intent.getData().getPath();
        return intent.getStringExtra(CommonUtils.EXTRA_KEY_PATH);
    }
}
