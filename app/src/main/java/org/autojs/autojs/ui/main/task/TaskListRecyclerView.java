package org.autojs.autojs.ui.main.task;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.workground.WrapContentLinearLayoutManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.storage.database.ModelChange;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.ui.timing.TimedTaskSettingActivity;
import org.autojs.autojs.ui.timing.TimedTaskSettingActivity_;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.ThemeColorRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Stardust on 2017/3/24.
 */

public class TaskListRecyclerView extends ThemeColorRecyclerView {


    private static final String LOG_TAG = "TaskListRecyclerView";

    private final List<TaskGroup> mTaskGroups = new ArrayList<>();
    private TaskGroup.RunningTaskGroup mRunningTaskGroup;
    private TaskGroup.PendingTaskGroup mPendingTaskGroup;
    private Adapter mAdapter;
    private Disposable mTimedTaskChangeDisposable;
    private Disposable mIntentTaskChangeDisposable;
    private ScriptExecutionListener mScriptExecutionListener = new SimpleScriptExecutionListener() {
        @Override
        public void onStart(final ScriptExecution execution) {
            post(() -> mAdapter.notifyChildInserted(0, mRunningTaskGroup.addTask(execution)));
        }

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            onFinish(execution);
        }

        @Override
        public void onException(ScriptExecution execution, Throwable e) {
            onFinish(execution);
        }

        private void onFinish(ScriptExecution execution) {
            post(() -> {
                final int i = mRunningTaskGroup.removeTask(execution);
                if (i >= 0) {
                    mAdapter.notifyChildRemoved(0, i);
                } else {
                    refresh();
                }
            });
        }
    };

    public TaskListRecyclerView(Context context) {
        super(context);
        init();
    }

    public TaskListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TaskListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(ContextCompat.getColor(getContext(), R.color.divider))
                .size(2)
                .marginResId(R.dimen.script_and_folder_list_divider_left_margin, R.dimen.script_and_folder_list_divider_right_margin)
                .showLastDivider()
                .build());
        mRunningTaskGroup = new TaskGroup.RunningTaskGroup(getContext());
        mTaskGroups.add(mRunningTaskGroup);
        mPendingTaskGroup = new TaskGroup.PendingTaskGroup(getContext());
        mTaskGroups.add(mPendingTaskGroup);
        mAdapter = new Adapter(mTaskGroups);
        setAdapter(mAdapter);
    }

    public void refresh() {
        for (TaskGroup group : mTaskGroups) {
            group.refresh();
        }
        mAdapter = new Adapter(mTaskGroups);
        setAdapter(mAdapter);
        //notifyDataSetChanged not working...
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AutoJs.getInstance().getScriptEngineService().registerGlobalScriptExecutionListener(mScriptExecutionListener);
        mTimedTaskChangeDisposable = TimedTaskManager.getInstance().getTimeTaskChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskChange);
        mIntentTaskChangeDisposable = TimedTaskManager.getInstance().getIntentTaskChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskChange);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            refresh();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AutoJs.getInstance().getScriptEngineService().unregisterGlobalScriptExecutionListener(mScriptExecutionListener);
        mTimedTaskChangeDisposable.dispose();
        mIntentTaskChangeDisposable.dispose();
    }

    void onTaskChange(ModelChange taskChange) {
        if (taskChange.getAction() == ModelChange.INSERT) {
            mAdapter.notifyChildInserted(1, mPendingTaskGroup.addTask(taskChange.getData()));
        } else if (taskChange.getAction() == ModelChange.DELETE) {
            final int i = mPendingTaskGroup.removeTask(taskChange.getData());
            if (i >= 0) {
                mAdapter.notifyChildRemoved(1, i);
            } else {
                Log.w(LOG_TAG, "data inconsistent on change: " + taskChange);
                refresh();
            }
        } else if (taskChange.getAction() == ModelChange.UPDATE) {
            final int i = mPendingTaskGroup.updateTask(taskChange.getData());
            if (i >= 0) {
                mAdapter.notifyChildChanged(1, i);
            } else {
                refresh();
            }
        }
    }

    private class Adapter extends ExpandableRecyclerAdapter<TaskGroup, Task, TaskGroupViewHolder, TaskViewHolder> {

        public Adapter(@NonNull List<TaskGroup> parentList) {
            super(parentList);
        }

        @NonNull
        @Override
        public TaskGroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            return new TaskGroupViewHolder(LayoutInflater.from(parentViewGroup.getContext())
                    .inflate(R.layout.dialog_code_generate_option_group, parentViewGroup, false));
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.task_list_recycler_view_item, parent, false));
        }

        @Override
        public void onBindParentViewHolder(@NonNull TaskGroupViewHolder viewHolder, int parentPosition, @NonNull TaskGroup taskGroup) {
            viewHolder.title.setText(taskGroup.getTitle());
        }

        @Override
        public void onBindChildViewHolder(@NonNull TaskViewHolder viewHolder, int parentPosition, int childPosition, @NonNull Task task) {
            viewHolder.bind(task);
        }
    }


    class TaskViewHolder extends ChildViewHolder<Task> {

        @BindView(R.id.first_char)
        TextView mFirstChar;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.desc)
        TextView mDesc;

        private Task mTask;
        private GradientDrawable mFirstCharBackground;

        TaskViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onItemClick);
            ButterKnife.bind(this, itemView);
            mFirstCharBackground = (GradientDrawable) mFirstChar.getBackground();
        }

        public void bind(Task task) {
            mTask = task;
            mName.setText(task.getName());
            mDesc.setText(task.getDesc());
            if (AutoFileSource.ENGINE.equals(mTask.getEngineName())) {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_r));
            } else {
                mFirstChar.setText("J");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_j));
            }
        }


        @OnClick(R.id.stop)
        void stop() {
            if (mTask != null) {
                mTask.cancel();
            }
        }

        void onItemClick(View view) {
            if (mTask instanceof Task.PendingTask) {
                Task.PendingTask task = (Task.PendingTask) mTask;
                String extra = task.getTimedTask() == null ? TimedTaskSettingActivity.EXTRA_INTENT_TASK_ID
                        : TimedTaskSettingActivity.EXTRA_TASK_ID;
                TimedTaskSettingActivity_.intent(getContext())
                        .extra(extra, task.getId())
                        .start();
            }
        }
    }

    private class TaskGroupViewHolder extends ParentViewHolder<TaskGroup, Task> {

        TextView title;
        ImageView icon;

        TaskGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(view -> {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            });
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            icon.setRotation(expanded ? -90 : 0);
        }
    }

}