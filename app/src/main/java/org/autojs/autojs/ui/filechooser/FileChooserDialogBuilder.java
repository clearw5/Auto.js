package org.autojs.autojs.ui.filechooser;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.pio.PFile;

import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.Explorer;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerFileProvider;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import java.io.File;
import java.io.FileFilter;
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
    private MultiChoiceCallback mCallback;
    private FileFilter mFileFilter;
    private String mRootDir;
    private String mInitialDir;

    public FileChooserDialogBuilder(@NonNull Context context) {
        super(context);
        mFileChooseListView = new FileChooseListView(context);
        customView(mFileChooseListView, false);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        onPositive((dialog, which) -> notifySelected());
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
        mRootDir = rootDir;
        mInitialDir = initialDir;
        return this;
    }

    public FileChooserDialogBuilder dir(String dir) {
        mRootDir = dir;
        return this;
    }

    public FileChooserDialogBuilder justScriptFile() {
        mFileFilter = Scripts.INSTANCE.getFILE_FILTER();
        return this;
    }


    public FileChooserDialogBuilder chooseDir() {
        mFileFilter = File::isDirectory;
        mFileChooseListView.setCanChooseDir(true);
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

    @Override
    public MaterialDialog build() {
        ExplorerDirPage root = ExplorerDirPage.createRoot(mRootDir);
        Explorer explorer = mFileFilter == null ? Explorers.external() :
                new Explorer(new ExplorerFileProvider(mFileFilter), 0);
        if(mInitialDir == null){
            mFileChooseListView.setExplorer(explorer, root);
        }else {
            mFileChooseListView.setExplorer(explorer, root,
                    new ExplorerDirPage(mInitialDir, root));
        }
        return super.build();
    }
}
