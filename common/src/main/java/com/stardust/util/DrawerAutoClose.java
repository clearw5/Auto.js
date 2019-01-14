package com.stardust.util;

import android.app.Activity;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by Stardust on 2017/6/19.
 */

public class DrawerAutoClose implements BackPressedHandler {

    private DrawerLayout mDrawerLayout;
    private int mGravity;

    public DrawerAutoClose(DrawerLayout drawerLayout, int gravity){
        mDrawerLayout = drawerLayout;
        mGravity = gravity;
    }

    @Override
    public boolean onBackPressed(Activity activity) {
        if (mDrawerLayout.isDrawerOpen(mGravity)) {
            mDrawerLayout.closeDrawer(mGravity);
            return true;
        }
        return false;
    }
}