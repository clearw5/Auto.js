package org.autojs.autojs.ui.common;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.R;

import java.io.File;

public class FileNameInputDialog implements MaterialDialog.InputCallback {

    private String mExcluded;
    private boolean mIsFirstTextChanged = true;
    private String mExtension;
    private Context mContext;
    private File mDir;

    private void validateInput(MaterialDialog dialog, String extension) {
        EditText editText = dialog.getInputEditText();
        if (editText == null)
            return;
        Editable input = editText.getText();
        int errorResId = 0;
        if (input == null || input.length() == 0) {
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            return;
        }
        if (new File(mDir, extension == null ? input.toString() : input.toString() + extension).exists()) {
            errorResId = R.string.text_file_exists;
        }
        if (errorResId == 0) {
            editText.setError(null);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        } else {
            editText.setError(mContext.getString(errorResId));
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    @Override
    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
        if (mIsFirstTextChanged) {
            mIsFirstTextChanged = false;
            return;
        }
        EditText editText = dialog.getInputEditText();
        if (editText == null)
            return;
        if (input.equals(mExcluded)) {
            editText.setError(null);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            return;
        }
        validateInput(dialog, mExtension);
    }
}
