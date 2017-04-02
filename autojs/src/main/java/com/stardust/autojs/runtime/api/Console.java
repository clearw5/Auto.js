package com.stardust.autojs.runtime.api;

import android.support.annotation.Nullable;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface Console {

    void i(@Nullable Object o);

    void i(String str);

    void e(String message);

    void e(@Nullable Object o);

    void v(String v);

    void v(@Nullable Object o);

    void log(@Nullable Object o);

    void log(String string);

    void show();

    void clear();

}
