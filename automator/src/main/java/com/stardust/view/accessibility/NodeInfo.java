package com.stardust.view.accessibility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    public String idHex;
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
    public int indexInParent;
    public NodeInfo parent;


    public NodeInfo(Resources resources, UiObject node, NodeInfo parent) {
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
        indexInParent = node.indexInParent();

        this.parent = parent;
        if (resources != null && packageName != null && id != null) {
            idHex = "0x" + Integer.toHexString(resources.getIdentifier(node.getViewIdResourceName(), null, null));
        }
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


    static NodeInfo capture(HashMap<String, Resources> resourcesCache, Context context, @NonNull UiObject uiObject, @Nullable NodeInfo parent) {
        String pkg = uiObject.packageName();
        Resources resources = null;
        if (pkg != null) {
            resources = resourcesCache.get(pkg);
            if (resources == null) {
                try {
                    resources = context.getPackageManager().getResourcesForApplication(pkg);
                    resourcesCache.put(pkg, resources);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        NodeInfo nodeInfo = new NodeInfo(resources, uiObject, parent);
        int childCount = uiObject.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UiObject child = uiObject.child(i);
            if (child != null) {
                nodeInfo.children.add(capture(resourcesCache, context, child, nodeInfo));
            }
        }
        return nodeInfo;
    }

    public static NodeInfo capture(Context context, @NonNull AccessibilityNodeInfo root) {
        UiObject r = UiObject.createRoot(root);
        HashMap<String, Resources> resourcesCache = new HashMap<>();
        return capture(resourcesCache, context, r, null);
    }

    @NonNull
    public List<NodeInfo> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return className + "{" +
                "childCount=" + children.size() +
                ", mBoundsInScreen=" + mBoundsInScreen +
                ", mBoundsInParent=" + mBoundsInParent +
                ", id='" + id + '\'' +
                ", desc='" + desc + '\'' +
                ", packageName='" + packageName + '\'' +
                ", text='" + text + '\'' +
                ", depth=" + depth +
                ", drawingOrder=" + drawingOrder +
                ", accessibilityFocused=" + accessibilityFocused +
                ", checked=" + checked +
                ", clickable=" + clickable +
                ", contextClickable=" + contextClickable +
                ", dismissable=" + dismissable +
                ", editable=" + editable +
                ", enabled=" + enabled +
                ", focusable=" + focusable +
                ", longClickable=" + longClickable +
                ", row=" + row +
                ", column=" + column +
                ", rowCount=" + rowCount +
                ", columnCount=" + columnCount +
                ", rowSpan=" + rowSpan +
                ", columnSpan=" + columnSpan +
                ", selected=" + selected +
                ", scrollable=" + scrollable +
                ", bounds='" + bounds + '\'' +
                ", checkable=" + checkable +
                ", focused=" + focused +
                ", visibleToUser=" + visibleToUser +
                ", parent=" + parent.className +
                '}';
    }
}
