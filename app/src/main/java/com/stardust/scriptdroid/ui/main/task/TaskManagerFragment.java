package com.stardust.scriptdroid.ui.main.task;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.widget.SimpleAdapterDataObserver;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/3/24.
 */
@EFragment(R.layout.fragment_task_manager)
public class TaskManagerFragment extends Fragment {

    @ViewById(R.id.task_list)
    TaskListRecyclerView mTaskListRecyclerView;
    @ViewById(R.id.close_all)
    View mCloseAllView;
    @ViewById(R.id.notice_no_running_script)
    View mNoRunningScriptNotice;
    @ViewById(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @AfterViews
    void setUpViews() {
        init();
        final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
        mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
        mCloseAllView.setVisibility(noRunningScript ? View.GONE : View.VISIBLE);
    }

    private void init() {
        mTaskListRecyclerView.getAdapter().registerAdapterDataObserver(new SimpleAdapterDataObserver() {

            @Override
            public void onSomethingChanged() {
                final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
                mTaskListRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mNoRunningScriptNotice == null)
                            return;
                        mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
                        mCloseAllView.setVisibility(noRunningScript ? View.GONE : View.VISIBLE);
                    }
                }, 150);
            }

        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskListRecyclerView.updateEngineList();
                mTaskListRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 800);
            }
        });
    }

    @Click(R.id.close_all)
    void closeAllRunningScripts() {
        AutoJs.getInstance().getScriptEngineService().stopAll();
    }
}
