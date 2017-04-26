package com.stardust.scriptdroid.ui.update;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.UpdateChecker;
import com.stardust.util.IntentUtil;

import java.util.LinkedList;

/**
 * Created by Stardust on 2017/4/9.
 */

public class UpdateCheckDialog implements UpdateChecker.Callback {

    private MaterialDialog mProgressDialog;
    private Context mContext;
    private UpdateChecker mUpdateChecker;


    public UpdateCheckDialog(Context context) {
        mContext = context;
        mUpdateChecker = new UpdateChecker(context);
    }

    public void show() {
        if (mProgressDialog != null)
            throw new IllegalStateException();
        mProgressDialog = new MaterialDialog.Builder(mContext)
                .content(mContext.getString(R.string.text_checking_update))
                .progress(true, 0)
                .cancelable(false)
                .show();
        mUpdateChecker.check(this);
    }

    @Override
    public void onSuccess(UpdateChecker.UpdateInfo result) {
        mProgressDialog.dismiss();
        if (BuildConfig.VERSION_CODE >= result.versionCode) {
            Toast.makeText(mContext, R.string.text_is_latest_version, Toast.LENGTH_SHORT).show();
            return;
        }
        new UpdateInfoDialogBuilder(mContext, result)
                .show();
        clear();
    }

    @Override
    public void onError(Exception exception) {
        mProgressDialog.dismiss();
        Toast.makeText(mContext, R.string.text_check_update_error, Toast.LENGTH_SHORT).show();
        clear();
    }

    private void clear() {
        mProgressDialog = null;
        mContext = null;
    }

}
