package org.autojs.autojs.ui.update;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.R;
import org.autojs.autojs.network.VersionService;
import org.autojs.autojs.network.entity.VersionInfo;
import org.autojs.autojs.tool.SimpleObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/4/12.
 */

public class VersionGuard {

    private Activity mActivity;
    private MaterialDialog mDeprecatedDialog;
    private VersionService mVersionService = VersionService.getInstance();

    public VersionGuard(Activity activity) {
        mActivity = activity;
    }

    public void checkForDeprecatesAndUpdates() {
        mVersionService.readDeprecatedFromPrefIfNeeded(mActivity);
        if (mVersionService.isCurrentVersionDeprecated()) {
            showDeprecatedDialogIfNeeded();
        } else {
            checkForUpdatesIfNeeded();
        }
    }

    private void checkForUpdatesIfNeeded() {
        mVersionService.checkForUpdatesIfNeededAndUsingWifi(mActivity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<VersionInfo>() {

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull VersionInfo versionInfo) {
                        if (mVersionService.isCurrentVersionDeprecated()) {
                            showDeprecatedDialogIfNeeded();
                        } else {
                            showUpdateInfoIfNeeded(versionInfo);
                        }
                    }

                });
    }

    private void showUpdateInfoIfNeeded(org.autojs.autojs.network.entity.VersionInfo info) {
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
        String issues = mVersionService.getCurrentVersionIssues();
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
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        new UpdateCheckDialog(mActivity)
                                .show();
                    } else {
                        mActivity.finish();
                    }
                })
                .dismissListener(dialog -> mDeprecatedDialog = null)
                .show();
    }
}
