package com.stardust.util;

import android.support.annotation.NonNull;

/**
 * Created by Stardust on 2017/5/3.
 */

public class TextUtils {

    public static String join(CharSequence delimiter, Object... tokens) {
        return android.text.TextUtils.join(delimiter, tokens);
    }


    @NonNull
    public static String toEmptyIfNull(String message) {
        if (message == null)
            return "";
        return message;
    }
}
