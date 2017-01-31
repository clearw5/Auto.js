package com.stardust.app;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;

import static com.jecelyin.common.utils.StringUtils.md5;

/**
 * Created by Stardust on 2017/1/30.
 */

public class NotRemindAgainDialog extends MaterialDialog {

    protected NotRemindAgainDialog(Builder builder) {
        super(builder);
    }


    public static class Builder extends MaterialDialog.Builder {

        private String mKeyRemind;
        private boolean mRemind;

        public Builder(@NonNull Context context) {
            super(context);
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
            generatePreferenceKey();
            mRemind = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(mKeyRemind, true);
        }

        private void generatePreferenceKey() {
            mKeyRemind = md5(TextUtils.join("", Thread.currentThread().getStackTrace()));
        }
    }
}
