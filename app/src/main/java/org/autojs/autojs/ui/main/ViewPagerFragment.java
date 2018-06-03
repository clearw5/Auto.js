package org.autojs.autojs.ui.main;

import android.support.annotation.CallSuper;
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
    private boolean mShown;
    private View.OnClickListener mOnFabClickListener = v -> onFabClick((FloatingActionButton) v);

    public ViewPagerFragment(int fabRotation) {
        mFabRotation = fabRotation;
    }

    public void setFab(FloatingActionButton fab) {
        mFab = fab;
    }

    protected abstract void onFabClick(FloatingActionButton fab);

    @CallSuper
    public void onPageShow() {
        mShown = true;
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


    @CallSuper
    public void onPageHide() {
        mShown = false;
    }

    public boolean isShown() {
        return mShown;
    }
}
