package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.widget.JsGridView;
import com.stardust.autojs.core.ui.widget.JsListView;
import com.stardust.autojs.runtime.ScriptRuntime;

import java.util.Map;

/**
 * Created by Stardust on 2018/3/30.
 */

public class JsGridViewInflater<V extends JsGridView> extends JsListViewInflater<V> {

    public JsGridViewInflater(ResourceParser resourceParser, ScriptRuntime runtime) {
        super(resourceParser, runtime);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "orientation":
                ((GridLayoutManager) view.getLayoutManager()).setOrientation(LinearLayoutInflater.ORIENTATIONS.get(value));
                return true;
            case "spanCount":
                ((GridLayoutManager) view.getLayoutManager()).setSpanCount(Integer.parseInt(value));
                return true;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
    }


    @Nullable
    @Override
    public ViewCreator<? super JsListView> getCreator() {
        return (context, attrs) -> new JsGridView(context, getRuntime());
    }
}
