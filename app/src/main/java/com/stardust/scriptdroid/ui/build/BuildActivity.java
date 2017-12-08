package com.stardust.scriptdroid.ui.build;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.build.AutoJsApkBuilder;
import com.stardust.scriptdroid.build.ApkBuilderPluginHelper;
import com.stardust.scriptdroid.storage.file.StorageFileProvider;
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
import java.io.InputStream;

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
    TextInputEditText mSourcePath;

    @ViewById(R.id.output_path)
    TextInputEditText mOutputPath;

    @ViewById(R.id.app_name)
    TextInputEditText mAppName;

    @ViewById(R.id.package_name)
    TextInputEditText mPackageName;

    @ViewById(R.id.version_name)
    TextInputEditText mVersionName;

    @ViewById(R.id.version_code)
    TextInputEditText mVersionCode;
    private MaterialDialog mProgressDialog;

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_build_apk));
        String sourcePath = getIntent().getStringExtra(EXTRA_SOURCE_FILE);
        if (sourcePath != null) {
            setupWithSourceFile(new ScriptFile(sourcePath));
        }
        showPluginDownloadDialogIfNeeded();
    }

    private void showPluginDownloadDialogIfNeeded() {
        if (ApkBuilderPluginHelper.checkPlugin(this)) {
            return;
        }
        new ThemeColorMaterialDialogBuilder(this)
                .content(R.string.no_apk_builder_plugin)
                .positiveText(R.string.ok)
                .negativeText(R.string.text_exit)
                .onPositive((dialog, which) -> downloadPlugin())
                .onNegative((dialog, which) -> finish())
                .show();

    }

    private void downloadPlugin() {
        // TODO: 2017/11/29
    }

    private void setupWithSourceFile(ScriptFile file) {
        mSourcePath.setText(file.getPath());
        mOutputPath.setText(file.getParent());
        mAppName.setText(file.getSimplifiedName());
        mPackageName.setText(getString(R.string.format_default_package_name, System.currentTimeMillis()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

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
                .title(R.string.text_output_apk_path)
                .dir(initialDir)
                .chooseDir()
                .singleChoice(dir -> mOutputPath.setText(dir.getPath()))
                .show();
    }

    @Click(R.id.fab)
    void buildApk() {
        if (!ApkBuilderPluginHelper.checkPlugin(this)) {
            Toast.makeText(this, R.string.text_apk_builder_plugin_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkInputs()) {
            return;
        }
        doBuildingApk();
    }

    private boolean checkInputs() {
        boolean inputValid = true;
        inputValid &= checkNotEmpty(mSourcePath);
        inputValid &= checkNotEmpty(mOutputPath);
        inputValid &= checkNotEmpty(mAppName);
        inputValid &= checkNotEmpty(mSourcePath);
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

    private void doBuildingApk() {
        String jsPath = mSourcePath.getText().toString();
        String versionName = mVersionName.getText().toString();
        int versionCode = Integer.parseInt(mVersionCode.getText().toString());
        String appName = mAppName.getText().toString();
        String packageName = mPackageName.getText().toString();
        File tmpDir = new File(getCacheDir(), "build/");
        File outApk = new File(mOutputPath.getText().toString(),
                String.format("%s_v%s.apk", appName, versionName));
        showProgressDialog();
        Observable.fromCallable(() -> {
                    InputStream templateApk = ApkBuilderPluginHelper.openTemplateApk(BuildActivity.this);
                    return new AutoJsApkBuilder(templateApk, outApk, tmpDir.getPath())
                            .setProgressCallback(BuildActivity.this)
                            .prepare()
                            .withConfig(new AutoJsApkBuilder.AppConfig(packageName, appName, versionName, versionCode, jsPath))
                            .build()
                            .sign()
                            .cleanWorkspace();
                }
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
