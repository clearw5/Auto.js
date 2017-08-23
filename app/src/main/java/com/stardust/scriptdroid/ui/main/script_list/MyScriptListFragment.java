package com.stardust.scriptdroid.ui.main.script_list;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.scriptdroid.ui.edit.ScriptEditView;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/3/13.
 */
@EFragment(R.layout.fragment_my_script_list)
public class MyScriptListFragment extends Fragment implements ViewPagerFragment {

    private static final String TAG = "MyScriptListFragment";

    private static ScriptFile sCurrentDirectory = StorageFileProvider.DEFAULT_DIRECTORY;

    @ViewById(R.id.script_file_list)
    ScriptListView mScriptFileList;

    private ScriptFile mSelectedScriptFile;


    @AfterViews
    void setUpViews() {
    }


    public static ScriptFile getCurrentDirectory() {
        return sCurrentDirectory;
    }


    private void notifyScriptFileChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StorageFileProvider.getDefault().notifyDirectoryChanged(getCurrentDirectory());
            }
        });
    }

    @Optional
    @OnClick(R.id.loop)
    void runScriptRepeatedly() {
        new ScriptLoopDialog(getActivity(), mSelectedScriptFile)
                .show();
    }

    @Optional
    @OnClick(R.id.rename)
    void renameScriptFile() {
    }


    @Optional
    @OnClick(R.id.open_by_other_apps)
    void openByOtherApps() {
        Scripts.openByOtherApps(mSelectedScriptFile);
        onScriptFileOperated();
    }

    private void onScriptFileOperated() {
        mSelectedScriptFile = null;

    }

    @Optional
    @OnClick(R.id.create_shortcut)
    void createShortcut() {
        Scripts.createShortcut(mSelectedScriptFile);
        Snackbar.make(getView(), R.string.text_already_create, Snackbar.LENGTH_SHORT).show();
        onScriptFileOperated();
    }

    @Optional
    @OnClick(R.id.delete)
    void deleteScriptFile() {
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
    public void setUpWithFab(ViewPager pager, FloatingActionButton fab) {
        if (fab.getVisibility() != View.VISIBLE) {
            fab.show();
            return;
        }
        fab.animate()
                .rotation(0)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }
}
