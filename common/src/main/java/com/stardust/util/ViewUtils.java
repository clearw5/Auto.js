package com.stardust.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by Stardust on 2017/7/2.
 */

public class ViewUtils {

    public static View findParentById(View view, int id) {
        ViewParent parent = view.getParent();
        if (parent == null || !(parent instanceof View))
            return null;
        View viewParent = (View) parent;
        if (viewParent.getId() == id) {
            return viewParent;
        }
        return findParentById(viewParent, id);
    }

    public static float pxToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }
}
