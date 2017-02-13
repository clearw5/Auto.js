package com.stardust.scriptdroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.stardust.app.NotRemindAgainDialog;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.scriptdroid.tool.BackPressedHandler;
import com.stardust.scriptdroid.ui.ScriptFileOperation;
import com.stardust.scriptdroid.ui.ScriptListRecyclerView;
import com.stardust.scriptdroid.ui.SlideMenuFragment;
import com.stardust.scriptdroid.ui.SlidingUpPanel;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.io.File;


public class MainActivity extends BaseActivity implements FileChooserDialog.FileCallback {

    private SlidingUpPanel mAddFilePanel;
    private ScriptListRecyclerView mScriptListRecyclerView;
    private ScriptFileList mScriptFileList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
        checkPermissions();
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        goToAccessibilityPermissionSettingIfDisabled();
    }

    private void goToAccessibilityPermissionSettingIfDisabled() {
        if (!AccessibilityServiceUtils.isAccessibilityServiceEnabled(this, ActionPerformService.class)) {
            new NotRemindAgainDialog.Builder(this, "goToAccessibilityPermissionSettingIfDisabled")
                    .title(R.string.text_alert)
                    .content(R.string.explain_accessibility_permission)
                    .positiveText(R.string.text_go_to_setting)
                    .negativeText(R.string.text_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            AccessibilityServiceUtils.goToAccessibilitySetting(MainActivity.this);
                        }
                    }).show();
        }
    }

    private void addScriptFile(final String path) {
        new MaterialDialog.Builder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), new File(path).getName(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        MainActivity.this.addScriptFile(input.toString(), path);
                    }
                }).show();
    }

    private void addScriptFile(String name, String path) {
        mScriptFileList.add(new ScriptFile(name, path));
        mScriptListRecyclerView.getAdapter().notifyItemInserted(mScriptFileList.size() - 1);
    }


    private void setUpUI() {
        mDrawerLayout = (DrawerLayout) View.inflate(this, R.layout.activity_main, null);
        setContentView(mDrawerLayout);
        SlideMenuFragment.setFragment(this, R.id.fragment_slide_menu);
        mAddFilePanel = $(R.id.bottom_menu);

        setUpToolbar();
        setUpScriptList();
        ViewBinder.bind(this);

    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.text_drawer_open,
                R.string.text_drawer_close);
        drawerToggle.syncState();
        mDrawerLayout.addDrawerListener(drawerToggle);
    }

    private void setUpScriptList() {
        mScriptListRecyclerView = $(R.id.script_list);
        mScriptFileList = SharedPrefScriptFileList.getInstance();
        mScriptListRecyclerView.setScriptFileList(mScriptFileList);
    }

    @ViewBinding.Click(R.id.fab)
    private void showAddFilePanel() {
        mAddFilePanel.show();
    }

    @ViewBinding.Click(R.id.create_new_file)
    private void createScriptFile() {
        new MaterialDialog.Builder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String path = ScriptFile.DEFAULT_FOLDER + input + ".js";
                        MainActivity.this.createScriptFile(input.toString(), path);
                    }
                }).show();
    }

    private void createScriptFile(String name, String path) {
        if (FileUtils.createFileIfNotExists(path)) {
            addScriptFile(name, path);
            new ScriptFileOperation.Edit().operate(mScriptListRecyclerView, mScriptFileList, mScriptFileList.size() - 1);
        } else {
            Snackbar.make(mDrawerLayout, R.string.text_file_create_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    @ViewBinding.Click(R.id.import_from_file)
    private void showFileChooser() {
        new FileChooserDialog.Builder(this)
                .extensionsFilter(".js", ".txt")
                .show();
    }

    @ViewBinding.Click(R.id.setting)
    private void startSettingActivity() {
        startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ViewBinding.Click(R.id.exit)
    public void finish() {
        super.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NonUiInitializer.getInstance().copySampleScriptFileIfNeeded();
            mScriptListRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        addScriptFile(file.getPath());
    }


    private BackPressedHandler mBackPressedHandler = new BackPressedHandler.DoublePressExit(this);
    @Override
    public void onBackPressed() {
        mBackPressedHandler.onBackPressed();
    }
}