package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

/**
 * Created by Stardust on 2017/11/4.
 */

public class Strings {


    public static String parse(Context context, String str) {
        if (str.startsWith("@string/")) {
            Resources resources = context.getResources();
            return resources.getString(resources.getIdentifier(str, "string",
                    context.getPackageName()));
        }
        return str;
    }

    public static String parse(View view, String str) {
        return parse(view.getContext(), str);
    }
}
