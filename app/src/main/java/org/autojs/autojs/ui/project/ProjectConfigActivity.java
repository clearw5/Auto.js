package org.autojs.autojs.ui.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;

import com.stardust.autojs.project.LaunchConfig;
import com.stardust.autojs.project.ProjectConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.project.ProjectTemplate;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.io.File;
import java.util.concurrent.Executors;

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
            if(dir == null){
                finish();
                return;
            }
            mDirectory = new File(dir);
            mProjectConfig = ProjectConfig.fromProjectDir(dir);
        }
    }

    @AfterViews
    void setupViews() {
        setToolbarAsBack(mNewProject ? getString(R.string.text_new_project) : mProjectConfig.getName());
        mAppName.addTextChangedListener(new SimpleTextWatcher(s ->
                mProjectLocation.setText(new File(mParentDirectory, s.toString()).getPath()))
        );

    }

    @SuppressLint("CheckResult")
    @Click(R.id.fab)
    void commit() {
        syncProjectConfig();
        if (!checkInputs()) {
            return;
        }
        if(mNewProject){
            String location = mProjectLocation.getText().toString();
            new ProjectTemplate(mProjectConfig, new File(location))
                    .newProject()
                    .subscribe(ignored -> {
                        Explorers.workspace().notifyItemCreated(new ExplorerDirPage(location, null));
                        finish();
                    });
        }
    }

    private void syncProjectConfig(){
        mProjectConfig.setName(mAppName.getText().toString());
        mProjectConfig.setVersionCode(Integer.parseInt(mVersionCode.getText().toString()));
        mProjectConfig.setVersionName(mVersionName.getText().toString());
        mProjectConfig.setMainScriptFile(mMainFileName.getText().toString());
        //mProjectConfig.getLaunchConfig().setHideLogs(true);
    }

    private boolean checkInputs() {
        return true;
    }
}
