package com.stardust.scriptdroid.layout_inspector;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/10.
 */

public class NodeInfo {

    private List<NodeInfo> children = new ArrayList<>();

    public String id;
    public CharSequence contentDesc, className, packageName, text;
    public int drawingOrder;
    public boolean accessibilityFocused, checked, clickable, contextClickable, dismissable, editable, enabled,
            focusable, longClickable, selected, scrollable, visibleToUser;
    public String bounds;

    private Rect mBoundsInScreen;

    public NodeInfo(AccessibilityNodeInfoCompat node) {
        id = simplifyId(node.getViewIdResourceName());
        contentDesc = node.getContentDescription();
        className = node.getClassName();
        packageName = node.getPackageName();
        text = node.getText();

        drawingOrder = node.getDrawingOrder();

        accessibilityFocused = node.isAccessibilityFocused();
        checked = node.isChecked();
        clickable = node.isClickable();
        contextClickable = node.isContextClickable();
        dismissable = node.isDismissable();
        enabled = node.isEnabled();
        editable = node.isEditable();
        focusable = node.isFocusable();
        longClickable = node.isLongClickable();
        selected = node.isSelected();
        scrollable = node.isScrollable();
        visibleToUser = node.isVisibleToUser();

        mBoundsInScreen = new Rect();
        node.getBoundsInScreen(mBoundsInScreen);
        bounds = boundsToString(mBoundsInScreen);
    }

    private String simplifyId(String idResourceName) {
        if(idResourceName == null)
            return null;
        int i = idResourceName.indexOf('/');
        return idResourceName.substring(i + 1);
    }

    public Rect getBoundsInScreen() {
        return mBoundsInScreen;
    }

    public static String boundsToString(Rect rect) {
        return rect.toString().replace('-', ',').replace(" ", "").substring(4);
    }

    public NodeInfo(AccessibilityNodeInfo node) {
        this(new AccessibilityNodeInfoCompat(node));
    }

    public static NodeInfo capture(@NonNull AccessibilityNodeInfo root) {
        NodeInfo nodeInfo = new NodeInfo(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                nodeInfo.children.add(capture(child));
            }
        }
        return nodeInfo;
    }

    public List<NodeInfo> getChildren() {
        return children;
    }
}
