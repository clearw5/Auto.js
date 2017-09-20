package com.stardust.scriptdroid.ui.main.task;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemeColorRecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/3/24.
 */

public class TaskListRecyclerView extends ThemeColorRecyclerView implements ScriptEngineManager.EngineLifecycleCallback {


    private final OnClickListener mOnItemClickListenerProxy = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private final List<ScriptEngine> mScriptEngines = new ArrayList<>();
    private Adapter mAdapter;
    private final ScriptEngineService mScriptEngineService = AutoJs.getInstance().getScriptEngineService();
    private ScriptExecutionListener mScriptExecutionListener = new SimpleScriptExecutionListener() {
        @Override
        public void onStart(final ScriptExecution execution) {
            post(new Runnable() {
                @Override
                public void run() {
                    int position = mScriptEngines.indexOf(execution.getEngine());
                    if (position >= 0) {
                        mAdapter.notifyItemChanged(position);
                    } else {
                        updateEngineList();
                    }
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
                .color(0xffEDEEEF)
                .size(2)
                .marginResId(R.dimen.script_and_folder_list_divider_left_margin, R.dimen.script_and_folder_list_divider_right_margin)
                .showLastDivider()
                .build());
        mAdapter = new Adapter();
        setAdapter(mAdapter);
    }

    public void updateEngineList() {
        mScriptEngines.clear();
        mScriptEngines.addAll(mScriptEngineService.getEngines());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mScriptEngineService.registerEngineLifecycleCallback(this);
        AutoJs.getInstance().getScriptEngineService().registerGlobalScriptExecutionListener(mScriptExecutionListener);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            updateEngineList();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScriptEngineService.unregisterEngineLifecycleCallback(this);
        AutoJs.getInstance().getScriptEngineService().unregisterGlobalScriptExecutionListener(mScriptExecutionListener);
    }

    private void onScriptANR(final ScriptEngine engine) {
        // TODO: 2017/7/19 强制停止aq1sws2
    }

    @Override
    public void onEngineCreate(final ScriptEngine engine) {
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
    public void onEngineRemove(final ScriptEngine engine) {
        post(new Runnable() {
            @Override
            public void run() {
                final int i = mScriptEngines.indexOf(engine);
                if (i >= 0) {
                    mScriptEngines.remove(i);
                    mAdapter.notifyItemRemoved(i);
                } else {
                    updateEngineList();
                }

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
            ScriptEngine engine = mScriptEngines.get(position);
            ScriptSource source = (ScriptSource) mScriptEngines.get(position).getTag(ScriptEngine.TAG_SOURCE);
            holder.bind(source, engine);
        }

        @Override
        public int getItemCount() {
            return mScriptEngines.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.first_char)
        TextView mFirstChar;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.desc)
        TextView mDesc;

        private ScriptEngine mScriptEngine;
        private GradientDrawable mFirstCharBackground;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnItemClickListenerProxy);
            ButterKnife.bind(this, itemView);
            mFirstCharBackground = (GradientDrawable) mFirstChar.getBackground();
        }

        public void bind(ScriptSource source, ScriptEngine engine) {
            mScriptEngine = engine;
            if (source == null)
                return;
            mName.setText(source.getName());
            mDesc.setText(source.toString());
            //ignore android studio warning: use equals to compare string
            if (source.getEngineName() == AutoFileSource.ENGINE) {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_r));
            } else {
                mFirstChar.setText("J");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_j));
            }
        }

        @OnClick(R.id.stop)
        void stop() {
            if (mScriptEngine != null) {
                mScriptEngine.forceStop();
            }
        }
    }

}
