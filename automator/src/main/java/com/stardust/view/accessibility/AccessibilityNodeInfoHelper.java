package com.stardust.view.accessibility;

import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/3/6.
 */

public class AccessibilityNodeInfoHelper {

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @param width  pixel width of the display
     * @param height pixel height of the display
     * @return null if node is null, else a Rect containing visible bounds
     */
    public static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height) {
        if (node == null) {
            return null;
        }
        // targeted node's bounds
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);

        Rect displayRect = new Rect();
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.right = width;
        displayRect.bottom = height;
        boolean intersect = nodeRect.intersect(displayRect);
        return nodeRect;
    }

    public static Rect getBoundsInParent(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInParent(rect);
        return rect;
    }

    public static Rect getBoundsInScreen(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return rect;
    }

    public static Rect getBoundsInScreen(AccessibilityNodeInfoCompat nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return rect;
    }

    public static Rect getBoundsInParent(AccessibilityNodeInfoCompat nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInParent(rect);
        return rect;
    }
}
