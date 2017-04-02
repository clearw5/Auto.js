package com.stardust.util;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/3.
 */
public interface BackPressedHandler {

    boolean onBackPressed(Activity activity);

    class Observer implements BackPressedHandler {

        private List<BackPressedHandler> mBackPressedHandlers = new ArrayList<>();

        @Override
        public boolean onBackPressed(Activity activity) {
            for (BackPressedHandler handler : mBackPressedHandlers) {
                if (handler.onBackPressed(activity)) {
                    return true;
                }
            }
            return false;
        }

        public void registerHandler(BackPressedHandler handler) {
            mBackPressedHandlers.add(handler);
        }
    }


    class DoublePressExit implements BackPressedHandler {

        private final Activity mActivity;
        private long mLastPressedMillis;
        private long mDoublePressInterval = 1000;
        private String mNotice;

        public DoublePressExit(Activity activity, int noticeResId) {
           this(activity, activity.getString(noticeResId));
        }

        public DoublePressExit(Activity activity, String notice) {
            mActivity = activity;
            mNotice = notice;
        }

        public DoublePressExit doublePressInterval(long doublePressInterval) {
            mDoublePressInterval = doublePressInterval;
            return this;
        }

        @Override
        public boolean onBackPressed(Activity activity) {
            if (System.currentTimeMillis() - mLastPressedMillis < mDoublePressInterval) {
                mActivity.finish();
            } else {
                mLastPressedMillis = System.currentTimeMillis();
                Toast.makeText(mActivity, mNotice, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    class DrawerAutoClose implements BackPressedHandler {

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
}
