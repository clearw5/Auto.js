package org.autojs.autojs.ui.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.util.IntentUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.build.ApkBuilder;
import org.autojs.autojs.build.ApkBuilderPluginHelper;
import org.autojs.autojs.external.fileprovider.AppFileProvider;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.tool.BitmapTool;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.filechooser.FileChooserDialogBuilder;
import org.autojs.autojs.ui.shortcut.ShortcutIconSelectActivity;
import org.autojs.autojs.ui.shortcut.ShortcutIconSelectActivity_;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import androidx.cardview.widget.CardView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/22.
 */
@EActivity(R.layout.activity_build)
public class BuildActivity extends BaseActivity implements ApkBuilder.ProgressCallback {

    private static final int REQUEST_CODE = 44401;

    public static final String EXTRA_SOURCE = BuildActivity.class.getName() + ".extra_source_file";

    private static final String LOG_TAG = "BuildActivity";
    private static final Pattern REGEX_PACKAGE_NAME = Pattern.compile("^([A-Za-z][A-Za-z\\d_]*\\.)+([A-Za-z][A-Za-z\\d_]*)$");

    @ViewById(R.id.source_path)
    EditText mSourcePath;

    @ViewById(R.id.source_path_container)
    View mSourcePathContainer;

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

    @ViewById(R.id.icon)
    ImageView mIcon;

    @ViewById(R.id.app_config)
    CardView mAppConfig;

