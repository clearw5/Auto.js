package org.autojs.autojs.ui.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.stardust.autojs.project.ProjectConfig;
import com.stardust.autojs.runtime.api.Dialogs;
import com.stardust.pio.PFiles;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerFileItem;
import org.autojs.autojs.model.explorer.ExplorerItem;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.project.ProjectTemplate;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@EActivity(R.layout.activity_project_config)
public class ProjectConfigActivity extends BaseActivity {

    public static final String EXTRA_PARENT_DIRECTORY = "parent_directory";

    public static final String EXTRA_NEW_PROJECT = "new_project";

    public static final String EXTRA_DIRECTORY = "directory";


    @ViewById(R.id.project_location)
    EditText mProjectLocation;

    @ViewById(R.id.app_name)
    TextInputEditText mAppName;

    @ViewById(R.id.package_name)
    TextInputEditText mPackageName;

    @ViewById(R.id.version_name)
    TextInputEditText mVersionName;

    @ViewById(R.id.version_code)
    TextInputEditText mVersionCode;

    @ViewById(R.id.main_file_name)
    EditText mMainFileName;

    @ViewById(R.id.icon)
    ImageView mIcon;

    private File mDirectory;
    private File mParentDirectory;
    private ProjectConfig mProjectConfig;
    private boolean mNewProject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewProject = getIntent().getBooleanExtra(EXTRA_NEW_PROJECT, false);
        String parentDirectory = getIntent().getStringExtra(EXTRA_PARENT_DIRECTORY);
        if (mNewProject) {
            if (parentDirectory == null) {
                finish();
                return;
            }
            mParentDirectory = new File(parentDirectory);
            mProjectConfig = new ProjectConfig();
        } else {
            String dir = getIntent().getStringExtra(EXTRA_DIRECTORY);
            if (dir == null) {
                finish();
                return;
            }
            mDirectory = new File(dir);
            mProjectConfig = ProjectConfig.fromProjectDir(dir);
            if (mProjectConfig == null) {
                new ThemeColorMaterialDialogBuilder(this)
                        .title(R.string.text_invalid_project)
                        .positiveText(R.string.ok)
                        .dismissListener(dialogInterface -> finish())
                        .show();
            }
        }
    }

    @AfterViews
    void setupViews() {
        if (mProjectConfig == null) {
            return;
        }
        setToolbarAsBack(mNewProject ? getString(R.string.text_new_project) : mProjectConfig.getName());
        if (mNewProject) {
            mAppName.addTextChangedListener(new SimpleTextWatcher(s ->
                    mProjectLocation.setText(new File(mParentDirectory, s.toString()).getPath()))
            );
        } else {
            mAppName.setText(mProjectConfig.getName());
            mVersionCode.setText(String.valueOf(mProjectConfig.getVersionCode()));
            mPackageName.setText(mProjectConfig.getPackageName());
            mVersionName.setText(mProjectConfig.getVersionName());
            mMainFileName.setText(mProjectConfig.getMainScriptFile());
            mProjectLocation.setVisibility(View.GONE);
        }
    }

    @SuppressLint("CheckResult")
    @Click(R.id.fab)
    void commit() {
        syncProjectConfig();
        if (!checkInputs()) {
            return;
        }
        if (mNewProject) {
            String location = mProjectLocation.getText().toString();
            new ProjectTemplate(mProjectConfig, new File(location))
                    .newProject()
                    .subscribe(ignored -> {
                        Explorers.workspace().notifyChildrenChanged(new ExplorerDirPage(mParentDirectory, null));
                        finish();
                    }, e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Observable.fromCallable(() -> {
                PFiles.write(ProjectConfig.configFileOfDir(mDirectory.getPath()),
                        mProjectConfig.toJson());
                return Void.TYPE;
            })
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(ignored -> {
                        ExplorerFileItem item = new ExplorerFileItem(mDirectory, null);
                        Explorers.workspace().notifyItemChanged(item, item);
                        finish();
                    }, e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void syncProjectConfig() {
        mProjectConfig.setName(mAppName.getText().toString());
        mProjectConfig.setVersionCode(Integer.parseInt(mVersionCode.getText().toString()));
        mProjectConfig.setVersionName(mVersionName.getText().toString());
        mProjectConfig.setMainScriptFile(mMainFileName.getText().toString());
        mProjectConfig.setPackageName(mPackageName.getText().toString());
        //mProjectConfig.getLaunchConfig().setHideLogs(true);
    }

    private boolean checkInputs() {
        boolean inputValid = true;
        inputValid &= checkNotEmpty(mAppName);
        inputValid &= checkNotEmpty(mVersionCode);
        inputValid &= checkNotEmpty(mVersionName);
        inputValid &= checkNotEmpty(mPackageName);
        return inputValid;
    }

    private boolean checkNotEmpty(TextInputEditText editText) {
        if (!TextUtils.isEmpty(editText.getText()))
            return true;
        // TODO: 2017/12/8 more beautiful ways?
        String hint = ((TextInputLayout) editText.getParent().getParent()).getHint().toString();
        editText.setError(hint + getString(R.string.text_should_not_be_empty));
        return false;
    }
}
