package com.stardust.scriptdroid.ui.main.task;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stardust.autojs.runtime.console.ConsoleView;
import com.stardust.autojs.runtime.console.StardustConsole;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;
import com.stardust.widget.SimpleAdapterDataObserver;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

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

    @ViewById(R.id.spinner)
    View mSpinner;

    @ViewById(R.id.spinner_current_item)
    TextView mSpinnerCurrentItem;

    @ViewById(R.id.console)
    ConsoleView mConsoleView;

    private StardustConsole mStardustConsole;

    public TaskManagerFragment() {
        super(45);
    }


    @AfterViews
    void setUpViews() {
        init();
        final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
        mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
        mStardustConsole = (StardustConsole) AutoJs.getInstance().getGlobalConsole();
        mConsoleView.setConsole(mStardustConsole);
    }

    private void init() {
        mTaskListRecyclerView.getAdapter().registerAdapterDataObserver(new SimpleAdapterDataObserver() {

            @Override
            public void onSomethingChanged() {
                final boolean noRunningScript = mTaskListRecyclerView.getAdapter().getItemCount() == 0;
                mTaskListRecyclerView.postDelayed(() -> {
                    if (mNoRunningScriptNotice == null)
                        return;
                    mNoRunningScriptNotice.setVisibility(noRunningScript ? View.VISIBLE : View.GONE);
                }, 150);
            }

        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mTaskListRecyclerView.updateEngineList();
            mTaskListRecyclerView.postDelayed(() -> {
                if (mSwipeRefreshLayout != null)
                    mSwipeRefreshLayout.setRefreshing(false);
            }, 800);
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
        mConsoleView.setVisibility(View.VISIBLE);
        mSpinnerCurrentItem.setText(R.string.text_log);
    }

    private void showTaskManager() {
        mConsoleView.setVisibility(View.GONE);
        mTaskManagerView.setVisibility(View.VISIBLE);
        mSpinnerCurrentItem.setText(R.string.text_task_manage);
    }


    @Override
    protected void onFabClick(FloatingActionButton fab) {
        if (mConsoleView.getVisibility() == View.VISIBLE) {
            mStardustConsole.clear();
        } else {
            AutoJs.getInstance().getScriptEngineService().stopAll();
        }
    }
}
