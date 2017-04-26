package com.stardust.scriptdroid.tool;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/4/18.
 */

public class MaterialDialogFactory {
    public static MaterialDialog createProgress(Context context) {
        return new MaterialDialog.Builder(context)
                .progress(true, 0)
                .cancelable(false)
                .content(R.string.text_processing)
                .build();
    }

    public static MaterialDialog showProgress(Context context) {
        MaterialDialog dialog = createProgress(context);
        dialog.show();
        return dialog;
    }
}
