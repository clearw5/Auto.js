package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Strings;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/5.
 */

public class ToolbarInflater<V extends Toolbar> extends BaseViewInflater<V> {


    private static final ValueMapper<Integer> POP_UP_THEMES = new ValueMapper<Integer>("popupTheme")
            .map("dark", R.style.ThemeOverlay_AppCompat_Dark_ActionBar)
            .map("light", R.style.ThemeOverlay_AppCompat_ActionBar);

    public ToolbarInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {

            case "logo":
                view.setLogo(getDrawables().parse(view, value));
                break;
            case "logoDescription":
                view.setLogoDescription(Strings.parse(view, value));
                break;
            case "navigationIcon":
                view.setNavigationContentDescription(Strings.parse(view, value));
                break;
            case "popupTheme":
                view.setPopupTheme(POP_UP_THEMES.get(value));
                break;
            case "subtitle":
                view.setSubtitle(Strings.parse(view, value));
                break;
            case "subtitleTextColor":
                view.setSubtitleTextColor(Colors.parse(view, value));
                break;
            case "titleTextColor":
                view.setTitleTextColor(Colors.parse(view, value));
                break;
            case "title":
                view.setTitle(Strings.parse(view, value));
                break;
            case "titleMargin":
                int margin = Dimensions.parseToIntPixel(value, view);
                view.setTitleMargin(margin, margin, margin, margin);
                break;
            case "titleMarginBottom":
                view.setTitleMarginBottom(Dimensions.parseToIntPixel(value, view));
                break;
            case "titleMarginTop":
                view.setTitleMarginTop(Dimensions.parseToIntPixel(value, view));
                break;
            case "titleMarginStart":
                view.setTitleMarginStart(Dimensions.parseToIntPixel(value, view));
                break;
            case "titleMarginEnd":
                view.setTitleMarginEnd(Dimensions.parseToIntPixel(value, view));
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
        return (ViewCreator<V>) (context, attrs) -> (V) View.inflate(context, R.layout.js_toolbar, null);
    }
}
