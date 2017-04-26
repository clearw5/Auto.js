package com.stardust.scriptdroid.tool;

import android.annotation.SuppressLint;
import android.app.Application;

import com.stardust.autojs.script.JsBeautifier;

/**
 * Created by Stardust on 2017/4/18.
 */

public class JsBeautifierFactory {

    @SuppressLint("StaticFieldLeak")
    private static JsBeautifier jsBeautifier;

    public static JsBeautifier getJsBeautify() {
        return jsBeautifier;
    }

    public static void initJsBeautify(Application context, String path) {
        jsBeautifier = new JsBeautifier(context, path);
        jsBeautifier.prepare();
    }
}
