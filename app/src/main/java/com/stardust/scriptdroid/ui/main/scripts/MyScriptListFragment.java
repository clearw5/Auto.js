package com.stardust.scriptdroid.ui.main.scripts;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.scriptdroid.ui.main.NewFileDialogBuilder;
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
public class MyScriptListFragment extends ViewPagerFragment {

    private static final String TAG = "MyScriptListFragment";

    private static ScriptFile sCurrentDirectory = StorageFileProvider.DEFAULT_DIRECTORY;

    public MyScriptListFragment() {
        super(0);
    }

    @ViewById(R.id.script_file_list)
    ScriptListView mScriptFileList;

    @AfterViews
    void setUpViews() {
        mScriptFileList.setOnScriptFileClickListener(new ScriptListView.OnScriptFileClickListener() {
            @Override
            public void onScriptFileClick(View view, ScriptFile file) {
                Scripts.edit(file);
            }
        });
    }


    public static ScriptFile getCurrentDirectory() {
        return sCurrentDirectory;
    }

    @Override
    protected void onFabClick(FloatingActionButton fab) {
        new NewFileDialogBuilder(getContext())
                .show();
    }
}
