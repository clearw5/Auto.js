package com.stardust.autojs.core.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;

import com.stardust.autojs.R;
import com.stardust.autojs.runtime.api.UI;

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
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, this, R.string.text_drawer_open,
                R.string.text_drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private Activity getActivity() {
        Context context = getContext();
        while (!(context instanceof Activity)) {
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        }
        return (Activity) context;
    }

    public void setupWithDrawer(NativeObject object) {
        setupWithDrawer(UI.unwrapJsViewObject(object, DrawerLayout.class));
    }

}
