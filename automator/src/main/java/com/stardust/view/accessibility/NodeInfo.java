package com.stardust.view.accessibility;

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
    private Rect mBoundsInScreen = new Rect();
    private Rect mBoundsInParent = new Rect();

    public String id;
    public String desc;
    public String className;
    public String packageName;
    public String text;
    public int depth;
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
    public int row;
    public int column;
    public int rowCount;
    public int columnCount;
    public int rowSpan;
    public int columnSpan;
    public boolean selected;
    public boolean scrollable;
    public String bounds;
    public boolean checkable;
    public boolean focused;
    public boolean visibleToUser;


    public NodeInfo(UiObject node) {
        id = simplifyId(node.getViewIdResourceName());
        desc = node.desc();
        className = node.className();
        packageName = node.packageName();
        text = node.text();

        depth = node.depth();
        drawingOrder = node.getDrawingOrder();

        row = node.row();
        column = node.column();
        rowCount = node.rowCount();
        columnCount = node.columnCount();
        rowSpan = node.rowSpan();
        columnSpan = node.columnSpan();

        accessibilityFocused = node.isAccessibilityFocused();
        checked = node.isChecked();
        checkable = node.isCheckable();
        clickable = node.isClickable();
        contextClickable = node.isContextClickable();
        dismissable = node.isDismissable();
        enabled = node.isEnabled();
        editable = node.isEditable();
        focusable = node.isFocusable();
        focused = node.focused();
        longClickable = node.isLongClickable();
        selected = node.isSelected();
        scrollable = node.isScrollable();
        visibleToUser = node.visibleToUser();
        node.getBoundsInScreen(mBoundsInScreen);
        node.getBoundsInParent(mBoundsInParent);
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


    public Rect getBoundsInParent() {
        return mBoundsInParent;
    }

    public static String boundsToString(Rect rect) {
        return rect.toString().replace('-', ',').replace(" ", "").substring(4);
    }


    public static NodeInfo capture(@NonNull UiObject parent) {
        NodeInfo nodeInfo = new NodeInfo(parent);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UiObject child = parent.child(i);
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

    @NonNull
    public List<NodeInfo> getChildren() {
        return children;
    }
}
