package com.stardust.app;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.HashUtils;


/**
 * Created by Stardust on 2017/1/30.
 */

public class NotAskAgainDialog extends MaterialDialog {

    protected NotAskAgainDialog(Builder builder) {
        super(builder);
    }


    public static class Builder extends ThemeColorMaterialDialogBuilder {

        private String mKeyRemind;
        private boolean mRemind;

        public Builder(@NonNull Context context) {
            this(context, null);
        }

        public Builder(Context context, String key) {
            super(context);
            mKeyRemind = key;
            readRemindStatus();
            checkBoxPrompt(context.getString(R.string.text_do_not_remind_again), false, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setRemindState(!isChecked);
                }
            });
        }

        public MaterialDialog show() {
            if (mRemind) {
                return super.show();
            }
            return null;
        }

        private void setRemindState(boolean remind) {
            mRemind = remind;
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                    .putBoolean(mKeyRemind, remind).apply();
        }

        private void readRemindStatus() {
            generatePreferenceKeyIfNeeded();
            mRemind = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(mKeyRemind, true);
        }

        private void generatePreferenceKeyIfNeeded() {
            if (mKeyRemind == null)
                mKeyRemind = HashUtils.md5(TextUtils.join("", Thread.currentThread().getStackTrace()));
        }
    }
}
