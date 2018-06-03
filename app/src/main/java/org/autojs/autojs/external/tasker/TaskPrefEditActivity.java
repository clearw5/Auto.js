package org.autojs.autojs.external.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.autojs.autojs.R;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.storage.file.StorageFileProvider;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.main.scripts.ScriptListView;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import static org.autojs.autojs.ui.edit.EditorView.EXTRA_CONTENT;


/**
 * Created by Stardust on 2017/3/27.
 */
@EActivity(R.layout.activity_tasker_edit)
public class TaskPrefEditActivity extends AbstractAppCompatPluginActivity {

    private String mSelectedScriptFilePath;
    private String mPreExecuteScript;
    private StorageFileProvider mStorageFileProvider;

    @AfterViews
    void setUpViews() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, getString(R.string.text_please_choose_a_script));
        initScriptListRecyclerView();
    }


    private void initScriptListRecyclerView() {
        mStorageFileProvider = StorageFileProvider.getExternalStorageProvider();
        ScriptListView scriptList = (ScriptListView) findViewById(R.id.script_list);
        scriptList.setStorageFileProvider(mStorageFileProvider, new ScriptFile(StorageFileProvider.getDefaultDirectory()));
        scriptList.setOnScriptFileClickListener((view, file) -> {
            mSelectedScriptFilePath = file.getPath();
            finish();
        });
    }


    @Click(R.id.edit_script)
    void editPreExecuteScript() {
        TaskerScriptEditActivity.edit(this, getString(R.string.text_pre_execute_script), getString(R.string.summary_pre_execute_script), mPreExecuteScript == null ? "" : mPreExecuteScript);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mStorageFileProvider.refreshAll();
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
        return ScriptIntents.isTaskerBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String s) {
        mSelectedScriptFilePath = bundle.getString(ScriptIntents.EXTRA_KEY_PATH);
        mPreExecuteScript = bundle.getString(ScriptIntents.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(ScriptIntents.EXTRA_KEY_PATH, mSelectedScriptFilePath);
        bundle.putString(ScriptIntents.EXTRA_KEY_PRE_EXECUTE_SCRIPT, mPreExecuteScript);
        return bundle;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        String blurb = bundle.getString(ScriptIntents.EXTRA_KEY_PATH);
        if (TextUtils.isEmpty(blurb)) {
            blurb = bundle.getString(ScriptIntents.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
        }
        if (TextUtils.isEmpty(blurb)) {
            blurb = getString(R.string.text_path_is_empty);
        }
        return blurb;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mPreExecuteScript = data.getStringExtra(EXTRA_CONTENT);
        }
    }
}
