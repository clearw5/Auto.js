package com.stardust.autojs.core.ui.inflater.inflaters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;
import com.stardust.autojs.core.ui.widget.JsTabLayout;

import java.util.Map;

public class TabLayoutInflater<V extends TabLayout> extends BaseViewInflater<V> {

    private static final ValueMapper<Integer> TAB_MODES = new ValueMapper<Integer>("tabMode")
            .map("fixed", TabLayout.MODE_FIXED)
            .map("scrollable", TabLayout.MODE_SCROLLABLE);

    public TabLayoutInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "tabGravity":
                view.setTabGravity(Gravities.parse(value));
                break;
            case "tabIndicatorColor":
                view.setSelectedTabIndicatorColor(Colors.parse(view, value));
                break;
            case "tabIndicatorHeight":
                view.setSelectedTabIndicatorHeight(Dimensions.parseToIntPixel(value, view));
                break;
            case "tabMode":
                view.setTabMode(TAB_MODES.get(value));
                break;
            case "tabSelectedTextColor":
                ColorStateList colors = view.getTabTextColors();
                view.setTabTextColors(colors == null ? Color.WHITE : colors.getDefaultColor(),
                        Colors.parse(view, value));
                break;
            case "tabTextColor":
                colors = view.getTabTextColors();
                view.setTabTextColors(Colors.parse(view, value),
                        colors == null ? Color.WHITE : colors.getDefaultColor());
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
        return (ViewCreator<V>) (context, attrs) -> (V) View.inflate(context, R.layout.js_tablayout, null);
    }
}
