package com.stardust.scriptdroid.ui.edit;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.stardust.scriptdroid.R;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

/**
 * Created by Stardust on 2017/9/28.
 */

public class FindOrReplaceDialogBuilder extends ThemeColorMaterialDialogBuilder {

    private static final String KEY_KEYWORDS = "你回来好不好...";

    @BindView(R.id.checkbox_regex)
    CheckBox mRegexCheckBox;

    @BindView(R.id.checkbox_replace)
    CheckBox mReplaceCheckBox;

    @BindView(R.id.checkbox_replace_all)
    CheckBox mReplaceAllCheckBox;

    @BindView(R.id.keywords)
    TextInputEditText mKeywordsEditText;

    @BindView(R.id.replacement)
    TextInputEditText mReplacementEditText;

    private EditorView mEditorView;


    public FindOrReplaceDialogBuilder(@NonNull Context context, EditorView editorView) {
        super(context);
        mEditorView = editorView;
        setupViews();
        restoreState();
        onPositive(new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                storeState();
                findOrReplace();
            }

        });
    }

    private void setupViews() {
        View view = View.inflate(context, R.layout.dialog_find_or_replace, null);
        ButterKnife.bind(this, view);
        customView(view, true);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        title(R.string.text_find_or_replace);
    }


    private void storeState() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putString(KEY_KEYWORDS, mKeywordsEditText.getText().toString())
                .apply();
    }


    private void restoreState() {
        mKeywordsEditText.setText(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(KEY_KEYWORDS, ""));
    }

    @OnCheckedChanged(R.id.checkbox_replace_all)
    void syncWithReplaceCheckBox() {
        if (mReplaceAllCheckBox.isChecked() && !mReplaceCheckBox.isChecked()) {
            mReplaceCheckBox.setChecked(true);
        }
    }

    @OnTextChanged(R.id.replacement)
    void onTextChanged() {
        if (mReplacementEditText.getText().length() > 0) {
            mReplaceCheckBox.setChecked(true);
        }
    }

    private void findOrReplace() {
        String keywords = mKeywordsEditText.getText().toString();
        if (keywords.isEmpty()) {
            return;
        }
        boolean usingRegex = mRegexCheckBox.isChecked();
        if (!mReplaceCheckBox.isChecked()) {
            mEditorView.find(keywords, usingRegex);
        } else {
            String replacement = mReplacementEditText.getText().toString();
            if (mReplaceAllCheckBox.isChecked()) {
                mEditorView.replaceAll(keywords, replacement, usingRegex);
            } else {
                mEditorView.replace(keywords, replacement, usingRegex);
            }
        }

    }

    public FindOrReplaceDialogBuilder setQueryIfNotEmpty(String s) {
        if (!TextUtils.isEmpty(s))
            mKeywordsEditText.setText(s);
        return this;
    }
}
