package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.stardust.autojs.runtime.ScriptRuntime;

/**
 * Created by Stardust on 2018/3/30.
 */

public class JsGridView extends JsListView {

    public JsGridView(Context context, ScriptRuntime scriptRuntime) {
        super(context, scriptRuntime);
    }

    @Override
    protected void init() {
        super.init();
        setLayoutManager(new GridLayoutManager(getContext(), 1));
    }
}
