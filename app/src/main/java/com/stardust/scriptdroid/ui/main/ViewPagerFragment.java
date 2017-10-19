package com.stardust.scriptdroid.ui.main;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

/**
 * Created by Stardust on 2017/8/22.
 */

public abstract class ViewPagerFragment extends Fragment {

    protected static final int ROTATION_GONE = -1;

    private int mFabRotation;
    private FloatingActionButton mFab;
    private View.OnClickListener mOnFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onFabClick((FloatingActionButton) v);
        }
    };

    public ViewPagerFragment(int fabRotation) {
        mFabRotation = fabRotation;
    }

    public void setFab(FloatingActionButton fab) {
        mFab = fab;
    }

    protected abstract void onFabClick(FloatingActionButton fab);

    public void onPageShow() {
        if (mFabRotation == ROTATION_GONE) {
            if (mFab.getVisibility() == View.VISIBLE) {
                mFab.hide();
            }
            mFab.setOnClickListener(null);
            return;
        }
        mFab.setOnClickListener(mOnFabClickListener);
        if (mFab.getVisibility() != View.VISIBLE) {
            mFab.setRotation(mFabRotation);
            mFab.show();
        } else if (Math.abs(mFab.getRotation() - mFabRotation) > 0.1f) {
            mFab.animate()
                    .rotation(mFabRotation)
                    .setDuration(300)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        }
    }


    public void onPageHide() {

    }
}
