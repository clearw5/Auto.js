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
import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageScriptProvider;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.UnderuseExecutors;
import com.stardust.widget.SimpleAdapterDataObserver;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/3/13.
 */

public class MyScriptListFragment extends Fragment {

    public static final String MESSAGE_SCRIPT_FILE_ADDED = "MESSAGE_SCRIPT_FILE_ADDED";

    private static final String TAG = "MyScriptListFragment";

    private static ScriptFile sCurrentDirectory = StorageScriptProvider.DEFAULT_DIRECTORY;

    private ScriptAndFolderListRecyclerView mScriptListRecyclerView;
    private ScriptListWithProgressBarView mScriptListWithProgressBarView;
    private View mNoScriptHint;
    private MaterialDialog mScriptFileOperationDialog;
    private MaterialDialog mDirectoryOperationDialog;
    private ScriptFile mSelectedScriptFile;

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
        mScriptListRecyclerView.setOnCurrentDirectoryChangeListener(new ScriptAndFolderListRecyclerView.OnCurrentDirectoryChangeListener() {
            @Override
            public void onChange(ScriptFile oldDir, ScriptFile newDir) {
                sCurrentDirectory = newDir;
            }
        });
    }

    private void initDialogs() {
        mScriptFileOperationDialog = new OperationDialogBuilder(getContext())
                .item(R.id.loop, R.drawable.ic_loop_white_24dp, R.string.text_run_repeatedly)
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


    public static ScriptFile getCurrentDirectory() {
        return sCurrentDirectory;
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
    @OnClick(R.id.loop)
    void runScriptRepeatedly() {
        dismissDialogs();
        new ScriptLoopDialog(getActivity(), mSelectedScriptFile)
                .show();
    }

    @Optional
    @OnClick(R.id.rename)
    void renameScriptFile() {
        dismissDialogs();
        new ScriptOperations(getActivity(), getView())
                .rename(mSelectedScriptFile)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean renamed) throws Exception {
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
        getActivity().runOnUiThread(new Runnable() {
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
        Observable.fromPublisher(new Publisher<Boolean>() {
            @Override
            public void subscribe(Subscriber<? super Boolean> s) {
                s.onNext(PFile.deleteRecursively(mSelectedScriptFile));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean deleted) throws Exception {
                        showMessage(deleted ? R.string.text_already_delete : R.string.text_delete_failed);
                        notifyScriptFileChanged();
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
        private boolean mIsFirstTextChanged = true;

        InputCallback(boolean isDirectory, String excluded) {
            mIsDirectory = isDirectory;
            mExcluded = excluded;
        }

        InputCallback(boolean isDirectory) {
            mIsDirectory = isDirectory;
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
