package com.stardust.automator.filter;

import android.graphics.Rect;

import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.AccessibilityNodeInfoHelper;

import java.util.Locale;

/**
 * Created by Stardust on 2017/3/9.
 */

public class BoundsFilter extends DfsFilter {

    public static final int TYPE_EQUALS = 0;
    public static final int TYPE_INSIDE = 1;
    public static final int TYPE_CONTAINS = 2;

    private Rect mBounds;
    private int mType;

    public BoundsFilter(Rect bounds, int type) {
        mBounds = bounds;
        mType = type;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        if (mType == TYPE_CONTAINS) {
            return AccessibilityNodeInfoHelper.getBoundsInScreen(nodeInfo).contains(mBounds);
        }
        Rect boundsInScreen = AccessibilityNodeInfoHelper.getBoundsInScreen(nodeInfo);
        if (mType == TYPE_EQUALS)
            return boundsInScreen.equals(mBounds);
        return mBounds.contains(boundsInScreen);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "bounds%s(%d, %d, %d, %d)", mType == TYPE_EQUALS ? "" :
                        mType == TYPE_INSIDE ? "Inside" : "Contains",
                mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
    }
}
