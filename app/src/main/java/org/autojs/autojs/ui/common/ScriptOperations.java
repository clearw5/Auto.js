package org.autojs.autojs.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.app.GlobalAppContext;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;
import com.tencent.bugly.crashreport.BuglyLog;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.model.explorer.Explorer;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerFileItem;
import org.autojs.autojs.model.explorer.ExplorerPage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.storage.file.TmpScriptFiles;
import org.autojs.autojs.model.sample.SampleFile;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.network.download.DownloadManager;
import org.autojs.autojs.ui.filechooser.FileChooserDialogBuilder;
import org.autojs.autojs.ui.shortcut.ShortcutCreateActivity;
import org.autojs.autojs.ui.timing.TimedTaskSettingActivity_;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by Stardust on 2017/7/31.
 */

@SuppressLint("CheckResult")
public class ScriptOperations {

    private static final String LOG_TAG = "ScriptOperations";
    private final ExplorerPage mExplorerPage;
    private Context mContext;
    private View mView;
    private ScriptFile mCurrentDirectory;
    private Explorer mExplorer;

    public ScriptOperations(Context context, View view, ScriptFile currentDirectory) {
        mContext = context;
        mView = view;
        mCurrentDirectory = currentDirectory;
        mExplorer = Explorers.workspace();
        mExplorerPage = new ExplorerDirPage(currentDirectory, null);
    }

    public ScriptOperations(Context context, View view, ExplorerPage page) {
        mContext = context;
        mView = view;
        mCurrentDirectory = page.toScriptFile();
        mExplorer = Explorers.workspace();
        mExplorerPage = page;
    }

    public ScriptOperations(Context context, View view) {
        this(context, view, new ScriptFile(Pref.getScriptDirPath()));
    }

    public void newScriptFileForScript(final String script) {
        showFileNameInputDialog("", "js")
                .subscribe(input ->
                        createScriptFile(getCurrentDirectoryPath() + input + ".js", script, false)
                );
    }

    private String getCurrentDirectoryPath() {
        return getCurrentDirectory().getPath() + "/";
    }

    private ScriptFile getCurrentDirectory() {
        return mCurrentDirectory;
    }

    public void createScriptFile(String path, String script, boolean edit) {
        if (PFiles.createIfNotExists(path)) {
            if (script != null) {
                try {
                    PFiles.write(path, script);
                } catch (UncheckedIOException e) {
                    showMessage(R.string.text_file_write_fail);
                    return;
                }
            }
            notifyFileCreated(mCurrentDirectory, new ScriptFile(path));
            if (edit)
                Scripts.INSTANCE.edit(mContext, path);
        } else {
            showMessage(R.string.text_create_fail);
        }
    }

    private void notifyFileCreated(ScriptFile directory, ScriptFile scriptFile) {
        if (scriptFile.isDirectory()) {
            mExplorer.notifyItemCreated(new ExplorerDirPage(scriptFile, mExplorerPage));
        } else {
            mExplorer.notifyItemCreated(new ExplorerFileItem(scriptFile, mExplorerPage));
        }
    }

