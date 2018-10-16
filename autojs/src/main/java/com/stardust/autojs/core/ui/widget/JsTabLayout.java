package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;

import com.stardust.autojs.runtime.api.UI;

import org.mozilla.javascript.NativeObject;

public class JsTabLayout extends TabLayout {

    public JsTabLayout(Context context) {
        super(context);
    }

    public JsTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
