package com.stardust.scriptdroid.ui.main.scripts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.StorageFileProvider;

/**
 * Created by Stardust on 2017/4/3.
 */

public class ScriptFileChooserDialogBuilder extends MaterialDialog.Builder {

    public interface FileCallback {
        void onFileSelection(MaterialDialog dialog, ScriptFile file);
    }

    private ScriptListWithProgressBarView mScriptListWithProgressBarView;
    private StorageFileProvider mStorageFileProvider;
    private FileCallback mFileCallback;

    public ScriptFileChooserDialogBuilder(@NonNull Context context) {
        super(context);
        mScriptListWithProgressBarView = new ScriptListWithProgressBarView(context);
        mScriptListWithProgressBarView.setMinimumHeight(800);
        mScriptListWithProgressBarView.setScriptFileOperationEnabled(false);
    }

    public ScriptFileChooserDialogBuilder fileCallback(final FileCallback fileCallback) {
        mFileCallback = fileCallback;
        return this;
    }

    public ScriptFileChooserDialogBuilder initialPath(String initialPath) {
        mStorageFileProvider = new StorageFileProvider(initialPath, 5);
        return this;
    }

    public ScriptFileChooserDialogBuilder scriptProvider(StorageFileProvider storageFileProvider) {
        mStorageFileProvider = storageFileProvider;
        return this;
    }

    public MaterialDialog build() {
        if (mStorageFileProvider != null) {
            mScriptListWithProgressBarView.setStorageScriptProvider(mStorageFileProvider);
        }
        customView(mScriptListWithProgressBarView, false);
        final MaterialDialog dialog = super.build();
        if (mFileCallback != null) {
            mScriptListWithProgressBarView.setOnItemClickListener(new ScriptListRecyclerView.OnScriptFileClickListener() {
                @Override
                public void onClick(ScriptFile file, int position) {
                    mFileCallback.onFileSelection(dialog, file);
                }
            });
        }
        return dialog;
    }

}
