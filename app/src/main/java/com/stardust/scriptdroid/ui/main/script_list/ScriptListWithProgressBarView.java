package com.stardust.scriptdroid.ui.main.script_list;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.scripts.StorageScriptProvider;

/**
 * Created by Stardust on 2017/4/3.
 */

public class ScriptListWithProgressBarView extends FrameLayout {

    private View mProgressBar;
    private ScriptAndFolderListRecyclerView mScriptAndFolderListRecyclerView;

    public ScriptListWithProgressBarView(@NonNull Context context) {
        super(context);
        init();
    }

    public ScriptListWithProgressBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScriptListWithProgressBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScriptListWithProgressBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.script_and_folder_list_view, this);
        mProgressBar = findViewById(R.id.progressBar);
        mScriptAndFolderListRecyclerView = (ScriptAndFolderListRecyclerView) findViewById(R.id.script_list_recycler_view);
        mScriptAndFolderListRecyclerView.setFileProcessListener(new ScriptAndFolderListRecyclerView.FileProcessListener() {
            @Override
            public void onFilesListing() {
                showProgressBar();
            }

            @Override
            public void onFileListed() {
                hideProgressBar();
            }
        });
    }


    public ScriptAndFolderListRecyclerView getScriptAndFolderListRecyclerView() {
        return mScriptAndFolderListRecyclerView;
    }


    public void setStorageScriptProvider(StorageScriptProvider storageScriptProvider) {
        mScriptAndFolderListRecyclerView.setStorageScriptProvider(storageScriptProvider);
    }

    public void setOnItemClickListener(ScriptAndFolderListRecyclerView.OnScriptFileClickListener onItemClickListener) {
        mScriptAndFolderListRecyclerView.setOnItemClickListener(onItemClickListener);
    }

    public void setScriptFileOperationEnabled(boolean enabled) {
        mScriptAndFolderListRecyclerView.setScriptFileOperationEnabled(enabled);
    }


    public void showProgressBar() {
        mProgressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(GONE);
    }


}
