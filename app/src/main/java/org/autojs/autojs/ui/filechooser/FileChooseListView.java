package org.autojs.autojs.ui.filechooser;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stardust.pio.PFile;
import com.stardust.pio.PFiles;
import org.autojs.autojs.R;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.ui.main.scripts.ScriptListView;
import org.autojs.autojs.ui.widget.BindableViewHolder;
import org.autojs.autojs.ui.widget.CheckBoxCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/10/19.
 */

public class FileChooseListView extends ScriptListView {

    private int mMaxChoice = 1;
    private LinkedHashMap<PFile, Integer> mSelectedFiles = new LinkedHashMap<>();
    private boolean mCanChooseDir = false;

    public FileChooseListView(Context context) {
        super(context);
        init();
    }

    public FileChooseListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMaxChoice(int maxChoice) {
        mMaxChoice = maxChoice;
    }

    public void setCanChooseDir(boolean canChooseDir) {
        mCanChooseDir = canChooseDir;
    }

    public List<PFile> getSelectedFiles() {
        ArrayList<PFile> list = new ArrayList<>(mSelectedFiles.size());
        for (Map.Entry<PFile, Integer> entry : mSelectedFiles.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    private void init() {
        setDirectorySpanSize(2);
        ((SimpleItemAnimator) getScriptListView().getItemAnimator())
                .setSupportsChangeAnimations(false);
    }

    @Override
    protected BindableViewHolder<?> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FILE) {
            return new ScriptFileViewHolder(inflater.inflate(R.layout.file_choose_list_file, parent, false));
        } else if (viewType == VIEW_TYPE_DIRECTORY) {
            return new DirectoryViewHolder(inflater.inflate(R.layout.file_choose_list_directory, parent, false));
        } else {
            return super.onCreateViewHolder(inflater, parent, viewType);
        }
    }

    private void check(ScriptFile file, int position) {
        if (mSelectedFiles.size() == mMaxChoice) {
            Map.Entry<PFile, Integer> itemToUncheck = mSelectedFiles.entrySet().iterator().next();
            int positionOfItemToUncheck = itemToUncheck.getValue();
            mSelectedFiles.remove(itemToUncheck.getKey());
            getScriptListView().getAdapter().notifyItemChanged(positionOfItemToUncheck);
        }
        mSelectedFiles.put(file, position);
    }


    class ScriptFileViewHolder extends BindableViewHolder<ScriptFile> {

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.first_char)
        TextView mFirstChar;
        @BindView(R.id.checkbox)
        CheckBoxCompat mCheckBox;
        @BindView(R.id.desc)
        TextView mDesc;
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
            mCheckBox.setChecked(mSelectedFiles.containsKey(file), false);
            if (file.getType() == ScriptFile.TYPE_JAVA_SCRIPT) {
                mFirstChar.setText("J");
                mFirstCharBackground.setColor(ResourcesCompat.getColor(getResources(), R.color.color_j, getContext().getTheme()));
            } else {
                mFirstChar.setText("R");
                mFirstCharBackground.setColor(ResourcesCompat.getColor(getResources(), R.color.color_r, getContext().getTheme()));
            }
        }

        @OnClick(R.id.item)
        void onItemClick() {
            mCheckBox.toggle();
        }

        @OnCheckedChanged(R.id.checkbox)
        void onCheckedChanged() {
            if (mCheckBox.isChecked()) {
                check(mScriptFile, getAdapterPosition());
            } else {
                mSelectedFiles.remove(mScriptFile);
            }
        }


    }

    class DirectoryViewHolder extends BindableViewHolder<ScriptFile> {

        @BindView(R.id.name)
        TextView mName;

        @BindView(R.id.checkbox)
        CheckBoxCompat mCheckBox;

        private ScriptFile mScriptFile;

        DirectoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBox.setVisibility(mCanChooseDir ? VISIBLE : GONE);
        }

        @Override
        public void bind(ScriptFile data, int position) {
            mName.setText(data.getSimplifiedName());
            mScriptFile = data;
            if (mCanChooseDir) {
                mCheckBox.setChecked(mSelectedFiles.containsKey(data), false);
            }
        }

        @OnClick(R.id.item)
        void onItemClick() {
            setCurrentDirectory(mScriptFile);
        }

        @OnCheckedChanged(R.id.checkbox)
        void onCheckedChanged() {
            if (mCheckBox.isChecked()) {
                check(mScriptFile, getAdapterPosition());
            } else {
                mSelectedFiles.remove(mScriptFile);
            }
        }

    }

}