    private ProjectConfig mProjectConfig;
    private MaterialDialog mProgressDialog;
    private String mSource;
    private boolean mIsDefaultIcon = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_build_apk));
        mSource = getIntent().getStringExtra(EXTRA_SOURCE);
        if (mSource != null) {
            setupWithSourceFile(new ScriptFile(mSource));
        }
        checkApkBuilderPlugin();
    }

    private void checkApkBuilderPlugin() {
        if (!ApkBuilderPluginHelper.isPluginAvailable(this)) {
            showPluginDownloadDialog(R.string.no_apk_builder_plugin, true);
            return;
        }
        int version = ApkBuilderPluginHelper.getPluginVersion(this);
        if (version < 0) {
            showPluginDownloadDialog(R.string.no_apk_builder_plugin, true);
            return;
        }
        if (version < ApkBuilderPluginHelper.getSuitablePluginVersion()) {
            showPluginDownloadDialog(R.string.apk_builder_plugin_version_too_low, false);
        }
    }

    private void showPluginDownloadDialog(int msgRes, boolean finishIfCanceled) {
        new ThemeColorMaterialDialogBuilder(this)
                .content(msgRes)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> downloadPlugin())
                .onNegative((dialog, which) -> {
                    if (finishIfCanceled) finish();
                })
                .show();

    }

    private void downloadPlugin() {
        IntentUtil.browse(this, String.format(Locale.getDefault(),
                "https://i.autojs.org/autojs/plugin/%d.apk", ApkBuilderPluginHelper.getSuitablePluginVersion()));
    }

    private void setupWithSourceFile(ScriptFile file) {
        String dir = file.getParent();
        if (dir.startsWith(getFilesDir().getPath())) {
            dir = Pref.getScriptDirPath();
        }
        mOutputPath.setText(dir);
        mAppName.setText(file.getSimplifiedName());
        mPackageName.setText(getString(R.string.format_default_package_name, System.currentTimeMillis()));
        setSource(file);
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
                .dir(Environment.getExternalStorageDirectory().getPath(),
                        initialDir == null ? Pref.getScriptDirPath() : initialDir)
                .singleChoice(this::setSource)
                .show();
    }

    private void setSource(File file) {
        if (!file.isDirectory()) {
            mSourcePath.setText(file.getPath());
            return;
        }
        mProjectConfig = ProjectConfig.fromProjectDir(file.getPath());
        if (mProjectConfig == null) {
            return;
        }
        mOutputPath.setText(new File(mSource, mProjectConfig.getBuildDir()).getPath());
        mAppConfig.setVisibility(View.GONE);
        mSourcePathContainer.setVisibility(View.GONE);
    }

    @Click(R.id.select_output)
    void selectOutputDirPath() {
        String initialDir = new File(mOutputPath.getText().toString()).exists() ?
                mOutputPath.getText().toString() : Pref.getScriptDirPath();
        new FileChooserDialogBuilder(this)
                .title(R.string.text_output_apk_path)
                .dir(initialDir)
                .chooseDir()
                .singleChoice(dir -> mOutputPath.setText(dir.getPath()))
                .show();
    }

    @Click(R.id.icon)
    void selectIcon() {
        ShortcutIconSelectActivity_.intent(this)
                .startForResult(REQUEST_CODE);
    }

    @Click(R.id.fab)
    void buildApk() {
        if (!ApkBuilderPluginHelper.isPluginAvailable(this)) {
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
        inputValid &= checkPackageNameValid(mPackageName);
        return inputValid;
    }

    private boolean checkPackageNameValid(EditText editText) {
        Editable text = editText.getText();
        String hint = ((TextInputLayout) editText.getParent().getParent()).getHint().toString();
        if (TextUtils.isEmpty(text)) {
            editText.setError(hint + getString(R.string.text_should_not_be_empty));
            return false;
        }
        if (!REGEX_PACKAGE_NAME.matcher(text).matches()) {
            editText.setError(getString(R.string.text_invalid_package_name));
            return false;
        }
        return true;

    }

    private boolean checkNotEmpty(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText()) || !editText.isShown())
            return true;
        // TODO: 2017/12/8 more beautiful ways?
        String hint = ((TextInputLayout) editText.getParent().getParent()).getHint().toString();
        editText.setError(hint + getString(R.string.text_should_not_be_empty));
        return false;
    }

    @SuppressLint("CheckResult")
    private void doBuildingApk() {
        ApkBuilder.AppConfig appConfig = createAppConfig();
        File tmpDir = new File(getCacheDir(), "build/");
        File outApk = new File(mOutputPath.getText().toString(),
                String.format("%s_v%s.apk", appConfig.getAppName(), appConfig.getVersionName()));
        showProgressDialog();
        Observable.fromCallable(() -> callApkBuilder(tmpDir, outApk, appConfig))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apkBuilder -> onBuildSuccessful(outApk),
                        this::onBuildFailed);
    }

    private ApkBuilder.AppConfig createAppConfig() {
        if (mProjectConfig != null) {
            return ApkBuilder.AppConfig.fromProjectConfig(mSource, mProjectConfig);
        }
        String jsPath = mSourcePath.getText().toString();
        String versionName = mVersionName.getText().toString();
        int versionCode = Integer.parseInt(mVersionCode.getText().toString());
        String appName = mAppName.getText().toString();
        String packageName = mPackageName.getText().toString();
        return new ApkBuilder.AppConfig()
                .setAppName(appName)
                .setSourcePath(jsPath)
                .setPackageName(packageName)
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setIcon(mIsDefaultIcon ? null : (Callable<Bitmap>) () ->
                        BitmapTool.drawableToBitmap(mIcon.getDrawable())
                );
    }

    private ApkBuilder callApkBuilder(File tmpDir, File outApk, ApkBuilder.AppConfig appConfig) throws Exception {
        InputStream templateApk = ApkBuilderPluginHelper.openTemplateApk(BuildActivity.this);
        return new ApkBuilder(templateApk, outApk, tmpDir.getPath())
                .setProgressCallback(BuildActivity.this)
                .prepare()
                .withConfig(appConfig)
                .build()
                .sign()
                .cleanWorkspace();
    }

    private void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content(R.string.text_on_progress)
                .cancelable(false)
                .show();
    }

    private void onBuildFailed(Throwable error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
                        IntentUtil.installApkOrToast(BuildActivity.this, outApk.getPath(), AppFileProvider.AUTHORITY)
                )
                .show();

    }

    @Override
    public void onPrepare(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_prepare);
    }

    @Override
    public void onBuild(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_build);

    }

    @Override
    public void onSign(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_package);

    }

    @Override
    public void onClean(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_clean);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        ShortcutIconSelectActivity.getBitmapFromIntent(getApplicationContext(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    mIcon.setImageBitmap(bitmap);
                    mIsDefaultIcon = false;
                }, Throwable::printStackTrace);

    }

}
