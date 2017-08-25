package com.stardust.scriptdroid.ui.main;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

/**
 * Created by Stardust on 2017/8/22.
 */

public abstract class ViewPagerFragment extends Fragment {

    protected static final int ROTATION_GONE = -1;

    private int mRotation;
    private FloatingActionButton mFab;
    private View.OnClickListener mOnFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onFabClick((FloatingActionButton) v);
        }
    };

    public ViewPagerFragment(int rotation) {
        mRotation = rotation;
    }

    public void setFab(FloatingActionButton fab) {
        mFab = fab;
    }

    protected abstract void onFabClick(FloatingActionButton fab);

    public void onPageSelected() {
        if (mRotation == ROTATION_GONE) {
            if (mFab.getVisibility() == View.VISIBLE) {
                mFab.hide();
            }
            mFab.setOnClickListener(null);
            return;
        }
        mFab.setOnClickListener(mOnFabClickListener);
        if (mFab.getVisibility() != View.VISIBLE) {
            mFab.setRotation(mRotation);
            mFab.show();
        } else if (Math.abs(mFab.getRotation() - mRotation) > 0.1f) {
            mFab.animate()
                    .rotation(mRotation)
                    .setDuration(300)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        }
    }


}
