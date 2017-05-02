package com.stardust.autojs.runtime;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.api.Console;

/**
 * Created by Stardust on 2017/5/1.
 */

public abstract class AbstractConsole implements Console {

    protected abstract void log(int level, CharSequence charSequence);

    public void println(int level, @Nullable Object data, Object... options) {
        log(level, format(data, options));
    }

    public CharSequence format(@Nullable Object data, Object... options) {
        if (data == null)
            return "\n";
        if (options == null || options.length == 0)
            return data.toString();
        return String.format(data.toString(), options);
    }


    @Override
    public void verbose(@Nullable Object data, Object... options) {
        println(Log.VERBOSE, data, options);
    }

    @Override
    public void log(@Nullable Object data, Object... options) {
        println(Log.DEBUG, data, options);
    }

    @Override
    public void info(@Nullable Object data, Object... options) {
        println(Log.INFO, data, options);
    }

    @Override
    public void warn(@Nullable Object data, Object... options) {
        println(Log.WARN, data, options);
    }

    @Override
    public void error(@Nullable Object data, Object... options) {
        println(Log.ERROR, data, options);
    }

    @Override
    public void assertTrue(boolean value, @Nullable Object data, Object... options) {
        if (!value) {
            println(Log.ASSERT, data, options);
            throw new ScriptStopException(new AssertionError(format(data, options)));
        }
    }

}
