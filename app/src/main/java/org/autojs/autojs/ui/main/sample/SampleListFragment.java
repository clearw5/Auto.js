package org.autojs.autojs.ui.main.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import org.autojs.autojs.R;
import org.autojs.autojs.storage.file.SampleFileProvider;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.ui.main.QueryEvent;
import org.autojs.autojs.ui.main.ViewPagerFragment;
import org.autojs.autojs.ui.main.scripts.ScriptListView;
import com.stardust.util.BackPressedHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Stardust on 2017/10/28.
 */
@EFragment(R.layout.fragment_sample_list)
public class SampleListFragment extends ViewPagerFragment implements BackPressedHandler {


    public SampleListFragment() {
        super(ROTATION_GONE);
    }

    @ViewById(R.id.sample_list)
    ScriptListView mScriptFileList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @AfterViews
    void setUpViews() {
        mScriptFileList.setDirectorySpanSize(2);
        mScriptFileList.setStorageFileProvider(new SampleFileProvider(getContext()));
        mScriptFileList.setOnScriptFileClickListener((view, file) -> Scripts.edit(file));
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

    }


    @Override
    public boolean onBackPressed(Activity activity) {
        if (mScriptFileList.canGoBack()) {
            mScriptFileList.goBack();
            return true;
        }
        return false;
    }


    @Subscribe
    public void onQuerySummit(QueryEvent event) {
        if (!isShown()) {
            return;
        }
        if (event == QueryEvent.CLEAR) {
            mScriptFileList.setFilter(null);
            return;
        }
        String query = event.getQuery();
        mScriptFileList.setFilter((file -> file.getSimplifiedName().contains(query)));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
