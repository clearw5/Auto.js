package com.stardust.scriptdroid.external.floatingwindow.menu.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.scriptdroid.ui.main.scripts.ScriptListRecyclerView;
import com.stardust.scriptdroid.ui.main.scripts.ScriptListWithProgressBarView;
import com.stardust.widget.ViewHolderSupplier;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class ScriptListNavigatorContent implements NavigatorContent {

    private ViewHolderSupplier<ScriptListRecyclerView.ViewHolder> mViewHolderSupplier = new ViewHolderSupplier<ScriptListRecyclerView.ViewHolder>() {
        @Override
        public ScriptListRecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ScriptListRecyclerView.VIEW_TYPE_FILE:
                    return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_script_list_recycler_view_file, parent, false));
                case ScriptListRecyclerView.VIEW_TYPE_DIRECTORY:
                    return new DirectoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_script_list_recycler_view_directory, parent, false));

            }
            return null;
        }
    };
    private ScriptListRecyclerView mFloatingScriptFileListView;
    private ScriptListWithProgressBarView mScriptListWithProgressBarView;

    public ScriptListNavigatorContent(Context context) {
        mScriptListWithProgressBarView = new ScriptListWithProgressBarView(new ContextThemeWrapper(context, R.style.AppTheme));
        mFloatingScriptFileListView = mScriptListWithProgressBarView.getScriptAndFolderListRecyclerView();
        mFloatingScriptFileListView.setViewHolderSupplier(mViewHolderSupplier);
        mFloatingScriptFileListView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        mFloatingScriptFileListView.setStorageFileProvider(StorageFileProvider.getDefault());
        mFloatingScriptFileListView.setOnItemClickListener(new ScriptListRecyclerView.OnScriptFileClickListener() {

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

    private class DirectoryViewHolder extends ScriptListRecyclerView.ViewHolder {

        DirectoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mFloatingScriptFileListView.getOnItemClickListenerProxy());
            itemView.setOnLongClickListener(mFloatingScriptFileListView.getOnItemLongClickListenerProxy());
        }
    }

    private class FileViewHolder extends DirectoryViewHolder {

        private ImageView mIcon;
        private View mEdit;

        FileViewHolder(final View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mEdit = itemView.findViewById(R.id.edit);
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditActivity.editFile(v.getContext(), getScriptFile());
                    HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new ScriptLoopDialog(v.getContext(), getScriptFile())
                            .windowType(WindowManager.LayoutParams.TYPE_PHONE)
                            .show();
                    HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
                    return true;
                }
            });
        }

        @Override
        public void bind(ScriptFile file) {
            super.bind(file);
            mIcon.setImageResource(file.getType() == ScriptFile.TYPE_AUTO ? R.drawable.record_icon_18
                    : R.drawable.ic_node_js_black);
            mEdit.setVisibility(file.getType() == ScriptFile.TYPE_JAVA_SCRIPT ? View.VISIBLE : View.INVISIBLE);
        }

        private ScriptFile getScriptFile() {
            return mFloatingScriptFileListView.getAdapter().getScriptFileAt(getAdapterPosition());

        }

    }


}