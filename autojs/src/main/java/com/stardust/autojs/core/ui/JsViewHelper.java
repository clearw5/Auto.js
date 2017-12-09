package com.stardust.autojs.core.ui;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import org.autojs.dynamiclayoutinflater.util.Ids;


/**
 * Created by Stardust on 2017/5/14.
 */

public class JsViewHelper {

    @Nullable
    public static View findViewByStringId(View view, String id) {
        View result = view.findViewById(Ids.parse(id));
        if (result != null)
            return result;
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) {
            result = findViewByStringId(group.getChildAt(i), id);
            if (result != null)
                return result;
        }
        return null;
    }
}
