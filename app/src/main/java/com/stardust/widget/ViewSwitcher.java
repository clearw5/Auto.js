package com.stardust.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Stardust on 2017/3/11.
 */

public class ViewSwitcher extends android.widget.ViewSwitcher {

    private View mCurrentView;

    public ViewSwitcher(Context context) {
        super(context);
    }

    public ViewSwitcher(Context context, View first, View second) {
        super(context);
        addView(first);
        addView(second);
    }

    public ViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public View getCurrentView() {
        return mCurrentView;
    }

    public int getCurrentViewIndex() {
        return mCurrentView == getChildAt(0) ? 0 : 1;
    }

    public void showFirst() {
        ensureCurrentView();
        if (mCurrentView != getChildAt(0)) {
            showPrevious();
            mCurrentView = getChildAt(0);
        }
    }

    private void ensureCurrentView() {
        if (mCurrentView == null) {
            mCurrentView = getChildAt(0);
        }
    }

    public void showSecond() {
        ensureCurrentView();
        if (mCurrentView != getChildAt(1)) {
            showNext();
            mCurrentView = getChildAt(1);
        }
    }

    public void setSecondView(View v) {
        removeViewAt(1);
        addView(v);
    }
}
