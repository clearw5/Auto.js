package com.stardust.scriptdroid.ui.main.scripts;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.widget.BindableViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/8/21.
 */

public class ScriptListView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {


    public interface OnScriptFileClickListener {
        void onScriptFileClick(View view, ScriptFile file);
    }

    private RecyclerView mScriptListView;
    private ScriptListAdapter mScriptListAdapter = new ScriptListAdapter();
    private ArrayList<ScriptFile> mScriptFiles = new ArrayList<>();
    private ArrayList<ScriptFile> mDirectories = new ArrayList<>();
    private ScriptFile mCurrentDirectory;
    private OnScriptFileClickListener mOnScriptFileClickListener;
    private ScriptFile mSelectedScriptFile;

    public ScriptListView(Context context) {
        super(context);
        init();
    }

    public ScriptListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScriptFile getCurrentDirectory() {
        return mCurrentDirectory;
    }

    public void setCurrentDirectory(ScriptFile currentDirectory) {
        mCurrentDirectory = currentDirectory;
        loadScriptList();
    }

    public void setOnScriptFileClickListener(OnScriptFileClickListener onScriptFileClickListener) {
        mOnScriptFileClickListener = onScriptFileClickListener;
    }

    private void init() {
        setOnRefreshListener(this);
        mScriptListView = new RecyclerView(getContext());
        addView(mScriptListView);
        initScriptListRecyclerView();
        setCurrentDirectory(StorageFileProvider.getDefault().getInitialDirectory());
    }

    private void initScriptListRecyclerView() {
        mScriptListView.setAdapter(mScriptListAdapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //For directories
                if (position >= 1 && position <= mDirectories.size()) {
                    return 1;
                }
                //For files and category
                return 2;
            }
        });
        mScriptListView.setLayoutManager(manager);
    }


    private void loadScriptList() {
        mScriptFiles.clear();
        mDirectories.clear();
        StorageFileProvider.getDefault().getDirectoryScriptFiles(mCurrentDirectory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<ScriptFile>() {

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ScriptFile file) {
                        if (file.isFile()) {
                            mScriptFiles.add(file);
                        } else {
                            mDirectories.add(file);
                        }
                    }

                    @Override
                    public void onComplete() {
                        mScriptListAdapter.notifyDataSetChanged();
                        setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        loadScriptList();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename:
                new ScriptOperations(getContext(), this)
                        .rename(mSelectedScriptFile)
                        .subscribe();
                break;
            case R.id.delete:
                new ScriptOperations(getContext(), this)
                        .delete(mSelectedScriptFile);
                break;
            case R.id.run_repeatedly:
                new ScriptLoopDialog(getContext(), mSelectedScriptFile)
                        .show();
                break;
            case R.id.create_shortcut:
                new ScriptOperations(getContext(), this)
                        .createShortcut(mSelectedScriptFile);
                break;
            case R.id.open_by_other_apps:
                Scripts.openByOtherApps(mSelectedScriptFile);
                break;
            default:
                return false;
        }
        return true;
    }

    private class ScriptListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_FILE = 0;
        private final int VIEW_TYPE_DIRECTORY = 1;
        //category是类别，也即"文件", "文件夹"那两个
        private final int VIEW_TYPE_CATEGORY = 2;


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_FILE) {
                return new ScriptFileViewHolder(inflater.inflate(R.layout.script_file_list_file, parent, false));
            } else if (viewType == VIEW_TYPE_DIRECTORY) {
                return new DirectoryViewHolder(inflater.inflate(R.layout.script_file_list_directory, parent, false));
            } else {
                return new CategoryViewHolder(inflater.inflate(R.layout.script_file_list_category, parent, false));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BindableViewHolder bindableViewHolder = (BindableViewHolder) holder;
            if (position == 0 || position == mDirectories.size() + 1) {
                bindableViewHolder.bind(position == 0, position);
                return;
            }
            if (position <= mDirectories.size()) {
                bindableViewHolder.bind(mDirectories.get(position - 1), position);
                return;
            }
            bindableViewHolder.bind(mScriptFiles.get(position - mDirectories.size() - 2), position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mDirectories.size() + 1) {
                return VIEW_TYPE_CATEGORY;
            }
            if (position <= mDirectories.size()) {
                return VIEW_TYPE_DIRECTORY;
            }
            return VIEW_TYPE_FILE;
        }

        @Override
        public int getItemCount() {
            return mScriptFiles.size() + mDirectories.size() + 2;
        }
    }

    class ScriptFileViewHolder extends BindableViewHolder<ScriptFile> {


        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.first_char)
        TextView mFirstChar;
        @BindView(R.id.desc)
        TextView mDesc;
        @BindView(R.id.more)
        View mOptions;
        @BindView(R.id.edit)
        View mEdit;
        GradientDrawable mFirstCharBackground;
        private ScriptFile mScriptFile;

        ScriptFileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFirstCharBackground = (GradientDrawable) mFirstChar.getBackground();
        }

        @Override
        public void bind(ScriptFile file, int position) {
            mScriptFile = file;
            mName.setText(file.getSimplifiedName());
            mDesc.setText(PFile.getHumanReadableSize(file.length()));
            if (file.getType() == ScriptFile.TYPE_JAVA_SCRIPT) {
                mFirstChar.setText("J");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_j));
                mEdit.setVisibility(VISIBLE);
            } else {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(getResources().getColor(R.color.color_r));
                mEdit.setVisibility(GONE);
            }
        }

        @OnClick(R.id.item)
        void onItemClick() {
            if (mOnScriptFileClickListener != null) {
                mOnScriptFileClickListener.onScriptFileClick(itemView, mScriptFile);
            }
        }

        @OnClick(R.id.run)
        void run() {
            Scripts.run(mScriptFile);
        }

        @OnClick(R.id.edit)
        void edit() {
            Scripts.edit(mScriptFile);
        }

        @OnClick(R.id.more)
        void showOptionMenu() {
            mSelectedScriptFile = mScriptFile;
            PopupMenu popupMenu = new PopupMenu(getContext(), mOptions);
            popupMenu.inflate(R.menu.menu_script_options);
            popupMenu.setOnMenuItemClickListener(ScriptListView.this);
            popupMenu.show();
        }
    }

    class DirectoryViewHolder extends BindableViewHolder<ScriptFile> {

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.more)
        View mOptions;

        private ScriptFile mScriptFile;

        DirectoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(ScriptFile data, int position) {
            mName.setText(data.getSimplifiedName());
            mScriptFile = data;
        }

        @OnClick(R.id.item)
        void onItemClick() {
            setCurrentDirectory(mScriptFile);
        }

        @OnClick(R.id.more)
        void showOptionMenu() {
            mSelectedScriptFile = mScriptFile;
            PopupMenu popupMenu = new PopupMenu(getContext(), mOptions);
            popupMenu.inflate(R.menu.menu_dir_options);
            popupMenu.setOnMenuItemClickListener(ScriptListView.this);
            popupMenu.show();
        }
    }

    class CategoryViewHolder extends BindableViewHolder<Boolean> {

        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.sort)
        ImageView mSort;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(Boolean isDirCategory, int position) {
            mTitle.setText(isDirCategory ? R.string.text_directory : R.string.text_file);
        }

        @OnClick(R.id.order)
        void changeSortOrder() {

        }

        @OnClick(R.id.sort)
        void showSortOptions() {
            PopupMenu popupMenu = new PopupMenu(getContext(), mSort);
            popupMenu.inflate(R.menu.menu_sort_options);
            popupMenu.setOnMenuItemClickListener(ScriptListView.this);
            popupMenu.show();
        }
    }
}
