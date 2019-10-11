package org.autojs.autojs.ui.settings;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.storage.file.FileObservable;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.tool.SimpleObserver;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScriptDirPathPreference extends MaterialEditTextPreference {

    private RadioGroup mRadioGroup;

    public ScriptDirPathPreference(Context context) {
        super(context);
    }

    public ScriptDirPathPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScriptDirPathPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScriptDirPathPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAddEditTextToDialogView(@NonNull View dialogView, @NonNull EditText editText) {
        super.onAddEditTextToDialogView(dialogView, editText);
        mRadioGroup = (RadioGroup) View.inflate(getContext(), R.layout.script_dir_pref_radio_group, null);
        ((ViewGroup) dialogView).addView(mRadioGroup);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        String oldPath = Pref.getScriptDirPath();
        super.onDialogClosed(positiveResult);
        if (!positiveResult) {
            return;
        }
        String newPath = Pref.getScriptDirPath();
        if (TextUtils.equals(oldPath, newPath)) {
            return;
        }
        int id = mRadioGroup.getCheckedRadioButtonId();
        if (id == R.id.none) {
            Explorers.workspace().refreshAll();
            return;
        }
        Observable<File> fileObservable;
        if (id == R.id.copy) {
            fileObservable = FileObservable.copy(oldPath, newPath);
        } else {
            fileObservable = FileObservable.move(oldPath, newPath);
        }
        showFileProgressDialog(fileObservable);
    }

    private void showFileProgressDialog(Observable<File> observable) {
        MaterialDialog dialog = new ThemeColorMaterialDialogBuilder(getContext())
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .title(R.string.text_on_progress)
                .cancelable(false)
                .content("")
                .show();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<File>() {

                    @Override
                    public void onNext(File file) {
                        dialog.setContent(file.getPath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Explorers.workspace().refreshAll();
                        Toast.makeText(getContext(), getContext().getString(R.string.text_error_copy_file,
                                e.getMessage()), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                        Explorers.workspace().refreshAll();
                    }
                });
    }
}

