package com.stardust.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;


/**
 * Created by Stardust on 2017/3/10.
 */

public class ClipboardUtil {


    public static void setClip(Context context, CharSequence text) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", text));
    }
}
