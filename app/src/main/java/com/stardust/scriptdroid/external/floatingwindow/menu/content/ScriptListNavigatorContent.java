package com.stardust.scriptdroid.external.floatingwindow.menu.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageScriptProvider;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.scriptdroid.ui.main.script_list.ScriptAndFolderListRecyclerView;
import com.stardust.scriptdroid.ui.main.script_list.ScriptListWithProgressBarView;
import com.stardust.widget.ViewHolderSupplier;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class ScriptListNavigatorContent implements NavigatorContent {

    private ViewHolderSupplier<ScriptAndFolderListRecyclerView.ViewHolder> mViewHolderSupplier = new ViewHolderSupplier<ScriptAndFolderListRecyclerView.ViewHolder>() {
        @Override
        public ScriptAndFolderListRecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ScriptAndFolderListRecyclerView.VIEW_TYPE_FILE:
                    return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_script_list_recycler_view_file, parent, false));
                case ScriptAndFolderListRecyclerView.VIEW_TYPE_DIRECTORY:
                    return new DirectoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_script_list_recycler_view_directory, parent, false));

            }
            return null;
        }
    };
    private ScriptAndFolderListRecyclerView mFloatingScriptFileListView;
    private ScriptListWithProgressBarView mScriptListWithProgressBarView;

    public ScriptListNavigatorContent(Context context) {
        mScriptListWithProgressBarView = new ScriptListWithProgressBarView(new ContextThemeWrapper(context, R.style.AppTheme));
        mFloatingScriptFileListView = mScriptListWithProgressBarView.getScriptAndFolderListRecyclerView();
        mFloatingScriptFileListView.setViewHolderSupplier(mViewHolderSupplier);
        mFloatingScriptFileListView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        mFloatingScriptFileListView.setStorageScriptProvider(StorageScriptProvider.getDefault());
        mFloatingScriptFileListView.setOnItemClickListener(new ScriptAndFolderListRecyclerView.OnScriptFileClickListener() {

            @Override
            public void onClick(ScriptFile file, int position) {
                Scripts.run(file);
                HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
            }

        });
    }

    @NonNull
    @Override
    public View getView() {
        return mScriptListWithProgressBarView;
    }

    @Override
    public void onShown(@NonNull Navigator navigator) {

    }

    @Override
    public void onHidden() {

    }

    private class DirectoryViewHolder extends ScriptAndFolderListRecyclerView.ViewHolder {

        DirectoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mFloatingScriptFileListView.getOnItemClickListenerProxy());
            itemView.setOnLongClickListener(mFloatingScriptFileListView.getOnItemLongClickListenerProxy());
        }
    }

    private class FileViewHolder extends DirectoryViewHolder {

        FileViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mFloatingScriptFileListView.getChildViewHolder((View) v.getParent()).getAdapterPosition();
                    ScriptFile file = mFloatingScriptFileListView.getAdapter().getScriptFileAt(position);
                    EditActivity.editFile(v.getContext(), file);
                    HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
                }
            });

        }
    }
}