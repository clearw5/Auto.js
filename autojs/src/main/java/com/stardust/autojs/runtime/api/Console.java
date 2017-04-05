package com.stardust.autojs.runtime.api;

import android.support.annotation.Nullable;

import com.stardust.autojs.runtime.JavascriptInterface;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface Console {

    @JavascriptInterface
    void i(@Nullable Object o);

    @JavascriptInterface
    void i(String str);

    @JavascriptInterface
    void e(String message);

    @JavascriptInterface
    void e(@Nullable Object o);

    @JavascriptInterface
    void v(String v);

    @JavascriptInterface
    void v(@Nullable Object o);

    @JavascriptInterface
    void log(@Nullable Object o);

    @JavascriptInterface
    void log(String string);

    @JavascriptInterface
    void show();

    @JavascriptInterface
    void clear();

}
