package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/4.
 */

public class LinearLayoutInflater<V extends LinearLayout> extends ViewGroupInflater<V> {

    static final ValueMapper<Integer> ORIENTATIONS = new ValueMapper<Integer>("orientation")
            .map("vertical", LinearLayout.VERTICAL)
            .map("horizontal", LinearLayout.HORIZONTAL);

    static final ValueMapper<Integer> SHOW_DIVIDERS = new ValueMapper<Integer>("showDividers")
            .map("beginning", LinearLayout.SHOW_DIVIDER_BEGINNING)
            .map("middle", LinearLayout.SHOW_DIVIDER_MIDDLE)
            .map("end", LinearLayout.SHOW_DIVIDER_END)
            .map("none", LinearLayout.SHOW_DIVIDER_NONE);

    public LinearLayoutInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "baselineAligned":
                view.setBaselineAligned(Boolean.valueOf(value));
                break;
            case "baselineAlignedChildIndex":
                view.setBaselineAlignedChildIndex(Integer.valueOf(value));
                break;
            case "divider":
                view.setDividerDrawable(getDrawables().parse(view, value));
                break;
            case "gravity":
                view.setGravity(Gravities.parse(value));
                break;
            case "measureWithLargestChild":
                view.setMeasureWithLargestChildEnabled(Boolean.valueOf(value));
                break;
            case "orientation":
                view.setOrientation(ORIENTATIONS.get(value));
                break;
            case "showDividers":
                view.setShowDividers(SHOW_DIVIDERS.split(value));
                break;
            case "weightSum":
                view.setWeightSum(Float.valueOf(value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }
}
