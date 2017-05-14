package com.stardust.scriptdroid.ui.console;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;

import com.jraska.console.Console;
import com.stardust.autojs.runtime.api.AbstractConsole;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.sublime_plugin_client.SublimePluginService;
import com.stardust.util.SparseArrayEntries;
import com.stardust.util.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stardust on 2017/4/2.
 */

public class JraskaConsole extends AbstractConsole {


    private static final SparseArray<Integer> COLORS = new SparseArrayEntries<Integer>()
            .entry(Log.VERBOSE, 0xff909090)
            .entry(Log.DEBUG, 0xdf000000)
            .entry(Log.INFO, 0xdf4caf50)
            .entry(Log.WARN, 0xff2196f3)
            .entry(Log.ERROR, 0xffff534e)
            .entry(Log.ASSERT, 0xffff534e)
            .sparseArray();

    private static final SparseArray<String> TAGS = new SparseArrayEntries<String>()
            .entry(Log.VERBOSE, "V")
            .entry(Log.DEBUG, "D")
            .entry(Log.INFO, "I")
            .entry(Log.WARN, "W")
            .entry(Log.ERROR, "E")
            .entry(Log.ASSERT, "A")
            .sparseArray();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS/", Locale.getDefault());

    @Override
    public void println(int level, CharSequence charSequence) {
        Console.write(getLevelSpannable(level, getTag(level)));
        Console.writeLine(getLevelSpannable(level, charSequence));
        SublimePluginService.log(getTag(level) + charSequence.toString());
    }

    @Override
    public void write(int level, CharSequence data) {
        Console.write(getLevelSpannable(level, getTag(level)));
        Console.write(getLevelSpannable(level, data));
        SublimePluginService.log(getTag(level) + data.toString());
    }

    private CharSequence getTag(int level) {
        return TextUtils.join("", DATE_FORMAT.format(new Date()), TAGS.get(level), ": ");
    }

    private SpannableString getLevelSpannable(int level, CharSequence charSequence) {
        SpannableString spannable = new SpannableString(charSequence);
        spannable.setSpan(new ForegroundColorSpan(COLORS.get(level)), 0, charSequence.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public void clear() {
        com.jraska.console.Console.clear();
    }

    @Override
    public void show() {
        App.getApp().startActivity(new Intent(App.getApp(), LogActivity.class)
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
