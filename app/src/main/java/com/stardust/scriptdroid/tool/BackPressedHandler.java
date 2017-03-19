package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.widget.Toast;

import com.stardust.scriptdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/3.
 */
public interface BackPressedHandler {

    boolean onBackPressed();

    class DoublePressExit implements BackPressedHandler {

        private final Activity mActivity;
        private long mLastPressedMillis;
        private long mDoublePressInterval = 1000;

        public DoublePressExit(Activity activity) {
            mActivity = activity;
        }

        public DoublePressExit doublePressInterval(long doublePressInterval) {
            mDoublePressInterval = doublePressInterval;
            return this;
        }

        @Override
        public boolean onBackPressed() {
            if (System.currentTimeMillis() - mLastPressedMillis < mDoublePressInterval) {
                mActivity.finish();
            } else {
                mLastPressedMillis = System.currentTimeMillis();
                Toast.makeText(mActivity, R.string.text_press_again_to_exit, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    class Observer implements BackPressedHandler {

        private List<BackPressedHandler> mBackPressedHandlers = new ArrayList<>();

        @Override
        public boolean onBackPressed() {
            for (BackPressedHandler handler : mBackPressedHandlers) {
                if (handler.onBackPressed()) {
                    return true;
                }
            }
            return false;
        }

        public void registerHandler(BackPressedHandler handler) {
            mBackPressedHandlers.add(handler);
        }
    }
}
