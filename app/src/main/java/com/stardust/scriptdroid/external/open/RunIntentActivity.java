package com.stardust.scriptdroid.external.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stardust.scriptdroid.script.PathChecker;
import com.stardust.autojs.script.FileScriptSource;
import com.stardust.autojs.script.SequenceScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.Scripts;

import java.io.File;

/**
 * Created by Stardust on 2017/2/22.
 */

public class RunIntentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            handleIntent(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.edit_and_run_handle_intent_error, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void handleIntent(Intent intent) {
        String path = getPath(intent);
        String directoryPath = null;
        String script = intent.getStringExtra(CommonUtils.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
        ScriptSource source = null;
        if (path == null && script != null) {
            source = new StringScriptSource(script);
        } else if (path != null && new PathChecker(this).checkAndToastError(path)) {
            ScriptSource fileScriptSource = new FileScriptSource(path);
            if (script != null) {
                source = new SequenceScriptSource(fileScriptSource.getName(), new StringScriptSource(script), fileScriptSource);
            } else {
                source = fileScriptSource;
            }
            directoryPath = new File(path).getParent();
        }
        if (source != null) {
            if (directoryPath == null)
                Scripts.run(source);
            else
                Scripts.run(source, directoryPath);
        }
    }

    private String getPath(Intent intent) {
        if (intent.getData() != null && intent.getData().getPath() != null)
            return intent.getData().getPath();
        return intent.getStringExtra(CommonUtils.EXTRA_KEY_PATH);
    }
}
