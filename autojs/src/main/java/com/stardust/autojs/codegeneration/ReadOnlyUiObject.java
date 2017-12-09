package com.stardust.autojs.codegeneration;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.NodeInfo;

/**
 * Created by Stardust on 2017/11/5.
 */

public class ReadOnlyUiObject extends UiObject {

    private NodeInfo mNodeInfo;

    public ReadOnlyUiObject(NodeInfo info) {
        super(info, info.depth, -1);
        mNodeInfo = info;
    }

    public ReadOnlyUiObject(NodeInfo info, int indexInParent) {
        super(info, info.depth, indexInParent);
        mNodeInfo = info;
    }

    @Nullable
    @Override
    public UiObject child(int i) {
        return new ReadOnlyUiObject(mNodeInfo.getChildren().get(i), i);
    }

    @Nullable
    @Override
    public UiObject parent() {
        return mNodeInfo.parent == null ? null : new ReadOnlyUiObject(mNodeInfo.parent);
    }

    @Override
    public int childCount() {
        return mNodeInfo.getChildren().size();
    }

    @Override
    public int getChildCount() {
        return childCount();
    }

    @Override
    public String className() {
        return mNodeInfo.className;
    }

    @Override
    public CharSequence getClassName() {
        return className();
    }

    @Override
    public String packageName() {
        return mNodeInfo.packageName;
    }

    @Override
    public CharSequence getPackageName() {
        return packageName();
    }

    @Override
    public String id() {
        return mNodeInfo.id;
    }

    @Override
    public String desc() {
        return mNodeInfo.desc;
    }

    @Override
    public String getViewIdResourceName() {
        return id();
    }

    @Override
    public CharSequence getContentDescription() {
        return desc();
    }


    @Override
    public Rect bounds() {
        return mNodeInfo.getBoundsInScreen();
    }

    @Override
    public Rect boundsInParent() {
        return mNodeInfo.getBoundsInParent();
    }

    @Override
    public int drawingOrder() {
        return mNodeInfo.drawingOrder;
    }

    @NonNull
    @Override
    public String text() {
        return mNodeInfo.text;
    }

    @Override
    public CharSequence getText() {
        return text();
    }

    @Override
    public AccessibilityNodeInfoCompat getChild(int index) {
        return child(index);
    }

    @Override
    public int getDrawingOrder() {
        return drawingOrder();
    }

    @Override
    public void getBoundsInParent(Rect outBounds) {
        outBounds.set(mNodeInfo.getBoundsInParent());
    }

    @Override
    public void getBoundsInScreen(Rect outBounds) {
        outBounds.set(mNodeInfo.getBoundsInScreen());
    }


    @Override
    public int depth() {
        return mNodeInfo.depth;
    }

    @Override
    public boolean checkable() {
        return mNodeInfo.checkable;
    }

    @Override
    public boolean checked() {
        return mNodeInfo.checked;
    }

    @Override
    public boolean focusable() {
        return mNodeInfo.focusable;
    }

    @Override
    public boolean focused() {
        return mNodeInfo.focused;
    }

    @Override
    public boolean visibleToUser() {
        return mNodeInfo.visibleToUser;
    }

    @Override
    public boolean accessibilityFocused() {
        return mNodeInfo.accessibilityFocused;
    }

    @Override
    public boolean selected() {
        return mNodeInfo.selected;
    }

    @Override
    public boolean clickable() {
        return mNodeInfo.clickable;
    }

    @Override
    public boolean longClickable() {
        return mNodeInfo.longClickable;
    }

    @Override
    public boolean enabled() {
        return mNodeInfo.enabled;
    }

    @Override
    public boolean scrollable() {
        return mNodeInfo.scrollable;
    }

    @Override
    public boolean isCheckable() {
        return checkable();
    }

    @Override
    public boolean isChecked() {
        return checked();
    }

    @Override
    public boolean isFocusable() {
        return focusable();
    }

    @Override
    public boolean isFocused() {
        return focused();
    }

    @Override
    public boolean isVisibleToUser() {
        return visibleToUser();
    }

    @Override
    public boolean isAccessibilityFocused() {
        return accessibilityFocused();
    }

    @Override
    public boolean isSelected() {
        return selected();
    }

    @Override
    public boolean isClickable() {
        return clickable();
    }

    @Override
    public boolean isLongClickable() {
        return longClickable();
    }

    @Override
    public boolean isEnabled() {
        return enabled();
    }

    @Override
    public boolean isPassword() {
        return password();
    }

    @Override
    public boolean isScrollable() {
        return scrollable();
    }


    @Override
    public boolean isContextClickable() {
        return mNodeInfo.contextClickable;
    }

    @Override
    public boolean isDismissable() {
        return mNodeInfo.dismissable;
    }

    @Override
    public boolean isEditable() {
        return mNodeInfo.editable;
    }

    @Override
    public int row() {
        return mNodeInfo.row;
    }

    @Override
    public int column() {
        return mNodeInfo.column;
    }

    @Override
    public int rowSpan() {
        return mNodeInfo.rowSpan;
    }

    @Override
    public int columnSpan() {
        return mNodeInfo.columnSpan;
    }

    @Override
    public int rowCount() {
        return mNodeInfo.rowCount;
    }

    @Override
    public int columnCount() {
        return mNodeInfo.columnCount;
    }

    @Override
    public void recycle() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ReadOnlyUiObject that = (ReadOnlyUiObject) o;

        return mNodeInfo.equals(that.mNodeInfo);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mNodeInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return mNodeInfo.toString();
    }
}
