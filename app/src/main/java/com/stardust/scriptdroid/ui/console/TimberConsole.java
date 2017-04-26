package com.stardust.scriptdroid.ui.console;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.jraska.console.timber.ConsoleTree;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.scriptdroid.App;


import timber.log.Timber;

/**
 * Created by Stardust on 2017/4/2.
 */

public class TimberConsole implements Console {

    static {
        try {
            Timber.plant(new ConsoleTree.Builder()
                    .minPriority(Log.VERBOSE)
                    .verboseColor(0xff909090)
                    .debugColor(0xffc88b48)
                    .infoColor(0xffc9c9c9)
                    .warnColor(0xffa97db6)
                    .errorColor(0xffff534e)
                    .assertColor(0xffff5540)
                    .build());
        } catch (Exception e) {
            // FIXME: 2017/4/26  java.lang.NoClassDefFoundError: com.jraska.console.timber.ConsoleTree at android4.4
        }
    }

    @Override
    public void i(@Nullable Object o) {
        i(o + "");
    }

    @Override
    public void i(String str) {
        Timber.i(str);
    }

    @Override
    public void e(@Nullable String message) {
        Timber.e(message);
    }

    @Override
    public void e(@Nullable Object o) {
        e(o + "");
    }

    @Override
    public void v(@Nullable String v) {
        Timber.v(v);
    }

    @Override
    public void v(@Nullable Object o) {
        v(o + "");
    }

    @Override
    public void log(@Nullable Object o) {
        log(o + "");
    }

    @Override
    public void log(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        com.jraska.console.Console.writeLine(spannableString);
    }

    @Override
    public void show() {
        App.getApp().startActivity(new Intent(App.getApp(), ConsoleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void clear() {
        com.jraska.console.Console.clear();
    }
}
