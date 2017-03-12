package com.stardust.scriptdroid.tool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.stardust.scriptdroid.App;

/**
 * Created by Stardust on 2017/3/10.
 */

public class ClipboardTool {


    public static void setClip(CharSequence text) {
        ((ClipboardManager) App.getApp().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", text));
    }
}
