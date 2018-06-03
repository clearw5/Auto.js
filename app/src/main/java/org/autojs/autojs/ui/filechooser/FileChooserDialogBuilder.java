package org.autojs.autojs.ui.filechooser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.stardust.pio.PFile;
import org.autojs.autojs.R;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.storage.file.StorageFileProvider;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import java.io.File;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

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
    private PFile mRootDir = StorageFileProvider.getDefaultDirectory();
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
        if (mRootDir.equals(StorageFileProvider.getDefaultDirectory())) {
            mFileChooseListView.setStorageFileProvider(StorageFileProvider.getDefault());
        } else {
            mFileChooseListView.setStorageFileProvider(new StorageFileProvider(mRootDir, 10), new ScriptFile(initialDir));
        }
        return this;
    }

    public FileChooserDialogBuilder dir(String dir) {
        return dir(dir, dir);
    }

    public FileChooserDialogBuilder justScriptFile() {
        mFileChooseListView.setStorageFileProvider(new StorageFileProvider(mRootDir, 10, StorageFileProvider.SCRIPT_FILTER));
        return this;
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

    public Observable<PFile> singleChoice() {
        PublishSubject<PFile> result = PublishSubject.create();
        singleChoice(file -> {
            result.onNext(file);
            result.onComplete();
        });
        show();
        return result;
    }

    @Override
    public FileChooserDialogBuilder title(@NonNull CharSequence title) {
        super.title(title);
        return this;
    }

    @Override
    public FileChooserDialogBuilder title(@StringRes int titleRes) {
        super.title(titleRes);
        return this;
    }
}
