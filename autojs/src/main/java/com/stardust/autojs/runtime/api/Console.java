package com.stardust.autojs.runtime.api;

import android.support.annotation.Nullable;

import com.stardust.autojs.runtime.JavascriptInterface;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface Console {

    @JavascriptInterface
    void verbose(@Nullable Object data, Object... options);

    @JavascriptInterface
    void log(@Nullable Object data, Object... options);

    @JavascriptInterface
    void info(@Nullable Object data, Object... options);

    @JavascriptInterface
    void warn(@Nullable Object data, Object... options);

    @JavascriptInterface
    void error(@Nullable Object data, Object... options);

    @JavascriptInterface
    void assertTrue(boolean value, @Nullable Object data, Object... options);

    @JavascriptInterface
    void clear();

    @JavascriptInterface
    void show();

    @JavascriptInterface
    void hide();

    void println(int level, CharSequence charSequence);

    void setTitle(CharSequence title);
}
