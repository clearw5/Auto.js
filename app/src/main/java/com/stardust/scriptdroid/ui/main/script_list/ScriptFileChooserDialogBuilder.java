package com.stardust.scriptdroid.ui.main.script_list;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.scripts.StorageScriptProvider;

/**
 * Created by Stardust on 2017/4/3.
 */

public class ScriptFileChooserDialogBuilder extends MaterialDialog.Builder {

    public interface FileCallback {
        void onFileSelection(MaterialDialog dialog, ScriptFile file);
    }

    private ScriptListWithProgressBarView mScriptListWithProgressBarView;
    private StorageScriptProvider mStorageScriptProvider;
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
        mStorageScriptProvider = new StorageScriptProvider(initialPath, 5);
        return this;
    }

    public ScriptFileChooserDialogBuilder scriptProvider(StorageScriptProvider storageScriptProvider) {
        mStorageScriptProvider = storageScriptProvider;
        return this;
    }

    public MaterialDialog build() {
        if (mStorageScriptProvider != null) {
            mScriptListWithProgressBarView.setStorageScriptProvider(mStorageScriptProvider);
        }
        customView(mScriptListWithProgressBarView, false);
        final MaterialDialog dialog = super.build();
        if (mFileCallback != null) {
            mScriptListWithProgressBarView.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {
                @Override
                public void onClick(ScriptFile file, int position) {
                    mFileCallback.onFileSelection(dialog, file);
                }
            });
        }
        return dialog;
    }

}
