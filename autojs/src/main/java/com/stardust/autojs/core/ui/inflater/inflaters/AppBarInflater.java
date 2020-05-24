package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;

import java.util.Map;

public class AppBarInflater<V extends AppBarLayout> extends BaseViewInflater<V> {

    public AppBarInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "elevation":
                view.setTargetElevation(Dimensions.parseToPixel(value, view));
                break;
            case "expanded":
                view.setExpanded(Boolean.parseBoolean(value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ViewCreator<? super V> getCreator() {
        return (ViewCreator<V>) (context, attrs) -> (V) View.inflate(context, R.layout.js_appbar, null);
    }
}
