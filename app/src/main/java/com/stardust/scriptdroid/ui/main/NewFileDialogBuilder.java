package com.stardust.scriptdroid.ui.main;

import android.content.Context;
import android.view.View;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.stardust.scriptdroid.R;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/8/25.
 */

public class NewFileDialogBuilder extends ThemeColorMaterialDialogBuilder {

    @BindView(R.id.layout_new_file)
    ExpandableRelativeLayout mLayoutNewFile;

    @BindView(R.id.layout_import)
    ExpandableRelativeLayout mLayoutImport;

    ExpandableRelativeLayout mExpandedLayout;


    public NewFileDialogBuilder(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.new_file_dialog, null);
        customView(view, true);
        title(R.string.text_new);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        ButterKnife.bind(this, view);
        mExpandedLayout = mLayoutNewFile;
    }

    @OnClick(R.id.option_new_file)
    void expandLayoutNewFile() {
        if (mExpandedLayout != mLayoutNewFile) {
            if(mExpandedLayout != null){
                mExpandedLayout.collapse();
            }
            mLayoutNewFile.expand();
            mExpandedLayout = mLayoutNewFile;
        }
    }


    @OnClick(R.id.option_import)
    void expandLayoutImport() {
        if (mExpandedLayout != mLayoutImport) {
            if(mExpandedLayout != null){
                mExpandedLayout.collapse();
            }
            mLayoutImport.expand();
            mExpandedLayout = mLayoutImport;
        }
    }

}

