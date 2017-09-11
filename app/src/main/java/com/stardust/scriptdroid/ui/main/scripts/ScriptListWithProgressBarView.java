package com.stardust.scriptdroid.ui.main.scripts;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.StorageFileProvider;

/**
 * Created by Stardust on 2017/4/3.
 */

public class ScriptListWithProgressBarView extends FrameLayout {

    private ScriptListRecyclerView mScriptAndFolderListRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mScriptAndFolderListRecyclerView = (ScriptListRecyclerView) findViewById(R.id.script_list_recycler_view);
        mScriptAndFolderListRecyclerView.setFileProcessListener(new ScriptListRecyclerView.FileProcessListener() {
            @Override
            public void onFilesListing() {
                showProgressBar();
            }

            @Override
            public void onFileListed() {
                hideProgressBar();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScriptAndFolderListRecyclerView.getStorageFileProvider().refreshAll();
            }
        });
    }


    public ScriptListRecyclerView getScriptAndFolderListRecyclerView() {
        return mScriptAndFolderListRecyclerView;
    }


    public void setStorageScriptProvider(StorageFileProvider storageFileProvider) {
        mScriptAndFolderListRecyclerView.setStorageFileProvider(storageFileProvider);
    }

    public void setOnItemClickListener(ScriptListRecyclerView.OnScriptFileClickListener onItemClickListener) {
        mScriptAndFolderListRecyclerView.setOnItemClickListener(onItemClickListener);
    }

    public void setScriptFileOperationEnabled(boolean enabled) {
        mScriptAndFolderListRecyclerView.setScriptFileOperationEnabled(enabled);
    }


    public void showProgressBar() {
        mSwipeRefreshLayout.setRefreshing(true);
        mScriptAndFolderListRecyclerView.setEnabled(false);
    }

    public void hideProgressBar() {
        mSwipeRefreshLayout.setRefreshing(false);
        mScriptAndFolderListRecyclerView.setEnabled(true);
    }


    public void setCurrentDirectory(ScriptFile directory) {
        mScriptAndFolderListRecyclerView.setCurrentDirectory(directory);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        //in some phones, SwipeRefreshLayout will keep refreshing after screen orientation change
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
