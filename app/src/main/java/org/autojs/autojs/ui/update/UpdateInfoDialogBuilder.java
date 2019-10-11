package org.autojs.autojs.ui.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.util.IntentUtil;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.external.fileprovider.AppFileProvider;
import org.autojs.autojs.network.download.DownloadManager;
import org.autojs.autojs.network.entity.VersionInfo;
import org.autojs.autojs.tool.IntentTool;
import org.autojs.autojs.ui.widget.CommonMarkdownView;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/4/9.
 */

public class UpdateInfoDialogBuilder extends MaterialDialog.Builder {

    private static final String KEY_DO_NOT_ASK_AGAIN_FOR_VERSION = "I cannot forget you...cannot help missing you...";
    private View mView;
    private SharedPreferences mSharedPreferences;
    private VersionInfo mVersionInfo;

    public UpdateInfoDialogBuilder(@NonNull Context context, VersionInfo info) {
        super(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        updateInfo(info);
    }

    public UpdateInfoDialogBuilder updateInfo(VersionInfo info) {
        mVersionInfo = info;
        mView = View.inflate(context, R.layout.dialog_update_info, null);
        setReleaseNotes(mView, info);
        setCurrentVersionIssues(mView, info);
        setUpdateDownloadButtons(mView, info);
        title(context.getString(R.string.text_new_version) + " " + info.versionName);
        customView(mView, false);
        return this;
    }

    public UpdateInfoDialogBuilder showDoNotAskAgain() {
        mView.findViewById(R.id.do_not_ask_again_container).setVisibility(View.VISIBLE);
        CheckBox checkBox = (CheckBox) mView.findViewById(R.id.do_not_ask_again);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(KEY_DO_NOT_ASK_AGAIN_FOR_VERSION + mVersionInfo.versionCode, isChecked).apply();
            }
        });
        return this;
    }

    @Override
    public MaterialDialog show() {
        if (mSharedPreferences.getBoolean(KEY_DO_NOT_ASK_AGAIN_FOR_VERSION + mVersionInfo.versionCode, false)) {
            return null;
        }
        return super.show();
    }

    private void setCurrentVersionIssues(View view, VersionInfo info) {
        TextView issues = (TextView) view.findViewById(R.id.issues);
        VersionInfo.OldVersion currentVersion = info.getOldVersion(BuildConfig.VERSION_CODE);
        if (currentVersion == null) {
            issues.setVisibility(View.GONE);
        } else {
            issues.setText(currentVersion.issues);
        }
    }

    private void setUpdateDownloadButtons(View view, VersionInfo info) {
        LinearLayout downloads = (LinearLayout) view.findViewById(R.id.downloads);
        setDirectlyDownloadButton(downloads, info);
        for (final VersionInfo.Download download : info.downloads) {
            Button button = (Button) View.inflate(getContext(), R.layout.dialog_update_info_btn, null);
            button.setText(download.name);
            downloads.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentTool.browse(v.getContext(), download.url);
                }
            });
        }
    }

    private void setDirectlyDownloadButton(LinearLayout container, final VersionInfo info) {
        if (TextUtils.isEmpty(info.downloadUrl)) {
            return;
        }
        Button button = (Button) View.inflate(getContext(), R.layout.dialog_update_info_btn, null);
        button.setText(R.string.text_directly_download);
        button.setOnClickListener(v -> directlyDownload(info.downloadUrl));
        container.addView(button);
    }

    @SuppressLint("CheckResult")
    private void directlyDownload(String downloadUrl) {
        final String path = new File(Pref.getScriptDirPath(), "AutoJs.apk").getPath();
        DownloadManager.getInstance().downloadWithProgress(getContext(), downloadUrl, path)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> IntentUtil.installApkOrToast(getContext(), file.getPath(), AppFileProvider.AUTHORITY),
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(getContext(), R.string.text_download_failed, Toast.LENGTH_SHORT).show();
                        });

    }


    private void setReleaseNotes(View view, VersionInfo info) {
        CommonMarkdownView markdownView = view.findViewById(R.id.release_notes);
        markdownView.loadMarkdown(info.releaseNotes);
    }
}
