package com.stardust.autojs.codegeneration;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;
import com.stardust.view.accessibility.NodeInfo;

/**
 * Created by Stardust on 2017/11/5.
 */

public class ReadOnlyUiObject extends UiObject {

    private NodeInfo mNodeInfo;
    private boolean mUsingId;
    private boolean mUsingText;
    private boolean mUsingDesc;

    public ReadOnlyUiObject(NodeInfo info) {
        super(info, info.depth);
        mNodeInfo = info;
    }

    @Nullable
    @Override
    public UiObject child(int i) {
        return new ReadOnlyUiObject(mNodeInfo.getChildren().get(i));
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
}
