package autojs.runtime.action;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ScrollMaxAction extends Action {

    private static final String TAG = ScrollMaxAction.class.getSimpleName();
    private int mScrollAction;
    private AccessibilityNodeInfo mMaxScrollableNode;
    private AccessibilityNodeInfo mRootNode;

    public ScrollMaxAction(int scrollAction) {
        mScrollAction = scrollAction;
    }

    @Override
    public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
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
    }

    private void findMaxScrollableNodeInfo(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null)
            return;
        if (nodeInfo.isScrollable()) {
            if (mMaxScrollableNode == null) {
                mMaxScrollableNode = nodeInfo;
            } else if (getAreaInScreen(mMaxScrollableNode) < getAreaInScreen(nodeInfo)) {
                if (mMaxScrollableNode != mRootNode)
                    mMaxScrollableNode.recycle();
                mMaxScrollableNode = nodeInfo;
            }
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                findMaxScrollableNodeInfo(child);
                if (mMaxScrollableNode != child) {
                    child.recycle();
                }
            }
        }
    }

    private long getAreaInScreen(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        long area = ((long) rect.width()) * rect.height();
        Log.v(TAG, "area=" + area);
        return area;
    }

}
