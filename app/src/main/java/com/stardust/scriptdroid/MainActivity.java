package com.stardust.scriptdroid;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.file.FileChooser;
import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.scriptdroid.ui.ScriptFileOperation;
import com.stardust.scriptdroid.ui.ScriptListRecyclerView;
import com.stardust.scriptdroid.ui.SlidingUpPanel;
import com.stardust.util.MapEntries;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.util.Map;
import java.util.Optional;


public class MainActivity extends BaseActivity {

    private View mView;
    private SlidingUpPanel mSlidingUpPanel;
    private ScriptListRecyclerView mScriptListRecyclerView;
    private ScriptFileList mScriptFileList;
    private FileChooser mFileChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpUI();

        setUpFileChooser();

        checkPermissions();

    }

    public void onStart() {
        super.onStart();
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        goToAccessibilityPermissionSettingIfDisabled();
    }

    private void goToAccessibilityPermissionSettingIfDisabled() {
        if (!AccessibilityServiceUtils.isAccessibilityServiceEnabled(this, ActionPerformService.class)) {
            new MaterialDialog.Builder(this)
                    .content(R.string.explain_accessibility_permission)
                    .positiveText(R.string.text_go_to_setting)
                    .negativeText(R.string.text_cancel)
                    .onPositive((dialog, which) -> AccessibilityServiceUtils.goToPermissionSetting(this)).show();
        }
    }

    private void setUpFileChooser() {
        mFileChooser = new FileChooser(this);
        mFileChooser.setOnFileChoseListener(inputStream -> Optional.ofNullable(FileUtils.getPath(inputStream)).ifPresent(this::addScriptFile));
    }

    private void addScriptFile(final String path) {
        new MaterialDialog.Builder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), "", (dialog, input) -> addScriptFile(input.toString(), path)).show();
    }

    private void addScriptFile(String name, String path) {
        mScriptFileList.add(new ScriptFile(name, path));
        mScriptListRecyclerView.getAdapter().notifyItemInserted(mScriptFileList.size() - 1);
    }


    private void setUpUI() {
        mView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mView);
        mSlidingUpPanel = $(R.id.bottom_menu);

        setUpToolbar();
        setUpScriptList();
        setUpListener();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_script_droid_40);
        toolbar.setTitle(R.string.app_name);
    }

    private void setUpScriptList() {
        mScriptListRecyclerView = $(R.id.script_list);
        mScriptFileList = new SharedPrefScriptFileList(this);
        mScriptListRecyclerView.setScriptFileList(mScriptFileList);
    }

    private void setUpListener() {
        $(R.id.fab).setOnClickListener(view -> mSlidingUpPanel.show());
        $(R.id.import_from_file).setOnClickListener(v -> showFileChooser());
        $(R.id.create_new_file).setOnClickListener(v -> createScriptFile());
    }

    private void createScriptFile() {
        new MaterialDialog.Builder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), "", (dialog, input) -> {
                    String path = ScriptFile.DEFAULT_FOLDER + input + ".js";
                    createScriptFile(input.toString(), path);
                }).show();
    }

    private void createScriptFile(String name, String path) {
        if (FileUtils.createFileIfNotExists(path)) {
            addScriptFile(name, path);
            new ScriptFileOperation.Edit().operate(mScriptListRecyclerView, mScriptFileList, mScriptFileList.size() - 1);
        } else {
            Snackbar.make(mView, R.string.text_file_create_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showFileChooser() {
        mFileChooser.startFileManagerToChoose("*/*", (exception, mimeType) -> {
            exception.printStackTrace();
            Snackbar.make(mView, R.string.text_file_manager_not_found, Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private final Map<Integer, Runnable> mOptionActionMap = new MapEntries<Integer, Runnable>()
            .entry(R.id.action_exit, this::finish)
            .entry(R.id.action_disable_service, this::disableAccessibilityService)
            .map();


    private void startSettingActivity() {
        //TODO create Setting Activity
        startActivity(new Intent(this, MainActivity.class));
    }

    private void disableAccessibilityService() {
        Optional.ofNullable(ActionPerformService.getInstance()).ifPresent(ActionPerformService::disableSelf);
        Snackbar.make(mView, R.string.text_service_disabled, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Runnable action = mOptionActionMap.get(item.getItemId());
        if (action != null) {
            action.run();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFileChooser.onActivityResult(requestCode, resultCode, data);
    }
}