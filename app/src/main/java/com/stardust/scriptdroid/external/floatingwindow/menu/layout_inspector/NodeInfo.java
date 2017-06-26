package com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector;

import android.graphics.Rect;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/10.
 */
@Keep
public class NodeInfo {

    private List<NodeInfo> children = new ArrayList<>();
    private Rect mBoundsInScreen;

    public String id;
    public CharSequence contentDesc;
    public CharSequence className;
    public CharSequence packageName;
    public CharSequence text;
    public int drawingOrder;
    public boolean accessibilityFocused;
    public boolean checked;
    public boolean clickable;
    public boolean contextClickable;
    public boolean dismissable;
    public boolean editable;
    public boolean enabled;
    public boolean focusable;
    public boolean longClickable;
    public boolean selected;
    public boolean scrollable;
    public String bounds;


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
        mBoundsInScreen = new Rect();
        node.getBoundsInScreen(mBoundsInScreen);
        bounds = boundsToString(mBoundsInScreen);
    }

    private String simplifyId(String idResourceName) {
        if (idResourceName == null)
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


    public static NodeInfo capture(@NonNull UiObject root) {
        NodeInfo nodeInfo = new NodeInfo(root);
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UiObject child = root.child(i);
            if (child != null) {
                nodeInfo.children.add(capture(child));
            }
        }
        return nodeInfo;
    }

    public static NodeInfo capture(@NonNull AccessibilityNodeInfo root) {
        UiObject r = UiObject.createRoot(root);
        return capture(r);
    }

    public List<NodeInfo> getChildren() {
        return children;
    }
}
