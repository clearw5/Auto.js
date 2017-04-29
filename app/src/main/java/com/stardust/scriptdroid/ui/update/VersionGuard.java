package com.stardust.scriptdroid.ui.update;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.UpdateChecker;
import com.stardust.scriptdroid.tool.VersionInfo;

/**
 * Created by Stardust on 2017/4/12.
 */

public class VersionGuard {


    private Activity mActivity;
    private MaterialDialog mDeprecatedDialog;
    private VersionInfo mVersionInfo = VersionInfo.getInstance();

    public VersionGuard(Activity activity) {
        mActivity = activity;
    }

    public void checkDeprecateAndUpdate() {
        mVersionInfo.readDeprecatedFromPrefIfNeeded(mActivity);
        if (mVersionInfo.isCurrentVersionDeprecated()) {
            showDeprecatedDialogIfNeeded();
        } else {
            checkUpdateIfNeeded();
        }
    }

    private void checkUpdateIfNeeded() {
        if (mVersionInfo.getUpdateInfo() == null) {
            mVersionInfo.setOnReceiveUpdateResultCallback(new VersionInfo.OnReceiveUpdateResultCallback() {
                @Override
                public void onReceive(UpdateChecker.UpdateInfo info, boolean isCurrentVersionDeprecated) {
                    mVersionInfo.setOnReceiveUpdateResultCallback(null);
                    if (isCurrentVersionDeprecated) {
                        showDeprecatedDialogIfNeeded();
                    } else {
                        showUpdateInfoIfNeeded(info);
                    }
                }
            });
            mVersionInfo.checkUpdateIfNeeded(mActivity);
        }
    }

    private void showUpdateInfoIfNeeded(UpdateChecker.UpdateInfo info) {
        if (BuildConfig.VERSION_CODE < info.versionCode) {
            new UpdateInfoDialogBuilder(mActivity, info)
                    .showDoNotAskAgain()
                    .show();
        }
    }

    private void showDeprecatedDialogIfNeeded() {
        if (mDeprecatedDialog != null && mDeprecatedDialog.isShowing())
            return;
        String content = mActivity.getString(R.string.warning_version_too_old);
        String issues = mVersionInfo.getCurrentVersionIssues();
        if (issues != null) {
            content += "\n" + issues;
        }
        mDeprecatedDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.text_version_too_old)
                .content(content)
                .positiveText(R.string.text_update)
                .negativeText(R.string.text_exit)
                .cancelable(false)
                .autoDismiss(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            new UpdateCheckDialog(mActivity)
                                    .show();
                        } else {
                            mActivity.finish();
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDeprecatedDialog = null;
                    }
                })
                .show();
    }
}
