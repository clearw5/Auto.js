package com.stardust.scriptdroid.external.floating_window.view;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.external.floating_window.HoverMenuService;
import com.stardust.scriptdroid.tool.ViewTool;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperationPopupMenu;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/12.
 */

public class FloatingScriptFileListView extends RecyclerView {


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

    public FloatingScriptFileListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setAdapter(new Adapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initScriptFileOperationPopupMenu();
    }

    @Subscribe
    public void showMessage(ScriptFileOperation.ShowMessageEvent event) {
        Toast.makeText(getContext(), event.messageResId, Toast.LENGTH_SHORT).show();
    }

    private void initScriptFileOperationPopupMenu() {
        mScriptFileOperationPopupMenu = new ScriptFileOperationPopupMenu(getContext(), getScriptFileOperations());
        mScriptFileOperationPopupMenu.setOnItemClickListener(new ScriptFileOperationPopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, ScriptFileOperation operation) {
                operation.operate(FloatingScriptFileListView.this, mScriptFileList, mOperateFileIndex);
                mScriptFileOperationPopupMenu.dismiss();
                if (!(operation instanceof ScriptFileOperation.Rename))
                    EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
            }
        });
    }

    protected List<ScriptFileOperation> getScriptFileOperations() {
        List<ScriptFileOperation> scriptFileOperations = new ArrayList<>();
        scriptFileOperations.add(new ScriptFileOperation.Run());
        scriptFileOperations.add(new ScriptFileOperation.Rename() {
            @Override
            public void operate(final RecyclerView recyclerView, final ScriptFileList scriptFileList, final int position) {
                String oldName = scriptFileList.get(position).name;
                MaterialDialog dialog = new ThemeColorMaterialDialogBuilder(recyclerView.getContext())
                        .title(R.string.text_rename)
                        .checkBoxPrompt(App.getApp().getString(R.string.text_rename_file_meanwhile), false, null)
                        .input(App.getApp().getString(R.string.text_please_input_new_name), oldName, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                scriptFileList.rename(position, input.toString(), dialog.isPromptCheckBoxChecked());
                                recyclerView.getAdapter().notifyItemChanged(position);
                            }
                        })
                        .build();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        });
        scriptFileOperations.add(new ScriptFileOperation.OpenByOtherApp());
        scriptFileOperations.add(new ScriptFileOperation.CreateShortcut());
        scriptFileOperations.add(new ScriptFileOperation.Remove());
        scriptFileOperations.add(new ScriptFileOperation.Delete());
        return scriptFileOperations;
    }

    protected void onItemClicked(View v, int position) {
        new ScriptFileOperation.Run().operate(this, mScriptFileList, position);
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
    }

    protected void onEditIconClick(View v, int position) {
        new ScriptFileOperation.Edit().operate(this, mScriptFileList, position);
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
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

    @Subscribe
    public void onMenuCollapsing(MessageEvent event) {
        if (event.message.equals(HoverMenuService.MESSAGE_MENU_COLLAPSING) && mScriptFileOperationPopupMenu.isShowing()) {
            mScriptFileOperationPopupMenu.dismiss();
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.floating_script_list_recycler_view_item, parent, false);
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
            ViewTool.$(itemView, R.id.edit).setOnClickListener(mOnEditIconClickListener);
            ViewTool.$(itemView, R.id.more).setOnClickListener(mOnMoreIconClickListener);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
