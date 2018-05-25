package org.autojs.autojs.ui.main.sample;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stardust.pio.PFiles;
import org.autojs.autojs.R;
import org.autojs.autojs.storage.file.SampleFileProvider;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.ui.main.scripts.ScriptListView;
import org.autojs.autojs.ui.widget.BindableViewHolder;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/10/28.
 */

public class SampleListView extends ScriptListView {
    public SampleListView(Context context) {
        super(context);
    }

    public SampleListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy_to_my_script:
                new ScriptOperations(getContext(), this)
                        .importFile(mSelectedScriptFile.getPath())
                        .subscribe();
                return true;
            case R.id.reset:
                resetSample();
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    private void resetSample() {
        String samplePath = new File(getCurrentDirectory(), mSelectedScriptFile.getName()).getPath();
        if (SampleFileProvider.copySample(getContext(), samplePath, mSelectedScriptFile.getPath())) {
            Snackbar.make(this, R.string.text_reset_succeed, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(this, R.string.text_reset_fail, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected BindableViewHolder<?> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FILE) {
            return new SampleFileViewHolder(inflater.inflate(R.layout.script_file_list_file, parent, false));
        } else if (viewType == VIEW_TYPE_DIRECTORY) {
            DirectoryViewHolder viewHolder = (DirectoryViewHolder) super.onCreateViewHolder(inflater, parent, viewType);
            viewHolder.mOptions.setVisibility(GONE);
            return viewHolder;
        }
        return super.onCreateViewHolder(inflater, parent, viewType);
    }

    class SampleFileViewHolder extends BindableViewHolder<ScriptFile> {

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

        SampleFileViewHolder(View itemView) {
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
            popupMenu.inflate(R.menu.menu_sample_options);
            popupMenu.setOnMenuItemClickListener(SampleListView.this);
            popupMenu.show();
        }
    }

}
