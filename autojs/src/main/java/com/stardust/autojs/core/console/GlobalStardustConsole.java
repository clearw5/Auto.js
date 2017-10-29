package com.stardust.autojs.core.console;

import android.util.SparseArray;

import com.stardust.util.UiHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stardust on 2017/10/22.
 */

public class GlobalStardustConsole extends StardustConsole {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private final SparseArray<Integer> mColors;

    public GlobalStardustConsole(UiHandler uiHandler) {
        super(uiHandler);
        mColors = ConsoleView.COLORS.clone();
        mColors.put(android.util.Log.DEBUG, 0xcc000000);
    }

    @Override
    public void setConsoleView(ConsoleView consoleView) {
        super.setConsoleView(consoleView);
        consoleView.setColors(mColors);
    }

    @Override
    public String println(int level, CharSequence charSequence) {
        String log = String.format(Locale.getDefault(), "%s/%s: %s",
                DATE_FORMAT.format(new Date()), getLevelChar(level), charSequence.toString());
        super.println(level, log);
        return log;
    }

    private String getLevelChar(int level) {
        switch (level) {
            case android.util.Log.VERBOSE:
                return "V";
            case android.util.Log.DEBUG:
                return "D";
            case android.util.Log.INFO:
                return "I";
            case android.util.Log.WARN:
                return "W";
            case android.util.Log.ERROR:
                return "E";
            case android.util.Log.ASSERT:
                return "A";

        }
        return "";
    }

}
