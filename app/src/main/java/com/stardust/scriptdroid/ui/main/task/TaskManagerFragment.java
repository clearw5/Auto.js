package com.stardust.scriptdroid.ui.main.task;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.jraska.console.Console;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.ui.console.LogView;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;
import com.stardust.widget.SimpleAdapterDataObserver;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import butterknife.OnClick;

/**
 * Created by Stardust on 2017/3/24.
 */
@EFragment(R.layout.fragment_task_manager)
public class TaskManagerFragment extends ViewPagerFragment implements PopupMenu.OnMenuItemClickListener {

    @ViewById(R.id.task_list)
    TaskListRecyclerView mTaskListRecyclerView;

    @ViewById(R.id.notice_no_running_script)
    View mNoRunningScriptNotice;

    @ViewById(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @ViewById(R.id.task_manager_view)
    View mTaskManagerView;

    @ViewById(R.id.log)
    View mLogView;

    @ViewById(R.id.spinner)
    View mSpinner;

    @ViewById(R.id.spinner_current_item)
    TextView mSpinnerCurrentItem;

    public TaskManagerFragment() {
        super(45);
    }


    @AfterViews
    void setUpViews() {
        init();
        final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
        mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
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

    @Click(R.id.spinner)
    void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), mSpinner);
        popupMenu.inflate(R.menu.menu_manager);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.task_manage) {
            showTaskManager();
        } else {
            showLog();
        }
        return true;
    }

    private void showLog() {
        mTaskManagerView.setVisibility(View.GONE);
        mLogView.setVisibility(View.VISIBLE);
        mSpinnerCurrentItem.setText(R.string.text_log);
    }

    private void showTaskManager() {
        mLogView.setVisibility(View.GONE);
        mTaskManagerView.setVisibility(View.VISIBLE);
        mSpinnerCurrentItem.setText(R.string.text_task_manage);
    }


    @Override
    protected void onFabClick(FloatingActionButton fab) {
        if (mLogView.getVisibility() == View.VISIBLE) {
            Console.clear();
        } else {
            AutoJs.getInstance().getScriptEngineService().stopAll();
        }
    }
}
