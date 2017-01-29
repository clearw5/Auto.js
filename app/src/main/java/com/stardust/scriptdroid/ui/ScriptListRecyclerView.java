package com.stardust.scriptdroid.ui;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.tool.ClassTool;

import static com.stardust.scriptdroid.tool.ViewTool.$;

import static com.stardust.scriptdroid.ui.ScriptFileOperation.*;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptListRecyclerView extends RecyclerView {

    private ScriptFileList mScriptFileList;

    private final OnClickListener mOnItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            new ScriptFileOperation.Run().operate(ScriptListRecyclerView.this, mScriptFileList, position);
        }
    };


    private final OnClickListener mOnEditIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            new ScriptFileOperation.Edit().operate(ScriptListRecyclerView.this, mScriptFileList, position);
        }
    };

    private final OnClickListener mOnMoreIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mOperateFileIndex = getChildViewHolder((View) v.getParent()).getAdapterPosition();
            //showOperationDialog(position);
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

    private void init() {
        setAdapter(new Adapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initScriptFileOperationPopupMenu();
    }

    private void initScriptFileOperationPopupMenu() {
        mScriptFileOperationPopupMenu = new ScriptFileOperationPopupMenu(getContext());
        mScriptFileOperationPopupMenu.setOnItemClickListener(new ScriptFileOperationPopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ScriptFileOperation.getOperation(position).operate(ScriptListRecyclerView.this, mScriptFileList, mOperateFileIndex);
                mScriptFileOperationPopupMenu.dismiss();
            }
        });
    }

    public void setScriptFileList(ScriptFileList scriptFileList) {
        mScriptFileList = scriptFileList;
        getAdapter().notifyDataSetChanged();
    }

    private void showOperationDialog(final int position) {
        new MaterialDialog.Builder(getContext()).items(ScriptFileOperation.getOperationNames())
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int operation, CharSequence text) {
                        ScriptFileOperation.getOperation(operation).operate(ScriptListRecyclerView.this, mScriptFileList, position);
                    }
                }).show();
    }

    private void showOrDismissOperationPopupMenu(View v) {
        if (mScriptFileOperationPopupMenu.isShowing()) {
            mScriptFileOperationPopupMenu.dismiss();
        } else {
            mScriptFileOperationPopupMenu.show(v);
        }
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
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, path;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            path = (TextView) itemView.findViewById(R.id.path);
            $(itemView, R.id.edit).setOnClickListener(mOnEditIconClickListener);
            $(itemView, R.id.more).setOnClickListener(mOnMoreIconClickListener);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    static {
        loadScriptFileOperations();
    }

    private static void loadScriptFileOperations() {
        ClassTool.loadClasses(Run.class, Edit.class, Rename.class, CreateShortcut.class, Remove.class, Delete.class);
    }

}
