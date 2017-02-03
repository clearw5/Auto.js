package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.widget.Toast;

import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/2/3.
 */
public interface BackPressedHandler {

    void onBackPressed();

    class DoublePressExit implements BackPressedHandler {

        private final Activity mActivity;
        private long mLastPressedMillis;
        private long mDoublePressInterval = 1000;

        public DoublePressExit(Activity activity) {
            mActivity = activity;
        }

        public DoublePressExit setDoublePressInterval(long doublePressInterval) {
            mDoublePressInterval = doublePressInterval;
            return this;
        }

        @Override
        public void onBackPressed() {
            if (System.currentTimeMillis() - mLastPressedMillis < mDoublePressInterval) {
                mActivity.finish();
            } else {
                mLastPressedMillis = System.currentTimeMillis();
                Toast.makeText(mActivity, R.string.text_press_again_to_exit, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
