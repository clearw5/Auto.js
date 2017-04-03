package com.stardust.scriptdroid.external.tasker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.ui.main.script_list.ScriptAndFolderListRecyclerView;
import com.stardust.scriptdroid.ui.main.script_list.ScriptListWithProgressBarView;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity;

/**
 * Created by Stardust on 2017/3/27.
 */

public class TaskPrefEditActivity extends AbstractPluginActivity {

    private String mSelectedScriptFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasker_edit);
        initScriptListRecyclerView();
    }

    private void initScriptListRecyclerView() {
        ScriptListWithProgressBarView scriptListWithProgressBarView = (ScriptListWithProgressBarView) findViewById(R.id.script_list);
        scriptListWithProgressBarView.setScriptFileOperationEnabled(false);
        scriptListWithProgressBarView.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {
            @Override
            public void onClick(ScriptFile file) {
                mSelectedScriptFilePath = file.getPath();
                finish();
            }
        });
    }


    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return CommonUtils.isTaskerBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String s) {
        mSelectedScriptFilePath = bundle.getString(CommonUtils.EXTRA_KEY_PATH);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(CommonUtils.EXTRA_KEY_PATH, mSelectedScriptFilePath);
        return bundle;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        return bundle.getString(CommonUtils.EXTRA_KEY_PATH, getString(R.string.text_path_is_empty));
    }
}
