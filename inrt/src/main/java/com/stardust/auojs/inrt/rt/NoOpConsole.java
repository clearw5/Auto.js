package com.stardust.auojs.inrt.rt;

import android.support.annotation.Nullable;

import com.stardust.autojs.runtime.api.Console;

/**
 * Created by Stardust on 2017/9/21.
 */

public class NoOpConsole implements Console {
    @Override
    public void verbose(@Nullable Object data, Object... options) {

    }

    @Override
    public void log(@Nullable Object data, Object... options) {

    }

    @Override
    public void print(int level, Object data, Object... options) {

    }

    @Override
    public void info(@Nullable Object data, Object... options) {

    }

    @Override
    public void warn(@Nullable Object data, Object... options) {

    }

    @Override
    public void error(@Nullable Object data, Object... options) {

    }

    @Override
    public void assertTrue(boolean value, @Nullable Object data, Object... options) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void println(int level, CharSequence charSequence) {

    }

    @Override
    public void setTitle(CharSequence title) {

    }
}
