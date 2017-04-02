package com.stardust.scriptdroid.external.shortcut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.scripts.PathChecker;
import com.stardust.autojs.script.FileScriptSource;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.external.CommonUtils;

/**
 * Created by Stardust on 2017/1/23.
 */
public class ShortcutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String path = getIntent().getStringExtra(CommonUtils.EXTRA_KEY_PATH);
        if (new PathChecker(this).checkAndToastError(path)) {
            runScriptFile(path);
        }
    }

    public void onStart() {
        super.onStart();
        finish();
    }

    private void runScriptFile(String path) {
        try {
            AutoJs.getInstance().getScriptEngineService().execute(new FileScriptSource(path));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
