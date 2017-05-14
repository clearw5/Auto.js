package com.stardust.autojs.runtime.api.ui;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stardust on 2017/5/14.
 */

public class JsViewHelper {

    private View mView;

    public JsViewHelper(View view) {

    }

    public void w(String width) {
        float w = toPixel(width);

    }

    public static float toPixel(String dimen) {
        if (dimen.equals("*")) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if (dimen.equals("auto")) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (Character.isDigit(dimen.charAt(dimen.length() - 1))) {
            return Float.parseFloat(dimen);
        }
        float f = Float.parseFloat(dimen.substring(0, dimen.length() - 2));
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (dimen.endsWith("dp")) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, metrics);
        }
        if (dimen.endsWith("sp")) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, f, metrics);
        }
        throw new IllegalArgumentException(dimen);
    }

    public void h(String height) {

    }

    public void gravity(String gravity) {

    }

    public void align(String alignment) {

    }

    public void padding(String padding) {

    }

    public void margin(String margin) {

    }

    public void bg(String background) {

    }


}
