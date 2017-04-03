package com.stardust.scriptdroid.ui.main.script_list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemeColorRecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.scripts.ScriptFileList;
import com.stardust.util.ViewUtil;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptListRecyclerView extends ThemeColorRecyclerView {

    public interface OnItemClickListener {

        void OnItemClick(View v, int position);

    }

    private ScriptFileList mScriptFileList;

    private OnItemClickListener mOnItemClickListener;

    private final OnClickListener mOnItemClickListenerProxy = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.OnItemClick(v, position);
            }
        }
    };

    private final OnClickListener mOnEditIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            onEditIconClick(v, position);
        }
    };

    private final OnClickListener mOnRunIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            //onRunIconClick(v, position);
        }

    };


    public ScriptListRecyclerView(Context context) {
        super(context);
        init();
    }


    public ScriptListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScriptListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void init() {
        setAdapter(new Adapter());
        setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }


    protected void onEditIconClick(View v, int position) {
        ScriptFileOperation.Edit.getInstance().operate(ScriptListRecyclerView.this, mScriptFileList, position);
    }

    public void setScriptFileList(ScriptFileList scriptFileList) {
        mScriptFileList = scriptFileList;
        getAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void showMessage(ScriptFileOperation.ShowMessageEvent event) {
        Snackbar.make(this, event.messageResId, Snackbar.LENGTH_SHORT).show();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.script_list_recycler_view_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ScriptFile scriptFile = mScriptFileList.get(position);
            holder.name.setText(scriptFile.getSimplifiedName());
            holder.path.setText(scriptFile.getSimplifiedPath());
        }


        @Override
        public int getItemCount() {
            return mScriptFileList.size();
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, path;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            path = (TextView) itemView.findViewById(R.id.path);
            ViewUtil.$(itemView, R.id.edit).setOnClickListener(mOnEditIconClickListener);
            ViewUtil.$(itemView, R.id.run).setOnClickListener(mOnRunIconClickListener);
            itemView.setOnClickListener(mOnItemClickListenerProxy);
        }
    }

}
