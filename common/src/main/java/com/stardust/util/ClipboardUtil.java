package com.stardust.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Created by Stardust on 2017/3/10.
 */

public class ClipboardUtil {


    public static void setClip(Context context, CharSequence text) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", text));
    }

    public static CharSequence getClip(Context context) {
        return ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0).getText();
    }

    @NonNull
    public static CharSequence getClipOrEmpty(Context context) {
        CharSequence clip = getClip(context);
        if (clip == null) {
            return "";
        }
        return clip;
    }
}
