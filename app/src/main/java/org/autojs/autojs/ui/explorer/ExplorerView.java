package org.autojs.autojs.ui.explorer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.Explorer;
import org.autojs.autojs.model.explorer.ExplorerChangeEvent;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerFileItem;
import org.autojs.autojs.model.explorer.ExplorerItem;
import org.autojs.autojs.model.explorer.ExplorerPage;
import org.autojs.autojs.model.explorer.ExplorerProjectPage;
import org.autojs.autojs.model.explorer.ExplorerSampleItem;
import org.autojs.autojs.model.explorer.ExplorerSamplePage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.project.BuildActivity;
import org.autojs.autojs.ui.project.BuildActivity_;
import org.autojs.autojs.ui.common.ScriptLoopDialog;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.ui.viewmodel.ExplorerItemList;
import org.autojs.autojs.ui.widget.BindableViewHolder;
import org.autojs.autojs.theme.widget.ThemeColorSwipeRefreshLayout;

import org.autojs.autojs.workground.WrapContentGridLayoutManger;
import org.greenrobot.eventbus.Subscribe;

import java.util.Stack;

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

public class ExplorerView extends ThemeColorSwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private static final String LOG_TAG = "ExplorerView";

    public interface OnItemClickListener {
        void onItemClick(View view, ExplorerItem item);
    }

    public interface OnItemOperatedListener {
        void OnItemOperated(ExplorerItem item);
    }

    protected static final int VIEW_TYPE_ITEM = 0;
    protected static final int VIEW_TYPE_PAGE = 1;
    //category是类别，也即"文件", "文件夹"那两个
    protected static final int VIEW_TYPE_CATEGORY = 2;

    private static final int positionOfCategoryDir = 0;

    private ExplorerItemList mExplorerItemList = new ExplorerItemList();
    private RecyclerView mExplorerItemListView;
    private ExplorerProjectToolbar mProjectToolbar;
    private ExplorerAdapter mExplorerAdapter = new ExplorerAdapter();
    protected OnItemClickListener mOnItemClickListener;
    private Function<ExplorerItem, Boolean> mFilter;
    private OnItemOperatedListener mOnItemOperatedListener;
    protected ExplorerItem mSelectedItem;
    private Explorer mExplorer;
    private Stack<ExplorerPageState> mPageStateHistory = new Stack<>();
    private ExplorerPageState mCurrentPageState = new ExplorerPageState();
    private boolean mDirSortMenuShowing = false;
    private int mDirectorySpanSize = 2;

    public ExplorerView(Context context) {
        super(context);
        init();
    }

    public ExplorerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExplorerPage getCurrentPage() {
        return mCurrentPageState.page;
    }

    public void setRootPage(ExplorerPage page) {
        mPageStateHistory.clear();
        setCurrentPageState(new ExplorerPageState(page));
        loadItemList();
    }

    private void setCurrentPageState(ExplorerPageState currentPageState) {
        mCurrentPageState = currentPageState;
        if (mCurrentPageState.page instanceof ExplorerProjectPage) {
            mProjectToolbar.setVisibility(VISIBLE);
            mProjectToolbar.setProject(currentPageState.page.toScriptFile());
        } else {
            mProjectToolbar.setVisibility(GONE);
        }
    }

    protected void enterDirectChildPage(ExplorerPage childItemGroup) {
        mCurrentPageState.scrollY = ((LinearLayoutManager) mExplorerItemListView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        mPageStateHistory.push(mCurrentPageState);
        setCurrentPageState(new ExplorerPageState(childItemGroup));
        loadItemList();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setSortConfig(ExplorerItemList.SortConfig sortConfig) {
        mExplorerItemList.setSortConfig(sortConfig);
    }

    public ExplorerItemList.SortConfig getSortConfig() {
        return mExplorerItemList.getSortConfig();
    }

    public void setExplorer(Explorer explorer, ExplorerPage rootPage) {
        if (mExplorer != null)
            mExplorer.unregisterChangeListener(this);
        mExplorer = explorer;
        setRootPage(rootPage);
        mExplorer.registerChangeListener(this);
    }

    public void setExplorer(Explorer explorer, ExplorerPage rootPage, ExplorerPage currentPage) {
        if (mExplorer != null)
            mExplorer.unregisterChangeListener(this);
        mExplorer = explorer;
        mPageStateHistory.clear();
        setCurrentPageState(new ExplorerPageState(rootPage));
        mExplorer.registerChangeListener(this);
        enterChildPage(currentPage);
    }

    public void enterChildPage(ExplorerPage childPage) {
        ScriptFile root = mCurrentPageState.page.toScriptFile();
        ScriptFile dir = childPage.toScriptFile();
        Stack<ScriptFile> dirs = new Stack<>();
        while (!dir.equals(root)) {
            dir = dir.getParentFile();
            if (dir == null) {
                break;
            }
            dirs.push(dir);
        }
        ExplorerDirPage parent = null;
        while (!dirs.empty()) {
            dir = dirs.pop();
            ExplorerDirPage dirPage = new ExplorerDirPage(dir, parent);
            mPageStateHistory.push(new ExplorerPageState(dirPage));
            parent = dirPage;
        }
        setCurrentPageState(new ExplorerPageState(childPage));
        loadItemList();
    }

    public void setOnItemOperatedListener(OnItemOperatedListener onItemOperatedListener) {
        mOnItemOperatedListener = onItemOperatedListener;
    }

    public boolean canGoBack() {
        return !mPageStateHistory.empty();
    }

    public void goBack() {
        setCurrentPageState(mPageStateHistory.pop());
        loadItemList();
    }

    public void setDirectorySpanSize(int directorySpanSize) {
        mDirectorySpanSize = directorySpanSize;
    }

    public void setFilter(Function<ExplorerItem, Boolean> filter) {
        mFilter = filter;
        reload();
    }

    public void reload() {
        loadItemList();
    }

    private void init() {
        Log.d(LOG_TAG, "item bg = " + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.item_background)));
        setOnRefreshListener(this);
        inflate(getContext(), R.layout.explorer_view, this);
        mExplorerItemListView = findViewById(R.id.explorer_item_list);
        mProjectToolbar = findViewById(R.id.project_toolbar);
        initExplorerItemListView();
    }

    private void initExplorerItemListView() {
        mExplorerItemListView.setAdapter(mExplorerAdapter);
        WrapContentGridLayoutManger manager = new WrapContentGridLayoutManger(getContext(), 2);
        manager.setDebugInfo("ExplorerView");
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
        mExplorerItemListView.setLayoutManager(manager);
    }

    private int positionOfCategoryFile() {
        if (mCurrentPageState.dirsCollapsed)
            return 1;
        return mExplorerItemList.groupCount() + 1;
    }

    @SuppressLint("CheckResult")
    private void loadItemList() {
        setRefreshing(true);
        mExplorer.fetchChildren(mCurrentPageState.page)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(page -> {
                    mCurrentPageState.page = page;
                    return Observable.fromIterable(page);
                })
                .filter(f -> mFilter == null ? true : mFilter.apply(f))
                .collectInto(mExplorerItemList.cloneConfig(), ExplorerItemList::add)
                .observeOn(Schedulers.computation())
                .doOnSuccess(ExplorerItemList::sort)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mExplorerItemList = list;
                    mExplorerAdapter.notifyDataSetChanged();
                    setRefreshing(false);
                    post(() ->
                            mExplorerItemListView.scrollToPosition(mCurrentPageState.scrollY)
                    );
                });
    }

    @Subscribe
    public void onExplorerChange(ExplorerChangeEvent event) {
        Log.d(LOG_TAG, "on explorer change: " + event);
        if ((event.getAction() == ExplorerChangeEvent.ALL)) {
            loadItemList();
            return;
        }
        String currentDirPath = mCurrentPageState.page.getPath();
        String changedDirPath = event.getPage().getPath();
        ExplorerItem item = event.getItem();
        String changedItemPath = item == null ? null : item.getPath();
        if (currentDirPath.equals(changedItemPath) || (currentDirPath.equals(changedDirPath) &&
                event.getAction() == ExplorerChangeEvent.CHILDREN_CHANGE)) {
            loadItemList();
            return;
        }
        if (currentDirPath.equals(changedDirPath)) {
            int i;
            switch (event.getAction()) {
                case ExplorerChangeEvent.CHANGE:
                    i = mExplorerItemList.update(item, event.getNewItem());
                    if (i >= 0) {
                        mExplorerAdapter.notifyItemChanged(item, i);
                    }
                    break;
                case ExplorerChangeEvent.CREATE:
                    mExplorerItemList.insertAtFront(event.getNewItem());
                    mExplorerAdapter.notifyItemInserted(event.getNewItem(), 0);
                    break;
                case ExplorerChangeEvent.REMOVE:
                    i = mExplorerItemList.remove(item);
                    if (i >= 0) {
                        mExplorerAdapter.notifyItemRemoved(item, i);
                    }
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        mExplorer.notifyChildrenChanged(mCurrentPageState.page);
        mProjectToolbar.refresh();
    }


    public ScriptFile getCurrentDirectory() {
        return getCurrentPage().toScriptFile();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename:
                new ScriptOperations(getContext(), this, getCurrentPage())
                        .rename((ExplorerFileItem) mSelectedItem)
                        .subscribe(Observers.emptyObserver());
                break;
            case R.id.delete:
                new ScriptOperations(getContext(), this, getCurrentPage())
                        .delete(mSelectedItem.toScriptFile());
                break;
            case R.id.run_repeatedly:
                new ScriptLoopDialog(getContext(), mSelectedItem.toScriptFile())
                        .show();
                notifyOperated();
                break;
            case R.id.create_shortcut:
                new ScriptOperations(getContext(), this, getCurrentPage())
                        .createShortcut(mSelectedItem.toScriptFile());
                break;
            case R.id.open_by_other_apps:
                Scripts.INSTANCE.openByOtherApps(mSelectedItem.toScriptFile());
                notifyOperated();
                break;
            case R.id.send:
                Scripts.INSTANCE.send(mSelectedItem.toScriptFile());
                notifyOperated();
                break;
            case R.id.timed_task:
                new ScriptOperations(getContext(), this, getCurrentPage())
                        .timedTask(mSelectedItem.toScriptFile());
                notifyOperated();
                break;
            case R.id.action_build_apk:
                BuildActivity_.intent(getContext())
                        .extra(BuildActivity.EXTRA_SOURCE, mSelectedItem.getPath())
                        .start();
                notifyOperated();
                break;
            case R.id.action_sort_by_date:
                sort(ExplorerItemList.SORT_TYPE_DATE, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_type:
                sort(ExplorerItemList.SORT_TYPE_TYPE, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_name:
                sort(ExplorerItemList.SORT_TYPE_NAME, mDirSortMenuShowing);
                break;
            case R.id.action_sort_by_size:
                sort(ExplorerItemList.SORT_TYPE_SIZE, mDirSortMenuShowing);
                break;
            case R.id.reset:
                Explorers.Providers.workspace().resetSample(mSelectedItem.toScriptFile())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ignored -> {
                            Snackbar.make(this, R.string.text_reset_succeed, Snackbar.LENGTH_SHORT).show();
                        }, Observers.toastMessage());
                break;
            default:
                return false;
        }
        return true;
    }

    protected void notifyOperated() {
        if (mOnItemOperatedListener != null) {
            mOnItemOperatedListener.OnItemOperated(mSelectedItem);
        }
    }

    @SuppressLint("CheckResult")
    private void sort(final int sortType, final boolean isDir) {
        setRefreshing(true);
        Observable.fromCallable(() -> {
            if (isDir) {
                mExplorerItemList.sortItemGroup(sortType);
            } else {
                mExplorerItemList.sortFile(sortType);
            }
            return mExplorerItemList;
        })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    mExplorerAdapter.notifyDataSetChanged();
                    setRefreshing(false);
                });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mExplorer != null)
            mExplorer.registerChangeListener(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mExplorer.unregisterChangeListener(this);
    }


    protected BindableViewHolder<?> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ExplorerItemViewHolder(inflater.inflate(R.layout.script_file_list_file, parent, false));
        } else if (viewType == VIEW_TYPE_PAGE) {
            return new ExplorerPageViewHolder(inflater.inflate(R.layout.script_file_list_directory, parent, false));
        } else {
            return new CategoryViewHolder(inflater.inflate(R.layout.script_file_list_category, parent, false));
        }
    }

    protected RecyclerView getExplorerItemListView() {
        return mExplorerItemListView;
    }

    private class ExplorerAdapter extends RecyclerView.Adapter<BindableViewHolder<?>> {

        @Override
        public BindableViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return ExplorerView.this.onCreateViewHolder(inflater, parent, viewType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(BindableViewHolder<?> holder, int position) {
            int positionOfCategoryFile = positionOfCategoryFile();
            BindableViewHolder bindableViewHolder = (BindableViewHolder) holder;
            if (position == positionOfCategoryDir || position == positionOfCategoryFile) {
                bindableViewHolder.bind(position == positionOfCategoryDir, position);
                return;
            }
            if (position < positionOfCategoryFile) {
                bindableViewHolder.bind(mExplorerItemList.getItemGroup(position - 1), position);
                return;
            }
            bindableViewHolder.bind(mExplorerItemList.getItem(position - positionOfCategoryFile - 1), position);
        }

        @Override
        public int getItemViewType(int position) {
            int positionOfCategoryFile = positionOfCategoryFile();
            if (position == positionOfCategoryDir || position == positionOfCategoryFile) {
                return VIEW_TYPE_CATEGORY;
            } else if (position < positionOfCategoryFile) {
                return VIEW_TYPE_PAGE;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }

        int getItemPosition(ExplorerItem item, int i) {
            if (item instanceof ExplorerPage) {
                return i + positionOfCategoryDir + 1;
            }
            return i + positionOfCategoryFile() + 1;
        }

        public void notifyItemChanged(ExplorerItem item, int i) {
            notifyItemChanged(getItemPosition(item, i));
        }

        public void notifyItemRemoved(ExplorerItem item, int i) {
            notifyItemRemoved(getItemPosition(item, i));
        }

        public void notifyItemInserted(ExplorerItem item, int i) {
            notifyItemInserted(getItemPosition(item, i));
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (!mCurrentPageState.dirsCollapsed) {
                count += mExplorerItemList.groupCount();
            }
            if (!mCurrentPageState.filesCollapsed) {
                count += mExplorerItemList.itemCount();
            }
            return count + 2;
        }
    }

    protected class ExplorerItemViewHolder extends BindableViewHolder<ExplorerItem> {

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
        @BindView(R.id.run)
        View mRun;

        GradientDrawable mFirstCharBackground;
        private ExplorerItem mExplorerItem;

        ExplorerItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFirstCharBackground = (GradientDrawable) mFirstChar.getBackground();
        }

        @Override
        public void bind(ExplorerItem item, int position) {
            mExplorerItem = item;
            mName.setText(ExplorerViewHelper.getDisplayName(item));
            mDesc.setText(PFiles.getHumanReadableSize(item.getSize()));
            mFirstChar.setText(ExplorerViewHelper.getIconText(item));
            mFirstCharBackground.setColor(ExplorerViewHelper.getIconColor(item));
            mEdit.setVisibility(item.isEditable() ? VISIBLE : GONE);
            mRun.setVisibility(item.isExecutable() ? VISIBLE : GONE);
        }

        @OnClick(R.id.item)
        void onItemClick() {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemView, mExplorerItem);
            }
            notifyOperated();
        }

        @OnClick(R.id.run)
        void run() {
            Scripts.INSTANCE.run(new ScriptFile(mExplorerItem.getPath()));
            notifyOperated();
        }

        @OnClick(R.id.edit)
        void edit() {
            Scripts.INSTANCE.edit(getContext(), new ScriptFile(mExplorerItem.getPath()));
            notifyOperated();
        }

        @OnClick(R.id.more)
        void showOptionMenu() {
            mSelectedItem = mExplorerItem;
            PopupMenu popupMenu = new PopupMenu(getContext(), mOptions);
            popupMenu.inflate(R.menu.menu_script_options);
            Menu menu = popupMenu.getMenu();
            if (!mExplorerItem.isExecutable()) {
                menu.removeItem(R.id.run_repeatedly);
                menu.removeItem(R.id.more);
            }
            if (!mExplorerItem.canDelete()) {
                menu.removeItem(R.id.delete);
            }
            if (!mExplorerItem.canRename()) {
                menu.removeItem(R.id.rename);
            }
            if (!(mExplorerItem instanceof ExplorerSampleItem)) {
                menu.removeItem(R.id.reset);
            }
            popupMenu.setOnMenuItemClickListener(ExplorerView.this);
            popupMenu.show();
        }
    }

    protected class ExplorerPageViewHolder extends BindableViewHolder<ExplorerPage> {

        @BindView(R.id.name)
        public TextView mName;

        @BindView(R.id.more)
        public View mOptions;

        @BindView(R.id.icon)
        public ImageView mIcon;

        private ExplorerPage mExplorerPage;

        ExplorerPageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(ExplorerPage data, int position) {
            mName.setText(ExplorerViewHelper.getDisplayName(data));
            mIcon.setImageResource(ExplorerViewHelper.getIcon(data));
            mOptions.setVisibility(data instanceof ExplorerSamplePage ? GONE : VISIBLE);
            mExplorerPage = data;

        }

        @OnClick(R.id.item)
        void onItemClick() {
            enterDirectChildPage(mExplorerPage);
        }

        @OnClick(R.id.more)
        void showOptionMenu() {
            mSelectedItem = mExplorerPage;
            PopupMenu popupMenu = new PopupMenu(getContext(), mOptions);
            popupMenu.inflate(R.menu.menu_dir_options);
            popupMenu.setOnMenuItemClickListener(ExplorerView.this);
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
                mArrow.setRotation(mCurrentPageState.dirsCollapsed ? -90 : 0);
                mSortOrder.setImageResource(mExplorerItemList.isDirSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
            } else {
                mArrow.setRotation(mCurrentPageState.filesCollapsed ? -90 : 0);
                mSortOrder.setImageResource(mExplorerItemList.isFileSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
            }
        }

        @OnClick(R.id.order)
        void changeSortOrder() {
            if (mIsDir) {
                mSortOrder.setImageResource(mExplorerItemList.isDirSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
                mExplorerItemList.setDirSortedAscending(!mExplorerItemList.isDirSortedAscending());
                sort(mExplorerItemList.getDirSortType(), mIsDir);
            } else {
                mSortOrder.setImageResource(mExplorerItemList.isFileSortedAscending() ?
                        R.drawable.ic_ascending_order : R.drawable.ic_descending_order);
                mExplorerItemList.setFileSortedAscending(!mExplorerItemList.isFileSortedAscending());
                sort(mExplorerItemList.getFileSortType(), mIsDir);
            }
        }

        @OnClick(R.id.sort)
        void showSortOptions() {
            PopupMenu popupMenu = new PopupMenu(getContext(), mSort);
            popupMenu.inflate(R.menu.menu_sort_options);
            popupMenu.setOnMenuItemClickListener(ExplorerView.this);
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
                mCurrentPageState.dirsCollapsed = !mCurrentPageState.dirsCollapsed;
            } else {
                mCurrentPageState.filesCollapsed = !mCurrentPageState.filesCollapsed;
            }
            mExplorerAdapter.notifyDataSetChanged();
        }
    }

    private static class ExplorerPageState {

        ExplorerPage page;

        boolean dirsCollapsed;

        boolean filesCollapsed;

        int scrollY;

        ExplorerPageState() {
        }

        ExplorerPageState(ExplorerPage page) {
            this.page = page;
        }
    }
}
