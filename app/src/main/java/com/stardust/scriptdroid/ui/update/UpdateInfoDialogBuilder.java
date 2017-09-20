package com.stardust.scriptdroid.ui.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.entity.VersionInfo;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.util.DownloadTask;
import com.stardust.util.IntentUtil;
import com.stardust.widget.CommonMarkdownView;

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directlyDownload(info.downloadUrl);
            }
        });
        container.addView(button);
    }

    private void directlyDownload(String downloadUrl) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.text_downloading)
                .progress(false, 100)
                .show();
        final String path = StorageFileProvider.DEFAULT_DIRECTORY_PATH + "AutoJs.apk";
        final DownloadTask task = new DownloadTask() {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dialog.dismiss();
                if (!result) {
                    Toast.makeText(getContext(), R.string.text_download_failed, Toast.LENGTH_SHORT).show();
                } else {
                    IntentUtil.installApk(getContext(), path);
                }
            }
        };
        task.execute(downloadUrl, path);
    }

    private void setReleaseNotes(View view, VersionInfo info) {
        CommonMarkdownView markdownView = (CommonMarkdownView) view.findViewById(R.id.release_notes);
        markdownView.loadMarkdown(info.releaseNotes);
    }
}
