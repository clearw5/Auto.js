package com.stardust.scriptdroid.ui.main.script_list;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.Fragment;
import com.stardust.app.OperationDialogBuilder;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageScriptProvider;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.UnderuseExecutors;
import com.stardust.widget.SimpleAdapterDataObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Stardust on 2017/3/13.
 */

public class MyScriptListFragment extends Fragment {

    public static final String MESSAGE_SCRIPT_FILE_ADDED = "MESSAGE_SCRIPT_FILE_ADDED";

    private static final String TAG = "MyScriptListFragment";

    private ScriptAndFolderListRecyclerView mScriptListRecyclerView;
    private ScriptListWithProgressBarView mScriptListWithProgressBarView;
    private View mNoScriptHint;
    private MaterialDialog mScriptFileOperationDialog;
    private MaterialDialog mDirectoryOperationDialog;
    private ScriptFile mSelectedScriptFile;
    private MaterialDialog.InputCallback mFileNameInputCallback = new InputCallback(false);
    private MaterialDialog.InputCallback mDirectoryNameInputCallback = new InputCallback(true);
    private String mFilePathToImport;

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_script_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScriptListWithProgressBarView = $(R.id.script_list);
        mScriptListRecyclerView = mScriptListWithProgressBarView.getScriptAndFolderListRecyclerView();
        mNoScriptHint = $(R.id.hint_no_script);
        initScriptListRecyclerView();
        initDialogs();
        if (mFilePathToImport != null) {
            importFile(mFilePathToImport);
            mFilePathToImport = null;
        }
    }

    private void initScriptListRecyclerView() {
        mScriptListWithProgressBarView.setStorageScriptProvider(StorageScriptProvider.getDefault());
        mScriptListRecyclerView.getAdapter().registerAdapterDataObserver(new SimpleAdapterDataObserver() {
            @Override
            public void onSomethingChanged() {
                if (mScriptListRecyclerView.getAdapter().getItemCount() == 0) {
                    mNoScriptHint.setVisibility(View.VISIBLE);
                } else {
                    mNoScriptHint.setVisibility(View.GONE);
                }
            }
        });
        mScriptListRecyclerView.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {
            @Override
            public void onClick(ScriptFile file, int position) {
                EditActivity.editFile(getContext(), file);
            }
        });
        mScriptListRecyclerView.setOnItemLongClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileLongClickListener() {
            @Override
            public void onLongClick(ScriptFile file, int position) {
                mSelectedScriptFile = file;
                if (file.isDirectory()) {
                    mDirectoryOperationDialog.show();
                } else {
                    mScriptFileOperationDialog.show();
                }
            }
        });
    }


    private void initDialogs() {
        mScriptFileOperationDialog = new OperationDialogBuilder(getContext())
                .item(R.id.rename, R.drawable.ic_ali_rename, R.string.text_rename)
                .item(R.id.open_by_other_apps, R.drawable.ic_ali_open, R.string.text_open_by_other_apps)
                .item(R.id.create_shortcut, R.drawable.ic_ali_shortcut, R.string.text_send_shortcut)
                .item(R.id.delete, R.drawable.ic_ali_delete, R.string.text_delete)
                .bindItemClick(this)
                .build();
        mDirectoryOperationDialog = new OperationDialogBuilder(getContext())
                .item(R.id.rename, R.drawable.ic_ali_rename, R.string.text_rename)
                .item(R.id.delete, R.drawable.ic_ali_delete, R.string.text_delete)
                .bindItemClick(this)
                .build();
    }

    public void newScriptFileForScript(final String script) {
        showFileNameInputDialog("", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                createScriptFile(getCurrentDirectoryPath() + input + ".js", script);
            }
        });
    }

    private String getCurrentDirectoryPath() {
        return getCurrentDirectory().getPath() + "/";
    }

    public void createScriptFile(String path, String script) {
        if (PFile.createIfNotExists(path)) {
            if (script != null) {
                if (!PFile.write(path, script)) {
                    Snackbar.make(getView(), R.string.text_file_write_fail, Snackbar.LENGTH_LONG).show();
                }
            }
            notifyScriptFileChanged();
            Scripts.edit(path);
        } else {
            Snackbar.make(getView(), R.string.text_create_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    public void newScriptFile() {
        newScriptFileForScript(null);
    }

    public void importFile(final String pathFrom) {
        if (getActivity() == null) {
            mFilePathToImport = pathFrom;
            return;
        }
        try {
            importFile(PFile.getNameWithoutExtension(pathFrom), new FileInputStream(pathFrom));
        } catch (FileNotFoundException e) {
            showMessage(R.string.file_not_exists);
        }
    }

    public void importFile(String prefix, final InputStream inputStream) {
        showFileNameInputDialog(PFile.getNameWithoutExtension(prefix), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                final String pathTo = getCurrentDirectoryPath() + input + ".js";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (PFile.copyStream(inputStream, pathTo)) {
                            showMessage(R.string.text_import_succeed);
                        } else {
                            showMessage(R.string.text_import_fail);
                        }
                        notifyScriptFileChanged();
                    }
                }).start();
            }
        });
    }


    public void newDirectory() {
        showNameInputDialog("", mDirectoryNameInputCallback, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                if (new ScriptFile(getCurrentDirectory(), input.toString()).mkdirs()) {
                    showMessage(R.string.text_already_create);
                    notifyScriptFileChanged();
                } else {
                    showMessage(R.string.text_create_fail);
                }
            }
        });
    }

    private ScriptFile getCurrentDirectory() {
        return mScriptListRecyclerView.getCurrentDirectory();
    }

    private void showFileNameInputDialog(String prefix, final MaterialDialog.InputCallback callback) {
        showNameInputDialog(prefix, mFileNameInputCallback, callback);
    }

    private void showNameInputDialog(String prefix, MaterialDialog.InputCallback textWatcher, final MaterialDialog.InputCallback callback) {
        new ThemeColorMaterialDialogBuilder(getActivity()).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .alwaysCallInputCallback()
                .input(getString(R.string.text_please_input_name), prefix, false, textWatcher)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.onInput(dialog, dialog.getInputEditText().getText());
                    }
                })
                .show();
    }

    private void notifyScriptFileChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StorageScriptProvider.getDefault().notifyDirectoryChanged(getCurrentDirectory());
            }
        });
    }

    @Optional
    @OnClick(R.id.rename)
    void renameScriptFile() {
        dismissDialogs();
        String originalName = mSelectedScriptFile.getSimplifiedName();
        showNameInputDialog(originalName, new InputCallback(mSelectedScriptFile.isDirectory(), originalName), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                mSelectedScriptFile.renameTo(input.toString());
                StorageScriptProvider.getDefault().notifyDirectoryChanged(mScriptListRecyclerView.getCurrentDirectory());
                onScriptFileOperated();
            }
        });
    }

    private void dismissDialogs() {
        if (mDirectoryOperationDialog.isShowing())
            mDirectoryOperationDialog.dismiss();
        if (mScriptFileOperationDialog.isShowing())
            mScriptFileOperationDialog.dismiss();
    }


    @Optional
    @OnClick(R.id.open_by_other_apps)
    void openByOtherApps() {
        dismissDialogs();
        Scripts.openByOtherApps(mSelectedScriptFile);
        onScriptFileOperated();
    }

    private void onScriptFileOperated() {
        mSelectedScriptFile = null;
        getView().post(new Runnable() {
            @Override
            public void run() {
                mScriptListWithProgressBarView.hideProgressBar();
            }
        });
    }

    @Optional
    @OnClick(R.id.create_shortcut)
    void createShortcut() {
        dismissDialogs();
        Scripts.createShortcut(mSelectedScriptFile);
        Snackbar.make(getView(), R.string.text_already_create, Snackbar.LENGTH_SHORT).show();
        onScriptFileOperated();
    }

    @Optional
    @OnClick(R.id.delete)
    void deleteScriptFile() {
        dismissDialogs();
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_confirm)
                .positiveText(R.string.cancel)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        doDeletingScriptFile();
                    }
                })
                .show();
    }

    private void doDeletingScriptFile() {
        mScriptListWithProgressBarView.showProgressBar();
        UnderuseExecutors.execute(new Runnable() {
            @Override
            public void run() {
                if (PFile.deleteRecursively(mSelectedScriptFile)) {
                    showMessage(R.string.text_already_delete);
                    notifyScriptFileChanged();
                } else {
                    showMessage(R.string.text_already_delete);
                }
                onScriptFileOperated();
            }
        });
    }

    private void showMessage(final int resId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), resId, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mScriptListRecyclerView.setFocusableInTouchMode(true);
        mScriptListRecyclerView.requestFocus();
    }

    private class InputCallback implements MaterialDialog.InputCallback {

        private boolean mIsDirectory = false;
        private String mExcluded;

        InputCallback(boolean isDirectory, String excluded) {
            mIsDirectory = isDirectory;
            mExcluded = excluded;
        }

        InputCallback(boolean isDirectory) {
            mIsDirectory = isDirectory;
        }

        @Override
        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
            EditText editText = dialog.getInputEditText();
            if (editText == null)
                return;
            int errorResId = 0;
            if (input == null || input.length() == 0) {
                errorResId = R.string.text_name_should_not_be_empty;
            } else if (!input.equals(mExcluded)) {
                if (new File(getCurrentDirectory(), mIsDirectory ? input.toString() : input.toString() + ".js").exists()) {
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
