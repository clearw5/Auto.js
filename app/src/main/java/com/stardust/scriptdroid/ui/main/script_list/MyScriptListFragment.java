package com.stardust.scriptdroid.ui.main.script_list;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.common.ScriptLoopDialog;
import com.stardust.widget.AutoAdapter;
import com.stardust.widget.BindableViewHolder;
import com.stardust.widget.ViewHolderSupplier;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/3/13.
 */
@EFragment(R.layout.fragment_my_script_list)
public class MyScriptListFragment extends Fragment {

    public static final String MESSAGE_SCRIPT_FILE_ADDED = "MESSAGE_SCRIPT_FILE_ADDED";

    private static final String TAG = "MyScriptListFragment";

    private static ScriptFile sCurrentDirectory = StorageFileProvider.DEFAULT_DIRECTORY;

    @ViewById(R.id.script_file_list)
    RecyclerView mScriptFileList;

    @ViewById(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ScriptListAdapter mScriptListAdapter = new ScriptListAdapter();
    private ScriptFile mSelectedScriptFile;

    private ArrayList<ScriptFile> mScriptFiles = new ArrayList<>();
    private ArrayList<ScriptFile> mDirectories = new ArrayList<>();

    @AfterViews
    void setUpViews() {
        initScriptListRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadScriptList();
            }
        });
    }

    private void initScriptListRecyclerView() {
        mScriptFileList.setAdapter(mScriptListAdapter);
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
        mScriptFileList.setLayoutManager(manager);
        loadScriptList();
    }

    private void loadScriptList() {
        mScriptFiles.clear();
        mDirectories.clear();
        StorageFileProvider.getDefault().getInitialDirectoryScriptFiles()
                .subscribeOn(Schedulers.computation())
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
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    public static ScriptFile getCurrentDirectory() {
        return sCurrentDirectory;
    }


    private void notifyScriptFileChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StorageFileProvider.getDefault().notifyDirectoryChanged(getCurrentDirectory());
            }
        });
    }

    @Optional
    @OnClick(R.id.loop)
    void runScriptRepeatedly() {
        new ScriptLoopDialog(getActivity(), mSelectedScriptFile)
                .show();
    }

    @Optional
    @OnClick(R.id.rename)
    void renameScriptFile() {
    }


    @Optional
    @OnClick(R.id.open_by_other_apps)
    void openByOtherApps() {
        Scripts.openByOtherApps(mSelectedScriptFile);
        onScriptFileOperated();
    }

    private void onScriptFileOperated() {
        mSelectedScriptFile = null;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Optional
    @OnClick(R.id.create_shortcut)
    void createShortcut() {
        Scripts.createShortcut(mSelectedScriptFile);
        Snackbar.make(getView(), R.string.text_already_create, Snackbar.LENGTH_SHORT).show();
        onScriptFileOperated();
    }

    @Optional
    @OnClick(R.id.delete)
    void deleteScriptFile() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_confirm)
                .positiveText(R.string.cancel)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        doDeletingScriptFile();
                    }
                })
                .show();
    }

    private void doDeletingScriptFile() {
        Observable.fromPublisher(new Publisher<Boolean>() {
            @Override
            public void subscribe(Subscriber<? super Boolean> s) {
                s.onNext(PFile.deleteRecursively(mSelectedScriptFile));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean deleted) throws Exception {
                        showMessage(deleted ? R.string.text_already_delete : R.string.text_delete_failed);
                        notifyScriptFileChanged();
                        onScriptFileOperated();
                    }
                });
    }

    private void showMessage(final int resId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), resId, Snackbar.LENGTH_SHORT).show();
            }
        });
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

    static class ScriptFileViewHolder extends BindableViewHolder<ScriptFile> {

        private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance();

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.first_char)
        TextView mFirstChar;
        @BindView(R.id.desc)
        TextView mDesc;
        GradientDrawable mFirstCharBackground;

        public ScriptFileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFirstCharBackground = (GradientDrawable) mFirstChar.getBackground();
        }

        @Override
        public void bind(ScriptFile file, int position) {
            mName.setText(file.getSimplifiedName());
            mDesc.setText(PFile.getHumanReadableSize(file.length()));
            if (file.getType() == ScriptFile.TYPE_JAVA_SCRIPT) {
                mFirstChar.setText("J");
                //什么？裸写颜色代码？！不影响代码可读性和重构成本的情况下，这样子修改起来方便多啦
                mFirstCharBackground.setColor(0xFF99CC99);
            } else {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(0xFFFD999A);
            }

        }
    }

    static class DirectoryViewHolder extends BindableViewHolder<ScriptFile> {

        @BindView(R.id.name)
        TextView mName;

        public DirectoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(ScriptFile data, int position) {
            mName.setText(data.getSimplifiedName());
        }
    }

    static class CategoryViewHolder extends BindableViewHolder<Boolean> {

        @BindView(R.id.title)
        TextView mTitle;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(Boolean isDirCategory, int position) {
            mTitle.setText(isDirCategory ? R.string.text_directory : R.string.text_file);
        }
    }


}
