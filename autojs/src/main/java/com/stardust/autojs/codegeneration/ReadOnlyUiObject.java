package com.stardust.autojs.codegeneration;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.NodeInfo;

/**
 * Created by Stardust on 2017/11/5.
 */

public class ReadOnlyUiObject extends UiObject {

    private NodeInfo mNodeInfo;

    public ReadOnlyUiObject(NodeInfo info) {
        super(null, info.getDepth(), -1);
        mNodeInfo = info;
    }

    public ReadOnlyUiObject(NodeInfo info, int indexInParent) {
        super(null, info.getDepth(), indexInParent);
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
        return mNodeInfo.getParent() == null ? null : new ReadOnlyUiObject(mNodeInfo.getParent());
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
        return mNodeInfo.getClassName();
    }

    @Override
    public CharSequence getClassName() {
        return className();
    }

    @Override
    public String packageName() {
        return mNodeInfo.getPackageName();
    }

    @Override
    public CharSequence getPackageName() {
        return packageName();
    }

    @Override
    public String id() {
        return mNodeInfo.getId();
    }

    @Override
    public String desc() {
        return mNodeInfo.getDesc();
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
        return mNodeInfo.getDrawingOrder();
    }

    @NonNull
    @Override
    public String text() {
        return mNodeInfo.getText();
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
        return mNodeInfo.getDepth();
    }

    @Override
    public boolean checkable() {
        return mNodeInfo.getCheckable();
    }

    @Override
    public boolean checked() {
        return mNodeInfo.getChecked();
    }

    @Override
    public boolean focusable() {
        return mNodeInfo.getFocusable();
    }

    @Override
    public boolean focused() {
        return mNodeInfo.getFocused();
    }

    @Override
    public boolean visibleToUser() {
        return mNodeInfo.getVisibleToUser();
    }

    @Override
    public boolean accessibilityFocused() {
        return mNodeInfo.getAccessibilityFocused();
    }

    @Override
    public boolean selected() {
        return mNodeInfo.getSelected();
    }

    @Override
    public boolean clickable() {
        return mNodeInfo.getClickable();
    }

    @Override
    public boolean longClickable() {
        return mNodeInfo.getLongClickable();
    }

    @Override
    public boolean enabled() {
        return mNodeInfo.getEnabled();
    }

    @Override
    public boolean scrollable() {
        return mNodeInfo.getScrollable();
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
        return mNodeInfo.getContextClickable();
    }

    @Override
    public boolean isDismissable() {
        return mNodeInfo.getDismissable();
    }

    @Override
    public boolean isEditable() {
        return mNodeInfo.getEditable();
    }

    @Override
    public int row() {
        return mNodeInfo.getRow();
    }

    @Override
    public int column() {
        return mNodeInfo.getColumn();
    }

    @Override
    public int rowSpan() {
        return mNodeInfo.getRowSpan();
    }

    @Override
    public int columnSpan() {
        return mNodeInfo.getColumnSpan();
    }

    @Override
    public int rowCount() {
        return mNodeInfo.getRowCount();
    }

    @Override
    public int columnCount() {
        return mNodeInfo.getColumnCount();
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
