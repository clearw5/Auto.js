package com.stardust.scriptdroid.external.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;

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
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)) {
            startActivity(new Intent(this, ShortcutActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("path", path));
        }
    }
}
