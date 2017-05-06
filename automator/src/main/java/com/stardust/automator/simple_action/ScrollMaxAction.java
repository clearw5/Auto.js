package com.stardust.automator.simple_action;

import android.graphics.Rect;
import android.util.Log;

import com.stardust.automator.UiObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ScrollMaxAction extends SimpleAction {

    private static final String TAG = ScrollMaxAction.class.getSimpleName();
    private int mScrollAction;
    private UiObject mMaxScrollableNode;
    private UiObject mRootNode;
    private Set<UiObject> mRecycledMaxUiObjects = new HashSet<>();

    public ScrollMaxAction(int scrollAction) {
        mScrollAction = scrollAction;
    }

    @Override
    public boolean perform(UiObject rootNodeInfo) {
        reset();
        mRootNode = rootNodeInfo;
        findMaxScrollableNodeInfo(rootNodeInfo);
        boolean result = mMaxScrollableNode != null && mMaxScrollableNode.performAction(mScrollAction);
        reset();
        return result;
    }

    private void reset() {
        if (mMaxScrollableNode != null && mMaxScrollableNode != mRootNode) {
            mMaxScrollableNode.recycle();
        }
        mMaxScrollableNode = mRootNode = null;
        mRecycledMaxUiObjects.clear();
    }

    private void findMaxScrollableNodeInfo(UiObject nodeInfo) {
        if (nodeInfo == null)
            return;
        if (nodeInfo.isScrollable()) {
            if (mMaxScrollableNode == null) {
                mMaxScrollableNode = nodeInfo;
            } else if (getAreaInScreen(mMaxScrollableNode) < getAreaInScreen(nodeInfo)) {
                if (mMaxScrollableNode != mRootNode) {
                    mRecycledMaxUiObjects.add(mMaxScrollableNode);
                    mMaxScrollableNode.recycle();
                }
                mMaxScrollableNode = nodeInfo;
            }
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            UiObject child = nodeInfo.child(i);
            if (child != null) {
                findMaxScrollableNodeInfo(child);
                if (mMaxScrollableNode != child && !mRecycledMaxUiObjects.contains(child)) {
                    child.recycle();
                }
            }
        }
    }

    private long getAreaInScreen(UiObject nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        long area = ((long) rect.width()) * rect.height();
        Log.v(TAG, "area=" + area);
        return area;
    }

}
