package com.stardust.scriptdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.stardust.scriptdroid.BaseActivity;
import com.stardust.scriptdroid.EditActivity;

/**
 * Created by Stardust on 2017/2/2.
 */

public class EditAndRunIntentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path))
            EditActivity.editFile(this, path);
    }
}
