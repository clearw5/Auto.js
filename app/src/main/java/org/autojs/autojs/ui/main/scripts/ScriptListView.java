package org.autojs.autojs.ui.main.scripts;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
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
import com.stardust.pio.PFiles;
import org.autojs.autojs.R;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.storage.file.StorageFileProvider;
import org.autojs.autojs.ui.build.BuildActivity;
import org.autojs.autojs.ui.build.BuildActivity_;
import org.autojs.autojs.ui.common.ScriptLoopDialog;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.ui.viewmodel.ScriptList;
import org.autojs.autojs.ui.widget.BindableViewHolder;
import org.autojs.autojs.theme.widget.ThemeColorSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/8/21.
 */

public class ScriptListView extends ThemeColorSwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {


    private static final String LOG_TAG = "ScriptListView";

    public interface OnScriptFileClickListener {
        void onScriptFileClick(View view, ScriptFile file);
    }

    public interface OnItemOperatedListener {
        void OnItemOperated(ScriptFile file);
    }

    protected static final int VIEW_TYPE_FILE = 0;
    protected static final int VIEW_TYPE_DIRECTORY = 1;
    //category是类别，也即"文件", "文件夹"那两个
    protected static final int VIEW_TYPE_CATEGORY = 2;

    private static final int positionOfCategoryDir = 0;
    private ScriptList mScriptList = new ScriptList();
    private RecyclerView mScriptListView;
    private ScriptListAdapter mScriptListAdapter = new ScriptListAdapter();
    private ScriptFile mCurrentDirectory;
    protected OnScriptFileClickListener mOnScriptFileClickListener;
    private Function<PFile, Boolean> mFilter;
    private OnItemOperatedListener mOnItemOperatedListener;
    protected ScriptFile mSelectedScriptFile;
    private StorageFileProvider mStorageFileProvider;
    private boolean mDirSortMenuShowing = false;
    private boolean mDirsCollapsed;
    private boolean mFilesCollapsed;
    private int mDirectorySpanSize = 1;

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
        mDirsCollapsed = false;
        mFilesCollapsed = false;
        loadScriptList();
    }

    public void setOnScriptFileClickListener(OnScriptFileClickListener onScriptFileClickListener) {
        mOnScriptFileClickListener = onScriptFileClickListener;
    }

    public void setStorageFileProvider(StorageFileProvider storageFileProvider) {
        setStorageFileProvider(storageFileProvider, new ScriptFile(storageFileProvider.getInitialDirectory()));
    }


    public void setSortConfig(ScriptList.SortConfig sortConfig) {
        mScriptList.setSortConfig(sortConfig);
    }

    public ScriptList.SortConfig getSortConfig() {
        return mScriptList.getSortConfig();
    }

    public void setStorageFileProvider(StorageFileProvider fileProvider, ScriptFile currentDirectory) {
        if (mStorageFileProvider != null)
            mStorageFileProvider.unregisterDirectoryChangeListener(this);
        mStorageFileProvider = fileProvider;
        setCurrentDirectory(currentDirectory);
        mStorageFileProvider.registerDirectoryChangeListener(this);
    }

    public void setOnItemOperatedListener(OnItemOperatedListener onItemOperatedListener) {
        mOnItemOperatedListener = onItemOperatedListener;
    }

    public boolean canGoBack() {
        return !mCurrentDirectory.equals(mStorageFileProvider.getInitialDirectory());
    }

    public void goBack() {
        setCurrentDirectory(mCurrentDirectory.getParentFile());
    }

    public void setDirectorySpanSize(int directorySpanSize) {
        mDirectorySpanSize = directorySpanSize;
    }

    public void setFilter(Function<PFile, Boolean> filter) {
        mFilter = filter;
        reload();
    }

    public void reload() {
        loadScriptList();
    }

    private void init() {
        setOnRefreshListener(this);
        mScriptListView = new RecyclerView(getContext());
        addView(mScriptListView);
        initScriptListRecyclerView();
    }

    private void initScriptListRecyclerView() {
        mScriptListView.setAdapter(mScriptListAdapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //For directories
                if (position > positionOfCategoryDir && position < positionOfCategoryFile()) {
                    return mDirectorySpanSize;
                }
                //For files and category
                return 2;
            }
        });
        mScriptListView.setLayoutManager(manager);
    }

    private int positionOfCategoryFile() {
        if (mDirsCollapsed)
            return 1;
        return mScriptList.directoryCount() + 1;
    }

    private void loadScriptList() {
        setRefreshing(true);
        mStorageFileProvider.getDirectoryFiles(mCurrentDirectory)
                .subscribeOn(Schedulers.io())
                .filter(f -> mFilter == null ? true : mFilter.apply(f))
                .collectInto(mScriptList.cloneConfig(), (list, file) -> {
                    if (file instanceof ScriptFile) {
                        list.add((ScriptFile) file);
                    } else {
                        list.add(new ScriptFile(file));
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnSuccess(ScriptList::sort)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mScriptList = list;
                    mScriptListAdapter.notifyDataSetChanged();
                    setRefreshing(false);
                });
    }

    @Subscribe
    public void onDirectoryChange(StorageFileProvider.DirectoryChangeEvent event) {
        if (!event.getDir().equals(mCurrentDirectory)) {
            return;
        }
        loadScriptList();
    }

    @Override
    public void onRefresh() {
        mStorageFileProvider.notifyDirectoryChanged(mCurrentDirectory);
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
                notifyOperated();
                break;
            case R.id.create_shortcut:
                new ScriptOperations(getContext(), this)
                        .createShortcut(mSelectedScriptFile);
                break;
            case R.id.open_by_other_apps:
                Scripts.openByOtherApps(mSelectedScriptFile);
                notifyOperated();
                break;
            case R.id.send:
                Scripts.send(mSelectedScriptFile);
                notifyOperated();
                break;
            case R.id.timed_task:
                new ScriptOperations(getContext(), this)
                        .timedTask(mSelectedScriptFile);
                notifyOperated();
                break;
            case R.id.action_build_apk:
                BuildActivity_.intent(getContext())
                        .extra(BuildActivity.EXTRA_SOURCE_FILE, mSelectedScriptFile.getPath())
                        .start();
                notifyOperated();
                break;
            case R.id.action_sort_by_date:
                sort(ScriptList.SORT_TYPE_DATE, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_type:
                sort(ScriptList.SORT_TYPE_TYPE, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_name:
                sort(ScriptList.SORT_TYPE_NAME, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_size:
                sort(ScriptList.SORT_TYPE_SIZE, mDirSortMenuShowing);
                break;
            default:
                return false;
        }
        return true;
    }

    protected void notifyOperated() {
        if (mOnItemOperatedListener != null) {
            mOnItemOperatedListener.OnItemOperated(mSelectedScriptFile);
        }
    }

    private void sort(final int sortType, final boolean isDir) {
        setRefreshing(true);
        Observable.fromCallable(() -> {
            if (isDir) {
                mScriptList.sortDir(sortType);
            } else {
                mScriptList.sortFile(sortType);
            }
            return mScriptList;
        })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    mScriptListAdapter.notifyDataSetChanged();
                    setRefreshing(false);
                });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mStorageFileProvider != null)
            mStorageFileProvider.registerDirectoryChangeListener(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mStorageFileProvider.unregisterDirectoryChangeListener(this);
    }


    protected BindableViewHolder<?> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FILE) {
            return new ScriptFileViewHolder(inflater.inflate(R.layout.script_file_list_file, parent, false));
        } else if (viewType == VIEW_TYPE_DIRECTORY) {
            return new DirectoryViewHolder(inflater.inflate(R.layout.script_file_list_directory, parent, false));
        } else {
            return new CategoryViewHolder(inflater.inflate(R.layout.script_file_list_category, parent, false));
        }
    }

    protected RecyclerView getScriptListView() {
        return mScriptListView;
    }

    private class ScriptListAdapter extends RecyclerView.Adapter<BindableViewHolder<?>> {

        @Override
        public BindableViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return ScriptListView.this.onCreateViewHolder(inflater, parent, viewType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(BindableViewHolder<?> holder, int position) {
            int positionOfCategoryFile = positionOfCategoryFile();
            BindableViewHolder bindableViewHolder = (BindableViewHolder) holder;
            if (position == positionOfCategoryDir || position == positionOfCategoryFile) {
                // FIXME: 2017/10/20 java.lang.ClassCastException: java.lang.Boolean cannot be cast to org.autojs.autojs.model.script.ScriptFile
                bindableViewHolder.bind(position == positionOfCategoryDir, position);
                return;
            }
            if (position < positionOfCategoryFile) {
                bindableViewHolder.bind(mScriptList.getDir(position - 1), position);
                return;
            }
            bindableViewHolder.bind(mScriptList.getFile(position - positionOfCategoryFile - 1), position);
        }

        @Override
        public int getItemViewType(int position) {
            int positionOfCategoryFile = positionOfCategoryFile();
            if (position == positionOfCategoryDir || position == positionOfCategoryFile) {
                return VIEW_TYPE_CATEGORY;
            } else if (position < positionOfCategoryFile) {
                return VIEW_TYPE_DIRECTORY;
            } else {
                return VIEW_TYPE_FILE;
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (!mDirsCollapsed) {
                count += mScriptList.directoryCount();
            }
            if (!mFilesCollapsed) {
                count += mScriptList.fileCount();
            }
            return count + 2;
        }
    }

    protected class ScriptFileViewHolder extends BindableViewHolder<ScriptFile> {


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
            mDesc.setText(PFiles.getHumanReadableSize(file.length()));
            if (file.getType() == ScriptFile.TYPE_JAVA_SCRIPT) {
                mFirstChar.setText("J");
                mFirstCharBackground.setColor(ResourcesCompat.getColor(getResources(), R.color.color_j, getContext().getTheme()));
                mEdit.setVisibility(VISIBLE);
            } else {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(ResourcesCompat.getColor(getResources(), R.color.color_r, getContext().getTheme()));
                mEdit.setVisibility(GONE);
            }
        }

        @OnClick(R.id.item)
        void onItemClick() {
            if (mOnScriptFileClickListener != null) {
                mOnScriptFileClickListener.onScriptFileClick(itemView, mScriptFile);
            }
            notifyOperated();
        }

        @OnClick(R.id.run)
        void run() {
            Scripts.run(mScriptFile);
            notifyOperated();
        }

        @OnClick(R.id.edit)
        void edit() {
            Scripts.edit(mScriptFile);
            notifyOperated();
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

    protected class DirectoryViewHolder extends BindableViewHolder<ScriptFile> {

        @BindView(R.id.name)
        public TextView mName;
        @BindView(R.id.more)
        public View mOptions;

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

        @BindView(R.id.order)
        ImageView mSortOrder;

        @BindView(R.id.back)
        ImageView mGoBack;

        @BindView(R.id.collapse)
        ImageView mArrow;

        private boolean mIsDir;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(Boolean isDirCategory, int position) {
            mTitle.setText(isDirCategory ? R.string.text_directory : R.string.text_file);
            mIsDir = isDirCategory;
            if (isDirCategory && canGoBack()) {
                mGoBack.setVisibility(VISIBLE);
            } else {
                mGoBack.setVisibility(GONE);
            }
            if (isDirCategory) {
                mArrow.setRotation(mDirsCollapsed ? -90 : 0);
                mSortOrder.setImageResource(mScriptList.isDirSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
            } else {
                mArrow.setRotation(mFilesCollapsed ? -90 : 0);
                mSortOrder.setImageResource(mScriptList.isFileSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
            }
        }

        @OnClick(R.id.order)
        void changeSortOrder() {
            if (mIsDir) {
                mSortOrder.setImageResource(mScriptList.isDirSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
                mScriptList.setDirSortedAscending(!mScriptList.isDirSortedAscending());
                sort(mScriptList.getDirSortType(), mIsDir);
            } else {
                mSortOrder.setImageResource(mScriptList.isFileSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
                mScriptList.setFileSortedAscending(!mScriptList.isFileSortedAscending());
                sort(mScriptList.getFileSortType(), mIsDir);
            }
        }

        @OnClick(R.id.sort)
        void showSortOptions() {
            PopupMenu popupMenu = new PopupMenu(getContext(), mSort);
            popupMenu.inflate(R.menu.menu_sort_options);
            popupMenu.setOnMenuItemClickListener(ScriptListView.this);
            mDirSortMenuShowing = mIsDir;
            popupMenu.show();

        }

        @OnClick(R.id.back)
        void back() {
            if (canGoBack()) {
                goBack();
            }
        }

        @OnClick(R.id.title_container)
        void collapseOrExpand() {
            if (mIsDir) {
                mDirsCollapsed = !mDirsCollapsed;
            } else {
                mFilesCollapsed = !mFilesCollapsed;
            }
            mScriptListAdapter.notifyDataSetChanged();
        }
    }
}
