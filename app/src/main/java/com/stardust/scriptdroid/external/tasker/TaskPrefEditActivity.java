package com.stardust.scriptdroid.external.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.scripts.StorageScriptProvider;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.script_list.ScriptAndFolderListRecyclerView;
import com.stardust.scriptdroid.ui.main.script_list.ScriptListWithProgressBarView;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;

/**
 * Created by Stardust on 2017/3/27.
 */

public class TaskPrefEditActivity extends AbstractAppCompatPluginActivity {

    private String mSelectedScriptFilePath;
    private String mPreExecuteScript;
    private StorageScriptProvider mStorageScriptProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasker_edit);
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, getString(R.string.text_please_choose_a_script));
        initScriptListRecyclerView();
        ViewBinder.bind(this);
    }

    private void initScriptListRecyclerView() {
        mStorageScriptProvider = StorageScriptProvider.getExternalStorageProvider();
        ScriptListWithProgressBarView scriptList = (ScriptListWithProgressBarView) findViewById(R.id.script_list);
        scriptList.setScriptFileOperationEnabled(false);
        scriptList.setStorageScriptProvider(mStorageScriptProvider);
        scriptList.setCurrentDirectory(StorageScriptProvider.DEFAULT_DIRECTORY);
        scriptList.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {
            @Override
            public void onClick(ScriptFile file, int position) {
                mSelectedScriptFilePath = file.getPath();
                finish();
            }
        });
    }


    @ViewBinding.Click(R.id.edit_script)
    private void editPreExecuteScript() {
        TaskerScriptEditActivity.edit(this, getString(R.string.text_pre_execute_script), getString(R.string.summary_pre_execute_script), mPreExecuteScript == null ? "" : mPreExecuteScript);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mStorageScriptProvider.refreshAll();
        } else if (item.getItemId() == R.id.action_clear_file_selection) {
            mSelectedScriptFilePath = null;
        } else {
            mPreExecuteScript = null;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasker_script_edit_menu, menu);
        return true;
    }


    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return CommonUtils.isTaskerBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String s) {
        mSelectedScriptFilePath = bundle.getString(CommonUtils.EXTRA_KEY_PATH);
        mPreExecuteScript = bundle.getString(CommonUtils.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(CommonUtils.EXTRA_KEY_PATH, mSelectedScriptFilePath);
        bundle.putString(CommonUtils.EXTRA_KEY_PRE_EXECUTE_SCRIPT, mPreExecuteScript);
        return bundle;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        String blurb = bundle.getString(CommonUtils.EXTRA_KEY_PATH);
        if (TextUtils.isEmpty(blurb)) {
            blurb = bundle.getString(CommonUtils.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
        }
        if (TextUtils.isEmpty(blurb)) {
            blurb = getString(R.string.text_path_is_empty);
        }
        return blurb;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mPreExecuteScript = data.getStringExtra(TaskerScriptEditActivity.EXTRA_CONTENT);
        }
    }
}
