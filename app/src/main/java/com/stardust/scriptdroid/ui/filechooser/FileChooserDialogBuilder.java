package com.stardust.scriptdroid.ui.filechooser;

import android.content.Context;
import android.support.annotation.NonNull;

import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.model.script.StorageFileProvider;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/10/19.
 */

public class FileChooserDialogBuilder extends ThemeColorMaterialDialogBuilder {

    public interface SingleChoiceCallback {
        void onSelected(PFile file);
    }

    public interface MultiChoiceCallback {
        void onSelected(List<PFile> files);
    }

    private FileChooseListView mFileChooseListView;
    private PFile mRootDir = StorageFileProvider.DEFAULT_DIRECTORY;
    private MultiChoiceCallback mCallback;

    public FileChooserDialogBuilder(@NonNull Context context) {
        super(context);
        mFileChooseListView = new FileChooseListView(context);
        customView(mFileChooseListView, false);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        onPositive((dialog, which) -> {
            notifySelected();
        });
    }

    private void notifySelected() {
        if (mCallback == null)
            return;
        List<PFile> selectedFiles = mFileChooseListView.getSelectedFiles();
        if (selectedFiles.isEmpty()) {
            mCallback.onSelected(Collections.singletonList(mFileChooseListView.getCurrentDirectory()));
        } else {
            mCallback.onSelected(selectedFiles);
        }
    }

    public FileChooserDialogBuilder dir(String rootDir, String initialDir) {
        mRootDir = new PFile(rootDir);
        mFileChooseListView.setStorageFileProvider(new StorageFileProvider(mRootDir, 10), new ScriptFile(initialDir));
        return this;
    }

    public FileChooserDialogBuilder dir(String dir) {
        return dir(dir, dir);
    }


    public FileChooserDialogBuilder chooseDir() {
        mFileChooseListView.setCanChooseDir(true);
        mFileChooseListView.setStorageFileProvider(new StorageFileProvider(mRootDir, 10, File::isDirectory));
        return this;
    }

    public FileChooserDialogBuilder singleChoice(SingleChoiceCallback callback) {
        mFileChooseListView.setMaxChoice(1);
        mCallback = files -> callback.onSelected(files.get(0));
        return this;
    }


    public FileChooserDialogBuilder multiChoice(MultiChoiceCallback callback) {
        return multiChoice(Integer.MAX_VALUE, callback);
    }

    public FileChooserDialogBuilder multiChoice(int maxChoices, MultiChoiceCallback callback) {
        mFileChooseListView.setMaxChoice(maxChoices);
        mCallback = callback;
        return this;
    }


}
