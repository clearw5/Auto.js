package com.stardust.scriptdroid.ui.main.my_script_list;

import android.content.Context;
import android.os.Environment;
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

import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.tool.ViewTool;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperationPopupMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptListRecyclerView extends ThemeColorRecyclerView {

    private ScriptFileList mScriptFileList;

    private final OnClickListener mOnItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            onItemClicked(v, position);
        }
    };

    private final OnClickListener mOnEditIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            onEditIconClick(v, position);
        }
    };

    private final OnClickListener mOnMoreIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mOperateFileIndex = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            showOrDismissOperationPopupMenu(v);
        }

    };

    private ScriptFileOperationPopupMenu mScriptFileOperationPopupMenu;
    private int mOperateFileIndex;


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

    public ScriptFileOperationPopupMenu getScriptFileOperationPopupMenu() {
        return mScriptFileOperationPopupMenu;
    }

    private void init() {
        setAdapter(new Adapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initScriptFileOperationPopupMenu();
    }

    private void initScriptFileOperationPopupMenu() {
        mScriptFileOperationPopupMenu = new ScriptFileOperationPopupMenu(getContext(), getScriptFileOperations());
        mScriptFileOperationPopupMenu.setOnItemClickListener(new ScriptFileOperationPopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, ScriptFileOperation operation) {
                operation.operate(ScriptListRecyclerView.this, mScriptFileList, mOperateFileIndex);
                mScriptFileOperationPopupMenu.dismiss();
            }
        });
    }

    protected List<ScriptFileOperation> getScriptFileOperations() {
        List<ScriptFileOperation> scriptFileOperations = new ArrayList<>();
        scriptFileOperations.add(new ScriptFileOperation.Run());
        scriptFileOperations.add(new ScriptFileOperation.Rename());
        scriptFileOperations.add(new ScriptFileOperation.OpenByOtherApp());
        scriptFileOperations.add(new ScriptFileOperation.CreateShortcut());
        scriptFileOperations.add(new ScriptFileOperation.Remove());
        scriptFileOperations.add(new ScriptFileOperation.Delete());
        return scriptFileOperations;
    }

    protected void onItemClicked(View v, int position) {
        new ScriptFileOperation.Run().operate(ScriptListRecyclerView.this, mScriptFileList, position);
    }

    protected void onEditIconClick(View v, int position) {
        new ScriptFileOperation.Edit().operate(ScriptListRecyclerView.this, mScriptFileList, position);
    }

    public void setScriptFileList(ScriptFileList scriptFileList) {
        mScriptFileList = scriptFileList;
        getAdapter().notifyDataSetChanged();
    }

    private void showOrDismissOperationPopupMenu(View v) {
        if (mScriptFileOperationPopupMenu.isShowing()) {
            mScriptFileOperationPopupMenu.dismiss();
        } else {
            mScriptFileOperationPopupMenu.show(v);
        }
    }

    @Subscribe
    public void showMessage(ScriptFileOperation.ShowMessageEvent event) {
        Snackbar.make(this, event.messageResId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
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
            holder.name.setText(scriptFile.name);
            holder.path.setText(trimFilePath(scriptFile.path));
        }

        private final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

        private String trimFilePath(String path) {
            if (path.startsWith(SD_CARD_PATH)) {
                path = path.substring(SD_CARD_PATH.length());
            }
            return path;
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
            ViewTool.$(itemView, R.id.edit).setOnClickListener(mOnEditIconClickListener);
            ViewTool.$(itemView, R.id.more).setOnClickListener(mOnMoreIconClickListener);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

}
