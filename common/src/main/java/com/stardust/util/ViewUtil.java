package com.stardust.util;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by Stardust on 2017/1/24.
 */

public class ViewUtil {

    @SuppressWarnings("unchecked")
    public static <V extends View> V $(View view, @IdRes int resId) {
        return (V) view.findViewById(resId);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
