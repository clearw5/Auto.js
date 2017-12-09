package com.stardust.scriptdroid.external.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.storage.file.StorageFileProvider;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.scripts.ScriptListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Stardust on 2017/7/11.
 */
@EActivity(R.layout.activity_script_widget_settings)
public class ScriptWidgetSettingsActivity extends BaseActivity {


    private String mSelectedScriptFilePath;
    private StorageFileProvider mStorageFileProvider;
    private int mAppWidgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @AfterViews
    void setUpViews() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, getString(R.string.text_please_choose_a_script));
        initScriptListRecyclerView();
    }


    private void initScriptListRecyclerView() {
        mStorageFileProvider = StorageFileProvider.getExternalStorageProvider();
        ScriptListView scriptList = (ScriptListView) findViewById(R.id.script_list);
        scriptList.setStorageFileProvider(mStorageFileProvider);
        scriptList.setCurrentDirectory(new ScriptFile(StorageFileProvider.DEFAULT_DIRECTORY));
        scriptList.setOnScriptFileClickListener((view, file) -> {
            mSelectedScriptFilePath = file.getPath();
            finish();
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mStorageFileProvider.refreshAll();
        } else if (item.getItemId() == R.id.action_clear_file_selection) {
            mSelectedScriptFilePath = null;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.script_widget_settings_menu, menu);
        return true;
    }

    @Override
    public void finish() {
        if (ScriptWidget.updateWidget(this, mAppWidgetId, mSelectedScriptFilePath)) {
            setResult(RESULT_OK, new Intent()
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));

        } else {
            setResult(RESULT_CANCELED, new Intent()
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        }
        super.finish();
    }


}
