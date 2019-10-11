package com.stardust.autojs.runtime.api;

import androidx.annotation.Nullable;
import android.util.Log;

import com.stardust.autojs.runtime.exception.ScriptException;

/**
 * Created by Stardust on 2017/5/1.
 */

public abstract class AbstractConsole implements Console {

    public void printf(int level, @Nullable Object data, Object... options) {
        println(level, format(data, options));
    }

    public CharSequence format(@Nullable Object data, Object... options) {
        if (data == null)
            return "\n";
        if (options == null || options.length == 0)
            return data.toString();
        return String.format(data.toString(), options);
    }

    @Override
    public void print(int level, Object data, Object... options) {
        write(level, format(data, options));
    }

    protected abstract void write(int level, CharSequence data);

    @Override
    public void verbose(@Nullable Object data, Object... options) {
        printf(Log.VERBOSE, data, options);
    }

    @Override
    public void log(@Nullable Object data, Object... options) {
        printf(Log.DEBUG, data, options);
    }

    @Override
    public void info(@Nullable Object data, Object... options) {
        printf(Log.INFO, data, options);
    }

    @Override
    public void warn(@Nullable Object data, Object... options) {
        printf(Log.WARN, data, options);
    }

    @Override
    public void error(@Nullable Object data, Object... options) {
        printf(Log.ERROR, data, options);
    }

    @Override
    public void assertTrue(boolean value, @Nullable Object data, Object... options) {
        if (!value) {
            printf(Log.ASSERT, data, options);
            throw new ScriptException(new AssertionError(format(data, options)));
        }
    }


}
