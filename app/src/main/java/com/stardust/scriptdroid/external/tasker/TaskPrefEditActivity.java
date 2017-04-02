package com.stardust.scriptdroid.external.tasker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.ui.main.my_script_list.ScriptAndFolderListRecyclerView;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity;

/**
 * Created by Stardust on 2017/3/27.
 */

public class TaskPrefEditActivity extends AbstractPluginActivity {

    private static final String TAG = "TaskPrefEditActivity";
    private String mSelectedScriptFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_tasker_edit);
        initScriptListRecyclerView();
    }

    private void initScriptListRecyclerView() {
        ScriptAndFolderListRecyclerView scriptListRecyclerView = (ScriptAndFolderListRecyclerView) findViewById(R.id.script_list);
        scriptListRecyclerView.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {
            @Override
            public void onClick(ScriptFile file) {
                mSelectedScriptFilePath = file.getPath();
                finish();
            }
        });
    }


    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        boolean valid = bundle.getString(CommonUtils.EXTRA_KEY_PATH) != null;
        Log.v(TAG, "isBundleValid: " + valid);
        Context context;
        return valid;
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String s) {
        Log.v(TAG, "onPostCreateWithPreviousResult: bundle=" + bundle + " str=" + s);
        mSelectedScriptFilePath = bundle.getString(CommonUtils.EXTRA_KEY_PATH);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        Log.v(TAG, "getResultBundle");
        Bundle bundle = new Bundle();
        bundle.putString(CommonUtils.EXTRA_KEY_PATH, mSelectedScriptFilePath);
        return bundle;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        Log.v(TAG, "getResultBlurb");
        return bundle.getString(CommonUtils.EXTRA_KEY_PATH, getString(R.string.text_path_is_empty));
    }
}
