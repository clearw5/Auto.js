package com.stardust.scriptdroid.ui.update;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.UpdateChecker;
import com.stardust.util.IntentUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/4/9.
 */

public class UpdateCheckDialog implements UpdateChecker.Callback {

    private MaterialDialog mResultDialog;
    private MaterialDialog mProgressDialog;
    private Context mContext;
    private UpdateChecker mUpdateChecker;


    public UpdateCheckDialog(Context context) {
        mContext = context;
        mUpdateChecker = new UpdateChecker(context, this);
    }

    public void show() {
        mProgressDialog = new MaterialDialog.Builder(mContext)
                .content(mContext.getString(R.string.text_checking_update))
                .progress(true, 0)
                .cancelable(false)
                .show();
        mUpdateChecker.check();
    }

    @Override
    public void onSuccess(UpdateChecker.CheckResult result) {
        mProgressDialog.dismiss();
        if (BuildConfig.VERSION_CODE >= result.versionCode) {
            Toast.makeText(mContext, R.string.text_is_latest_version, Toast.LENGTH_SHORT).show();
            return;
        }
        mResultDialog = buildDialog(result);
        mResultDialog.show();
        String updateSite = result.downloads.get(0).name;
        final String updateUrl = result.downloads.get(0).url;
        mResultDialog = new MaterialDialog.Builder(mContext)
                .title(mContext.getString(R.string.text_new_version) + " " + result.versionName)
                .content(result.releaseNotes)
                .positiveText(updateSite)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        IntentUtil.browse(mContext, updateUrl);
                    }
                })
                .show();
    }

    private MaterialDialog buildDialog(final UpdateChecker.CheckResult result) {
        String updateSite = result.downloads.get(0).name;
        final String updateUrl = result.downloads.get(0).url;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(mContext.getString(R.string.text_new_version) + " " + result.versionName)
                .content(result.releaseNotes)
                .positiveText(updateSite)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        IntentUtil.browse(mContext, updateUrl);
                    }
                });
        if (result.downloads.size() > 1) {
            builder.negativeText(result.downloads.get(1).name)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            IntentUtil.browse(mContext, result.downloads.get(1).url);
                        }
                    });
        }
        return builder.build();
    }

    @Override
    public void onError(Exception exception) {
        mProgressDialog.dismiss();
        Toast.makeText(mContext, R.string.text_check_update_error, Toast.LENGTH_SHORT).show();
    }
}
