package com.stardust.scriptdroid.external.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/2/2.
 */

public class ImportIntentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            handleIntent(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.edit_and_run_handle_intent_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path))
            MainActivity.importScriptFile(this, path);
        finish();
    }

    private void startMainActivity() {

        finish();
    }
}