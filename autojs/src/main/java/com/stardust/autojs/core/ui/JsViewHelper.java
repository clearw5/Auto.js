package com.stardust.autojs.core.ui;

import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.util.Ids;


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
