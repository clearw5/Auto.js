package com.stardust.scriptdroid.external.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.ui.main.MainActivity;

import java.io.File;

/**
 * Created by Stardust on 2017/2/2.
 */

public class ImportIntentActivity extends BaseActivity {

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
        final String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)) {
            new MaterialDialog.Builder(this)
                    .title(R.string.text_please_input_name)
                    .input(getString(R.string.text_name), new File(path).getName(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            SharedPrefScriptFileList.getInstance().add(new ScriptFile(input.toString(), path));
                            startMainActivity();
                        }
                    })
                    .show();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}