    public void newFile() {
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .alwaysCallInputCallback()
                .input(getString(R.string.text_please_input_name), "", false, (dialog, input) ->
                        validateInput(dialog, dialog.isPromptCheckBoxChecked() ? ".js" : null))
                .checkBoxPromptRes(R.string.text_js_file, true, (buttonView, isChecked) -> {
                })
                .onPositive((dialog, which) -> {
                    boolean createJs = dialog.isPromptCheckBoxChecked();
                    assert dialog.getInputEditText() != null;
                    if (createJs) {
                        createScriptFile(getCurrentDirectoryPath() + dialog.getInputEditText().getText() + ".js", null, true);
                    } else {
                        createScriptFile(getCurrentDirectoryPath() + dialog.getInputEditText().getText(), null, false);
                    }
                })
                .build());
    }

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
        if (new File(getCurrentDirectory(), extension == null ? input.toString() : input.toString() + extension).exists()) {
            errorResId = R.string.text_file_exists;
        }
        if (errorResId == 0) {
            editText.setError(null);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        } else {
            editText.setError(getString(errorResId));
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    public Observable<String> importFile(final String pathFrom) {
        return showFileNameInputDialog(PFiles.getNameWithoutExtension(pathFrom), PFiles.getExtension(pathFrom))
                .observeOn(Schedulers.io())
                .map(input -> {
                    final String pathTo = getCurrentDirectoryPath() + input + "." + PFiles.getExtension(pathFrom);
                    if (PFiles.copy(pathFrom, pathTo)) {
                        showMessage(R.string.text_import_succeed);
                    } else {
                        showMessage(R.string.text_import_fail);
                    }
                    return pathTo;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(path -> notifyFileCreated(mCurrentDirectory, new ScriptFile(path)));
    }

    public Observable<String> importFile(String prefix, final InputStream inputStream, final String ext) {
        return showFileNameInputDialog(PFiles.getNameWithoutExtension(prefix), ext)
                .observeOn(Schedulers.io())
                .map(input -> {
                    final String pathTo = getCurrentDirectoryPath() + input + "." + ext;
                    if (PFiles.copyStream(inputStream, pathTo)) {
                        showMessage(R.string.text_import_succeed);
                    } else {
                        showMessage(R.string.text_import_fail);
                    }
                    notifyFileCreated(mCurrentDirectory, new ScriptFile(pathTo));
                    return pathTo;
                });
    }


    public void newDirectory() {
        showNameInputDialog("", new InputCallback())
                .subscribe(path -> {
                    ScriptFile newDir = new ScriptFile(getCurrentDirectory(), path);
                    if (newDir.mkdirs()) {
                        showMessage(R.string.text_already_create);
                        notifyFileCreated(mCurrentDirectory, new ScriptFile(newDir));
                    } else {
                        showMessage(R.string.text_create_fail);
                    }
                });
    }

    private void showMessage(final int resId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showMessageWithoutThreadSwitch(resId);
        }
        //switch to ui thread to show message
        GlobalAppContext.post(() -> showMessageWithoutThreadSwitch(resId));
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
                .onPositive((dialog, which) -> {
                    input.onNext(dialog.getInputEditText().getText().toString());
                    input.onComplete();
                })
                .build());
        return input;
    }


    private CharSequence getString(int resId) {
        return mContext.getString(resId);
    }

    public Observable<String> importSample(SampleFile sample) {
        try {
            return importFile(sample.getSimplifiedName(), sample.openInputStream(), sample.getExtension());
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(R.string.text_import_fail);
            return Observable.error(e);
        }
    }

    public Observable<ExplorerFileItem> rename(final ExplorerFileItem item) {
        String originalName = item.getName();
        return showNameInputDialog(originalName, new InputCallback(null, originalName))
                .map(newName -> {
                    ExplorerFileItem newItem = item.rename(newName);
                    if (ObjectHelper.equals(newItem.toScriptFile(), item.toScriptFile())) {
                        showMessage(R.string.error_cannot_rename);
                        throw new IOException();
                    }
                    notifyFileChanged(mCurrentDirectory, item, newItem);
                    return newItem;
                });
    }

    private void notifyFileChanged(ScriptFile directory, ExplorerFileItem oldItem, ExplorerFileItem newItem) {
        mExplorer.notifyItemChanged(oldItem, newItem);
    }

    public void createShortcut(ScriptFile file) {
        mContext.startActivity(new Intent(mContext, ShortcutCreateActivity.class)
                .putExtra(ShortcutCreateActivity.EXTRA_FILE, file));
    }

    public void delete(final ScriptFile scriptFile) {
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext)
                .title(mContext.getString(R.string.text_are_you_sure_to_delete, scriptFile.getName()))
                .positiveText(R.string.cancel)
                .negativeText(R.string.ok)
                .onNegative((dialog, which) -> {
                    deleteWithoutConfirm(scriptFile);
                })
                .build());

    }

    @SuppressLint("CheckResult")
    public void deleteWithoutConfirm(final ScriptFile scriptFile) {
        boolean isDir = scriptFile.isDirectory();
        Observable.fromPublisher((Publisher<Boolean>) s -> s.onNext(PFiles.deleteRecursively(scriptFile)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deleted -> {
                    showMessage(deleted ? R.string.text_already_delete : R.string.text_delete_failed);
                    if (deleted)
                        notifyFileRemoved(isDir, scriptFile);
                });
    }

    private void notifyFileRemoved(boolean isDir, ScriptFile scriptFile) {
        if (isDir) {
            mExplorer.notifyItemRemoved(new ExplorerDirPage(scriptFile, mExplorerPage));
        } else {
            mExplorer.notifyItemRemoved(new ExplorerFileItem(scriptFile, mExplorerPage));
        }
    }


    public Observable<ScriptFile> download(String url) {
        BuglyLog.i(LOG_TAG, "dir = " + Pref.getScriptDirPath() + ", sdcard = " + Environment.getExternalStorageDirectory() + ", url = " + url);
        String fileName = DownloadManager.parseFileNameLocally(url);
        return new FileChooserDialogBuilder(mContext)
                .title(R.string.text_select_save_path)
                .dir(Pref.getScriptDirPath())
                .chooseDir()
                .singleChoice()
                .map(saveDir -> new File(saveDir, fileName).getPath())
                .flatMap(savePath -> {
                    if (!new File(savePath).exists()) {
                        return Observable.just(savePath);
                    }
                    return RxDialogs.confirm(mContext, R.string.confirm_overwrite_file)
                            .flatMap(yes -> {
                                if (yes) {
                                    new File(savePath).delete();
                                    return Observable.just(savePath);
                                } else {
                                    return Observable.empty();
                                }
                            });
                })
                .flatMap(savePath -> DownloadManager.getInstance().downloadWithProgress(mContext, url, savePath))
                .map(ScriptFile::new);
    }

    public Observable<ScriptFile> temporarilyDownload(String url) {
        return Observable.fromCallable(() -> TmpScriptFiles.create(mContext))
                .flatMap(tmpFile ->
                        DownloadManager.getInstance().downloadWithProgress(mContext, url, tmpFile.getPath()))
                .map(ScriptFile::new);
    }

    public void importFile() {
        new FileChooserDialogBuilder(mContext)
                .dir(Environment.getExternalStorageDirectory().getPath())
                .justScriptFile()
                .singleChoice(file -> importFile(file.getPath()).subscribe())
                .title(R.string.text_select_file_to_import)
                .positiveText(R.string.ok)
                .show();
    }

    public void timedTask(ScriptFile scriptFile) {
        TimedTaskSettingActivity_.intent(mContext)
                .extra(ScriptIntents.EXTRA_KEY_PATH, scriptFile.getPath())
                .start();
    }


    private class InputCallback implements MaterialDialog.InputCallback {

        private String mExcluded;
        private boolean mIsFirstTextChanged = true;
        private String mExtension;

        InputCallback(@Nullable String ext, String excluded) {
            mExtension = ext == null ? null : "." + ext;
            mExcluded = excluded;
        }

        InputCallback(String ext) {
            this(ext, null);
        }

        InputCallback() {
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
            if (input.equals(mExcluded)) {
                editText.setError(null);
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                return;
            }
            validateInput(dialog, mExtension);
        }
    }


}
