package com.stardust.autojs.runtime.api.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflator;

/**
 * Created by Stardust on 2017/5/14.
 */

public class JsFrameLayout extends FrameLayout {
    public JsFrameLayout(@NonNull Context context) {
        super(context);
    }

    public JsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public View id(String id) {
        return DynamicLayoutInflator.findViewByIdString(this, id);
    }
}
