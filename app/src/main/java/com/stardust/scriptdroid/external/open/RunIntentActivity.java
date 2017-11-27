package com.stardust.scriptdroid.external.open;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFiles;
import com.stardust.scriptdroid.external.ScriptIntents;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.Scripts;

import java.io.FileNotFoundException;
import java.io.InputStream;

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

    private void handleIntent(Intent intent) throws FileNotFoundException {
        Uri uri = intent.getData();
        if (uri != null && "content".equals(uri.getScheme())) {
            InputStream stream = getContentResolver().openInputStream(uri);
            Scripts.run(new StringScriptSource(PFiles.read(stream)));
        } else {
            ScriptIntents.handleIntent(this, intent);
        }
    }
}
