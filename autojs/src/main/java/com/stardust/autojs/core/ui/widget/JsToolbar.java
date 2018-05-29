package com.stardust.autojs.core.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;

import com.stardust.autojs.R;

import org.mozilla.javascript.NativeObject;

public class JsToolbar extends Toolbar {

    public JsToolbar(Context context) {
        super(context);
    }

    public JsToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setupWithDrawer(DrawerLayout drawerLayout) {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle((Activity) getContext(), drawerLayout, this, R.string.text_drawer_open,
                R.string.text_drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
    }

    public void setupWithDrawer(NativeObject object) {
        if (object.containsKey("__androidView__")) {
            setupWithDrawer((DrawerLayout) object.get("__androidView__"));
        } else {
            throw new ClassCastException("cannot cast object to DrawerLayout");
        }
    }

}
