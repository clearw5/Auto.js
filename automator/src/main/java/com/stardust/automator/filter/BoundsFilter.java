package com.stardust.automator.filter;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoHelper;

/**
 * Created by Stardust on 2017/3/9.
 */

public class BoundsFilter extends DfsFilter {

    public static final int TYPE_EQUALS = 0;
    public static final int TYPE_INSIDE = 1;
    public static final int TYPE_PARENT = 2;

    private Rect mBounds;
    private int mType;

    public BoundsFilter(Rect bounds, int type) {
        mBounds = bounds;
        mType = type;
    }

    @Override
    protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
        if (mType == TYPE_PARENT) {
            return AccessibilityNodeInfoHelper.getBoundsInParent(nodeInfo).equals(mBounds);
        }
        Rect boundsInScreen = AccessibilityNodeInfoHelper.getBoundsInScreen(nodeInfo);
        if (mType == TYPE_EQUALS)
            return boundsInScreen.equals(mBounds);
        return mBounds.contains(boundsInScreen);
    }
}
