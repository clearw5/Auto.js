package com.stardust.scriptdroid.ui.console;

import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.jraska.console.Console;
import com.jraska.console.timber.ConsoleTree;
import com.stardust.autojs.runtime.AbstractConsole;
import com.stardust.scriptdroid.App;


import timber.log.Timber;

/**
 * Created by Stardust on 2017/4/2.
 */

public class TimberConsole extends AbstractConsole {

    static {
        Timber.plant(new ConsoleTree.Builder()
                .minPriority(Log.VERBOSE)
                .verboseColor(0xff909090)
                .debugColor(0xdf000000)
                .infoColor(0xdf4caf50)
                .warnColor(0xff2196f3)
                .errorColor(0xffff534e)
                .assertColor(0xffff534e)
                .build());
    }

    @Override
    public void println(int level, CharSequence charSequence) {
        if (level == Log.DEBUG) {
            SpannableString spannable = new SpannableString(charSequence);
            spannable.setSpan(new ForegroundColorSpan(0xdd000000), 0, charSequence.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Console.writeLine(spannable);
        } else {
            Timber.log(level, charSequence.toString());
        }
    }

    @Override
    public void clear() {
        com.jraska.console.Console.clear();
    }

    @Override
    public void show() {
        App.getApp().startActivity(new Intent(App.getApp(), ConsoleActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTitle(CharSequence title) {

    }
}
