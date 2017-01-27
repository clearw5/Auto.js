package com.stardust.scriptdroid.tool;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by Stardust on 2017/1/24.
 */

public class ViewTool {

    @SuppressWarnings("unchecked")
    public static <V extends View> V $(View view, @IdRes int resId) {
        return (V) view.findViewById(resId);
    }
}
