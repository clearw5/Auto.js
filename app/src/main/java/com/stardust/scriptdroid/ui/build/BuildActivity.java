package com.stardust.scriptdroid.ui.build;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.apkbuilder.ApkBuilder;
import com.stardust.pio.PFiles;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.build.AutoJsApkBuilder;
import com.stardust.scriptdroid.io.StorageFileProvider;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.filechooser.FileChooserDialogBuilder;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.IntentUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/22.
 */
@EActivity(R.layout.activity_build)
public class BuildActivity extends BaseActivity implements AutoJsApkBuilder.ProgressCallback {

    public static final String EXTRA_SOURCE_FILE = BuildActivity.class.getName() + ".extra_source_file";

    private static final String LOG_TAG = "BuildActivity";

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
    private MaterialDialog mProgressDialog;

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_build_apk));
        String sourcePath = getIntent().getStringExtra(EXTRA_SOURCE_FILE);
        if (sourcePath != null) {
            setupWithSourceFile(new ScriptFile(sourcePath));
        }

    }

    private void setupWithSourceFile(ScriptFile file) {
        mSourcePath.setText(file.getPath());
        mOutputPath.setText(file.getParent());
        mAppName.setText(file.getSimplifiedName());
    }

    @Click(R.id.select_source)
    void selectSourceFilePath() {
        String initialDir = new File(mSourcePath.getText().toString()).getParent();
        new FileChooserDialogBuilder(this)
                .title(R.string.text_source_file_path)
                .dir(initialDir == null ? StorageFileProvider.DEFAULT_DIRECTORY_PATH : initialDir)
                .justScriptFile()
                .singleChoice(file -> mSourcePath.setText(file.getPath()))
                .show();
    }


    @Click(R.id.select_output)
    void selectOutputDirPath() {
        String initialDir = new File(mOutputPath.getText().toString()).exists() ?
                mOutputPath.getText().toString() : StorageFileProvider.DEFAULT_DIRECTORY_PATH;
        new FileChooserDialogBuilder(this)
                .title(R.string.text_source_file_path)
                .dir(initialDir)
                .chooseDir()
                .singleChoice(dir -> mOutputPath.setText(dir.getPath()))
                .show();
    }

    @Click(R.id.fab)
    void buildApk() {
        String jsPath = mSourcePath.getText().toString();
        String versionName = mVersionName.getText().toString();
        int versionCode = Integer.parseInt(mVersionCode.getText().toString());
        String appName = mAppName.getText().toString();
        File tmpDir = new File(getCacheDir(), "build/");
        File outApk = new File(mOutputPath.getText().toString(),
                String.format("%s_v%s.apk", appName, versionName));
        showProgressDialog();
        Observable.fromCallable(() ->
                new AutoJsApkBuilder(getAssets().open("template.apk"), outApk, tmpDir.getPath())
                        .setProgressCallback(BuildActivity.this)
                        .prepare()
                        .withConfig(new AutoJsApkBuilder.AppConfig(appName, versionName, versionCode, jsPath))
                        .build()
                        .sign()
                        .cleanWorkspace()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apkBuilder -> onBuildSuccessful(outApk),
                        this::onBuildFailed);

    }

    private void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content(R.string.text_on_progress)
                .show();
    }

    private void onBuildFailed(Throwable error) {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        Toast.makeText(this, getString(R.string.text_build_failed) + error.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Build failed", error);
    }

    private void onBuildSuccessful(File outApk) {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        new MaterialDialog.Builder(this)
                .title(R.string.text_build_successfully)
                .content(getString(R.string.format_build_successfully, outApk.getPath()))
                .positiveText(R.string.text_install)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) ->
                        IntentUtil.installApk(BuildActivity.this, outApk.getPath())
                )
                .show();

    }

    @Override
    public void onPrepare(AutoJsApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_prepare);
    }

    @Override
    public void onBuild(AutoJsApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_build);

    }

    @Override
    public void onSign(AutoJsApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_package);

    }

    @Override
    public void onClean(AutoJsApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_clean);
    }
}
