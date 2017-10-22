package com.stardust.scriptdroid.ui.build;

import android.annotation.SuppressLint;
import android.widget.EditText;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.ui.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/10/22.
 */
@EActivity(R.layout.activity_build)
public class BuildActivity extends BaseActivity {

    public static final String EXTRA_SOURCE_FILE = BuildActivity.class.getName() + ".extra_source_file";

    @ViewById(R.id.source_path)
    EditText mSourcePath;

    @ViewById(R.id.output_path)
    EditText mOutputPath;

    @ViewById(R.id.app_name)
    EditText mAppName;

    @ViewById(R.id.package_name)
    EditText mPackageName;

    @ViewById(R.id.version_name)
    EditText mVersionName;

    @ViewById(R.id.version_code)
    EditText mVersionCode;

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_build_apk));
        String sourcePath = getIntent().getStringExtra(EXTRA_SOURCE_FILE);
        if (sourcePath != null) {
            setupWithSourceFile(new ScriptFile(sourcePath));
        }

    }

    @SuppressLint("SetTextI18n")
    private void setupWithSourceFile(ScriptFile file) {
        mSourcePath.setText(file.getPath());
        mOutputPath.setText(file.getParent());
        mAppName.setText(file.getSimplifiedName());
        mPackageName.setText("org.autojs.example_" + file.getSimplifiedName());
    }
}
