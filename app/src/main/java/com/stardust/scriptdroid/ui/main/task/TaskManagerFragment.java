package com.stardust.scriptdroid.ui.main.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.scriptdroid.R;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;
import com.stardust.widget.SimpleAdapterDataObserver;

/**
 * Created by Stardust on 2017/3/24.
 */

public class TaskManagerFragment extends Fragment {

    private TaskListRecyclerView mTaskListRecyclerView;
    private View mCloseAllView;
    private View mNoRunningScriptNotice;

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewBinder.bind(this, view);
        init();
    }

    private void init() {
        mNoRunningScriptNotice = $(R.id.notice_no_running_script);
        mCloseAllView = $(R.id.close_all);
        mTaskListRecyclerView = $(R.id.task_list);
        mTaskListRecyclerView.getAdapter().registerAdapterDataObserver(new SimpleAdapterDataObserver() {

            @Override
            public void onSomethingChanged() {
                final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
                mCloseAllView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
                        mCloseAllView.setVisibility(noRunningScript ? View.GONE : View.VISIBLE);
                    }
                }, 150);
            }

        });
        final SwipeRefreshLayout swipeRefreshLayout = $(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskListRecyclerView.updateEngineList();
                mTaskListRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 800);
            }
        });
    }

    @ViewBinding.Click(R.id.close_all)
    private void closeAllRunningScripts() {
        ScriptEngineService.getInstance().stopAll();
    }
}
