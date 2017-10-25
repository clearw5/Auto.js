package com.stardust.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Stardust on 2017/8/20.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    private static final long DURATION = 200;
    private static final TimeInterpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mHidden = false;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0) {
            onScrollDown(coordinatorLayout, child);
        } else if (dyConsumed < 0) {
            onScrollUp(coordinatorLayout, child);
        }
    }

    private void onScrollUp(CoordinatorLayout parent, FloatingActionButton button) {
        if (!mHidden) {
            return;
        }
        startShowingAnimation(parent, button);
    }

    private void startShowingAnimation(CoordinatorLayout parent, FloatingActionButton button) {
        button.animate()
                .translationY(0)
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mHidden = false;
                    }
                })
                .start();
    }

    private void onScrollDown(CoordinatorLayout parent, FloatingActionButton button) {
        if (mHidden) {
            return;
        }
        startHidingAnimation(parent, button);
    }

    private void startHidingAnimation(CoordinatorLayout parent, FloatingActionButton button) {
        button.animate()
                .translationY(parent.getY() + parent.getHeight() - button.getY())
                .setDuration(DURATION)
                .setInterpolator(INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mHidden = true;
                    }
                })
                .start();
    }


}
