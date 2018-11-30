package org.autojs.autojs.ui.edit;

import android.content.Context;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.R;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.edit.editor.CodeEditor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

/**
 * Created by Stardust on 2017/9/28.
 */

public class FindOrReplaceDialogBuilder extends ThemeColorMaterialDialogBuilder {

    private static final String KEY_KEYWORDS = "...";

    @BindView(R.id.checkbox_regex)
    CheckBox mRegexCheckBox;

    @BindView(R.id.checkbox_replace)
    CheckBox mReplaceCheckBox;

    @BindView(R.id.checkbox_replace_all)
    CheckBox mReplaceAllCheckBox;

    @BindView(R.id.keywords)
    EditText mKeywordsEditText;

    @BindView(R.id.replacement)
    EditText mReplacementEditText;

    private EditorView mEditorView;

    public FindOrReplaceDialogBuilder(@NonNull Context context, EditorView editorView) {
        super(context);
        mEditorView = editorView;
        setupViews();
        restoreState();
        autoDismiss(false);
        onNegative((dialog, which) -> dialog.dismiss());
        onPositive((dialog, which) -> {
            storeState();
            findOrReplace(dialog);
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

    private void findOrReplace(MaterialDialog dialog) {
        String keywords = mKeywordsEditText.getText().toString();
        if (keywords.isEmpty()) {
            return;
        }
        try {
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
            dialog.dismiss();
        } catch (CodeEditor.CheckedPatternSyntaxException e) {
            e.printStackTrace();
            mKeywordsEditText.setError(getContext().getString(R.string.error_pattern_syntax));
        }

    }

    public FindOrReplaceDialogBuilder setQueryIfNotEmpty(String s) {
        if (!TextUtils.isEmpty(s))
            mKeywordsEditText.setText(s);
        return this;
    }
}
