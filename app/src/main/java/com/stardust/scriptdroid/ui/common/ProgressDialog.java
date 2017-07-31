package com.stardust.scriptdroid.ui.common;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/7/31.
 */

public class ProgressDialog {

    private MaterialDialog mDialog;

    public ProgressDialog(Context context) {
        mDialog = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .cancelable(false)
                .content(R.string.text_on_progress)
                .show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

}
