package com.stardust.scriptdroid.ui.common;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.pio.PFile;
import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.script.sample.Sample;
import com.stardust.scriptdroid.ui.main.scripts.MyScriptListFragment;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/7/31.
 */

public class ScriptOperations {

    private Context mContext;
    private View mView;
    private ScriptFile mCurrentDirectory;
    private StorageFileProvider mStorageFileProvider;

    public ScriptOperations(Context context, View view, ScriptFile currentDirectory) {
        mContext = context;
        mView = view;
        mCurrentDirectory = currentDirectory;
        mStorageFileProvider = StorageFileProvider.getDefault();
    }

    public ScriptOperations(Context context, View view) {
        this(context, view, StorageFileProvider.DEFAULT_DIRECTORY);
    }

    public void newScriptFileForScript(final String script) {
        showFileNameInputDialog("", "js")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String input) throws Exception {
                        createScriptFile(getCurrentDirectoryPath() + input + ".js", script, false);
                    }
                });
    }

    private String getCurrentDirectoryPath() {
        return getCurrentDirectory().getPath() + "/";
    }

    private ScriptFile getCurrentDirectory() {
        return mCurrentDirectory;
    }

    public void createScriptFile(String path, String script, boolean edit) {
        if (PFile.createIfNotExists(path)) {
            if (script != null) {
                try {
                    PFile.write(path, script);
                } catch (UncheckedIOException e) {
                    showMessage(R.string.text_file_write_fail);
                    return;
                }
            }
            mStorageFileProvider.notifyFileCreated(mCurrentDirectory, new ScriptFile(path));
            if (edit)
                Scripts.edit(path);
        } else {
            showMessage(R.string.text_create_fail);
        }
    }

    public void newScriptFile() {
        showFileNameInputDialog("", "js")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String input) throws Exception {
                        createScriptFile(getCurrentDirectoryPath() + input + ".js", null, true);
                    }
                });
    }

    public Observable<String> importFile(final String pathFrom) {
        return showFileNameInputDialog(PFile.getNameWithoutExtension(pathFrom), PFile.getExtension(pathFrom))
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull String s) throws Exception {
                        final String pathTo = getCurrentDirectoryPath() + s + "." + PFile.getExtension(pathFrom);
                        if (PFile.copy(pathFrom, pathTo)) {
                            showMessage(R.string.text_import_succeed);
                        } else {
                            showMessage(R.string.text_import_fail);
                        }
                        mStorageFileProvider.notifyFileCreated(mCurrentDirectory, new ScriptFile(pathTo));
                        return pathTo;
                    }
                });
    }

    public Observable<String> importFile(String prefix, final InputStream inputStream, final String ext) {
        return showFileNameInputDialog(PFile.getNameWithoutExtension(prefix), ext)
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull String s) throws Exception {
                        final String pathTo = getCurrentDirectoryPath() + s + "." + ext;
                        if (PFile.copyStream(inputStream, pathTo)) {
                            showMessage(R.string.text_import_succeed);
                        } else {
                            showMessage(R.string.text_import_fail);
                        }
                        mStorageFileProvider.notifyFileCreated(mCurrentDirectory, new ScriptFile(pathTo));
                        return pathTo;
                    }
                });
    }


    public void newDirectory() {
        showNameInputDialog("", new InputCallback())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String path) throws Exception {
                        if (new ScriptFile(getCurrentDirectory(), path).mkdirs()) {
                            showMessage(R.string.text_already_create);
                            mStorageFileProvider.notifyFileCreated(mCurrentDirectory, new ScriptFile(path));
                        } else {
                            showMessage(R.string.text_create_fail);
                        }
                    }
                });
    }

    private void showMessage(final int resId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showMessageWithoutThreadSwitch(resId);
        }
        //switch to ui thread to show message
        App.getApp().getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                showMessageWithoutThreadSwitch(resId);
            }
        });
    }

    private void showMessageWithoutThreadSwitch(int resId) {
        if (mView != null) {
            Snackbar.make(mView, resId, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
        }
    }


    private Observable<String> showFileNameInputDialog(String prefix, String ext) {
        return showNameInputDialog(prefix, new InputCallback(ext));
    }

    private Observable<String> showNameInputDialog(String prefix, MaterialDialog.InputCallback textWatcher) {
        final PublishSubject<String> input = PublishSubject.create();
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .alwaysCallInputCallback()
                .input(getString(R.string.text_please_input_name), prefix, false, textWatcher)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        input.onNext(dialog.getInputEditText().getText().toString());
                        input.onComplete();
                    }
                })
                .build());
        return input;
    }


    private CharSequence getString(int resId) {
        return mContext.getString(resId);
    }

    public Observable<String> importSample(Sample sample) {
        try {
            return importFile(sample.name, mContext.getAssets().open(sample.path), PFile.getExtension(sample.path));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(R.string.text_import_fail);
            return Observable.error(e);
        }
    }

    public Observable<Boolean> rename(final ScriptFile file) {
        final ScriptFile oldFile = new ScriptFile(file.getPath());
        String originalName = file.getSimplifiedName();
        return showNameInputDialog(originalName, new InputCallback(file.isDirectory() ? null : PFile.getExtension(file.getName()),
                originalName))
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull String newName) throws Exception {
                        ScriptFile newFile = file.renameAndReturnNewFile(newName);
                        if (newFile != null) {
                            mStorageFileProvider.notifyFileChanged(mCurrentDirectory, oldFile, newFile);
                        }
                        return newFile != null;
                    }
                });
    }

    public void createShortcut(ScriptFile file) {
        Scripts.createShortcut(file);
        showMessage(R.string.text_already_create);
    }

    public void delete(final ScriptFile scriptFile) {
        Observable.fromPublisher(new Publisher<Boolean>() {
            @Override
            public void subscribe(Subscriber<? super Boolean> s) {
                s.onNext(PFile.deleteRecursively(scriptFile));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean deleted) throws Exception {
                        showMessage(deleted ? R.string.text_already_delete : R.string.text_delete_failed);
                        if (deleted)
                            mStorageFileProvider.notifyFileRemoved(mCurrentDirectory, scriptFile);
                    }
                });
    }


    private class InputCallback implements MaterialDialog.InputCallback {

        private String mExcluded;
        private boolean mIsFirstTextChanged = true;
        private String mExtension;

        InputCallback(@Nullable String ext, String excluded) {
            mExtension = "." + ext;
            mExcluded = excluded;
        }

        InputCallback(String ext) {
            this(ext, null);
        }

        public InputCallback() {
            this(null);
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
            int errorResId = 0;
            if (input == null || input.length() == 0) {
                errorResId = R.string.text_name_should_not_be_empty;
            } else if (!input.equals(mExcluded)) {
                if (new File(getCurrentDirectory(), mExtension == null ? input.toString() : input.toString() + mExtension).exists()) {
                    errorResId = R.string.text_file_exists;
                }
            }
            if (errorResId == 0) {
                editText.setError(null);
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            } else {
                editText.setError(getString(errorResId));
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

        }
    }


}
