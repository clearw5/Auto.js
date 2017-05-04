package com.stardust.util;

/**
 * Created by Stardust on 2017/5/3.
 */

public class TextUtils {

    public static String join(CharSequence delimiter, Object... tokens){
        return android.text.TextUtils.join(delimiter, tokens);
    }
}
