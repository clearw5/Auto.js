package com.stardust.scriptdroid.ui.main.task;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemeColorRecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.scriptdroid.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/24.
 */

public class TaskListRecyclerView extends ThemeColorRecyclerView implements JavaScriptEngineManager.EngineLifecycleCallback {


    private final OnClickListener mOnItemClickListenerProxy = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private final OnClickListener mOnStopClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            mScriptEngines.get(position).forceStop();
        }
    };

    private final List<JavaScriptEngine> mScriptEngines = new LinkedList<>();
    private Adapter mAdapter;

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
                .color(0xffd9d9d9)
                .size(2)
                .marginResId(R.dimen.script_and_folder_list_divider_left_margin, R.dimen.script_and_folder_list_divider_right_margin)
                .showLastDivider()
                .build());
        mAdapter = new Adapter();
        setAdapter(mAdapter);
    }

    private void updateEngineList() {
        mScriptEngines.clear();
        mScriptEngines.addAll(ScriptEngineService.getInstance().getEngines());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateEngineList();
        mAdapter.notifyDataSetChanged();
        ScriptEngineService.getInstance().registerEngineLifecycleCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ScriptEngineService.getInstance().unregisterEngineLifecycleCallback(this);
    }

    @Override
    public void onEngineCreate(final JavaScriptEngine engine) {
        synchronized (mScriptEngines) {
            post(new Runnable() {
                @Override
                public void run() {
                    mScriptEngines.add(engine);
                    getAdapter().notifyItemInserted(mScriptEngines.size() - 1);
                }
            });
        }
    }

    @Override
    public void onEngineRemove(final JavaScriptEngine engine) {
        post(new Runnable() {
            @Override
            public void run() {
                final int i = mScriptEngines.indexOf(engine);
                mScriptEngines.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.task_list_recycler_view_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(mScriptEngines.get(position).getExecutedScript());
        }

        @Override
        public int getItemCount() {
            return mScriptEngines.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, detail;
        View stop;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnItemClickListenerProxy);
            name = (TextView) itemView.findViewById(R.id.name);
            detail = (TextView) itemView.findViewById(R.id.detail);
            stop = itemView.findViewById(R.id.stop);
            stop.setOnClickListener(mOnStopClickListener);
        }

        public void bind(ScriptSource source) {
            if (source == null)
                return;
            name.setText(source.getName());
            detail.setText(source.toString());
        }
    }

}
