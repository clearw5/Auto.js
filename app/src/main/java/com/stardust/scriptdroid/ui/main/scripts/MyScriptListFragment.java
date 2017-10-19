package com.stardust.scriptdroid.ui.main.scripts;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.model.script.Scripts;
import com.stardust.scriptdroid.model.script.StorageFileProvider;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.scriptdroid.ui.main.FloatingActionMenu;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;
import com.stardust.util.BackPressedHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/3/13.
 */
@EFragment(R.layout.fragment_my_script_list)
public class MyScriptListFragment extends ViewPagerFragment implements BackPressedHandler, FloatingActionMenu.OnFloatingActionButtonClickListener {

    private static final String TAG = "MyScriptListFragment";

    public MyScriptListFragment() {
        super(0);
    }

    @ViewById(R.id.script_file_list)
    ScriptListView mScriptFileList;

    private FloatingActionMenu mFloatingActionMenu;

    @AfterViews
    void setUpViews() {
        mScriptFileList.setStorageFileProvider(StorageFileProvider.getDefault());
        mScriptFileList.setOnScriptFileClickListener(new ScriptListView.OnScriptFileClickListener() {
            @Override
            public void onScriptFileClick(View view, ScriptFile file) {
                Scripts.edit(file);
                //EditorFloaty.floatingEdit(getContext(), file);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BackPressedHandler.HostActivity) getActivity())
                .getBackPressedObserver()
                .registerHandlerAtFront(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BackPressedHandler.HostActivity) getActivity())
                .getBackPressedObserver()
                .unregisterHandler(this);
    }

    @Override
    protected void onFabClick(FloatingActionButton fab) {
        initFloatingActionMenuIfNeeded(fab);
        if (mFloatingActionMenu.isExpanded()) {
            mFloatingActionMenu.collapse();
        } else {
            mFloatingActionMenu.expand();

        }
    }

    private void initFloatingActionMenuIfNeeded(final FloatingActionButton fab) {
        if (mFloatingActionMenu != null)
            return;
        mFloatingActionMenu = ((FloatingActionMenu) getActivity().findViewById(R.id.floating_action_menu));
        mFloatingActionMenu.getState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Boolean expanding) {
                        fab.animate()
                                .rotation(expanding ? 45 : 0)
                                .setDuration(300)
                                .start();
                    }
                });
        mFloatingActionMenu.setOnFloatingActionButtonClickListener(this);
    }

    @Override
    public boolean onBackPressed(Activity activity) {
        if (mFloatingActionMenu != null && mFloatingActionMenu.isExpanded()) {
            mFloatingActionMenu.collapse();
            return true;
        }
        if (mScriptFileList.canGoBack()) {
            mScriptFileList.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPageHide() {
        if (mFloatingActionMenu != null && mFloatingActionMenu.isExpanded()) {
            mFloatingActionMenu.collapse();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mFloatingActionMenu != null)
            mFloatingActionMenu.setOnFloatingActionButtonClickListener(null);
    }

    @Override
    public void onClick(FloatingActionButton button, int pos) {
        switch (pos) {
            case 0:
                new ScriptOperations(getContext(), mScriptFileList, mScriptFileList.getCurrentDirectory())
                        .newDirectory();
                break;
            case 1:
                new ScriptOperations(getContext(), mScriptFileList, mScriptFileList.getCurrentDirectory())
                        .newScriptFile();
                break;
            case 2:
                break;

        }
    }
}
