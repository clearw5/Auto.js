package org.autojs.autojs.ui.main.scripts;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.stardust.app.GlobalAppContext;
import com.stardust.util.IntentUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.external.fileprovider.AppFileProvider;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.tool.SimpleObserver;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.ui.explorer.ExplorerView;
import org.autojs.autojs.ui.main.FloatingActionMenu;
import org.autojs.autojs.ui.main.QueryEvent;
import org.autojs.autojs.ui.main.ViewPagerFragment;
import org.autojs.autojs.ui.project.ProjectConfigActivity;
import org.autojs.autojs.ui.project.ProjectConfigActivity_;
import org.autojs.autojs.ui.viewmodel.ExplorerItemList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/3/13.
 */
@EFragment(R.layout.fragment_my_script_list)
public class MyScriptListFragment extends ViewPagerFragment implements FloatingActionMenu.OnFloatingActionButtonClickListener {

    private static final String TAG = "MyScriptListFragment";

    public MyScriptListFragment() {
        super(0);
    }

    @ViewById(R.id.script_file_list)
    ExplorerView mExplorerView;

    private FloatingActionMenu mFloatingActionMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @AfterViews
    void setUpViews() {
        ExplorerItemList.SortConfig sortConfig = ExplorerItemList.SortConfig.from(PreferenceManager.getDefaultSharedPreferences(getContext()));
        mExplorerView.setSortConfig(sortConfig);
        mExplorerView.setExplorer(Explorers.workspace(), ExplorerDirPage.createRoot(Pref.getScriptDirPath()));
        mExplorerView.setOnItemClickListener((view, item) -> {
            if (item.isEditable()) {
                Scripts.INSTANCE.edit(getActivity(), item.toScriptFile());
            } else {
                IntentUtil.viewFile(GlobalAppContext.get(), item.getPath(), AppFileProvider.AUTHORITY);
            }
        });
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
        mFloatingActionMenu = getActivity().findViewById(R.id.floating_action_menu);
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
        if (mExplorerView.canGoBack()) {
            mExplorerView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPageHide() {
        super.onPageHide();
        if (mFloatingActionMenu != null && mFloatingActionMenu.isExpanded()) {
            mFloatingActionMenu.collapse();
        }
    }

    @Subscribe
    public void onQuerySummit(QueryEvent event) {
        if (!isShown()) {
            return;
        }
        if (event == QueryEvent.CLEAR) {
            mExplorerView.setFilter(null);
            return;
        }
        String query = event.getQuery();
        mExplorerView.setFilter((item -> item.getName().contains(query)));
    }

    @Override
    public void onStop() {
        super.onStop();
        mExplorerView.getSortConfig().saveInto(PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mFloatingActionMenu != null)
            mFloatingActionMenu.setOnFloatingActionButtonClickListener(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(FloatingActionButton button, int pos) {
        if (mExplorerView == null)
            return;
        switch (pos) {
            case 0:
                new ScriptOperations(getContext(), mExplorerView, mExplorerView.getCurrentPage())
                        .newDirectory();
                break;
            case 1:
                new ScriptOperations(getContext(), mExplorerView, mExplorerView.getCurrentPage())
                        .newFile();
                break;
            case 2:
                new ScriptOperations(getContext(), mExplorerView, mExplorerView.getCurrentPage())
                        .importFile();
                break;
            case 3:
                ProjectConfigActivity_.intent(getContext())
                        .extra(ProjectConfigActivity.EXTRA_PARENT_DIRECTORY, mExplorerView.getCurrentPage().getPath())
                        .extra(ProjectConfigActivity.EXTRA_NEW_PROJECT, true)
                        .start();
                break;

        }
    }
}
