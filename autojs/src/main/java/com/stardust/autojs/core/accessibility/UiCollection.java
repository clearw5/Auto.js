package com.stardust.autojs.core.accessibility;

import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.ActionArgument;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;
import com.stardust.util.ArrayUtils;
import com.stardust.util.Consumer;
import com.stardust.util.Func1;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.ast.Scope;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Stardust on 2017/10/31.
 */

public class UiCollection extends NativeJavaObject {

    private UiObjectCollection mUiObjectCollection;

    public UiCollection(UiObjectCollection c, Scriptable scope) {
        super(scope, c, UiObjectCollection.class);
        mUiObjectCollection = c;
    }

    public boolean performAction(int action) {
        return mUiObjectCollection.performAction(action);
    }

    public boolean performAction(int action, ActionArgument... arguments) {
        return mUiObjectCollection.performAction(action, arguments);
    }

    public boolean click() {
        return mUiObjectCollection.click();
    }

    public boolean longClick() {
        return mUiObjectCollection.longClick();
    }

    public boolean accessibilityFocus() {
        return mUiObjectCollection.accessibilityFocus();
    }

    public boolean clearAccessibilityFocus() {
        return mUiObjectCollection.clearAccessibilityFocus();
    }

    public boolean focus() {
        return mUiObjectCollection.focus();
    }

    public boolean clearFocus() {
        return mUiObjectCollection.clearFocus();
    }

    public boolean copy() {
        return mUiObjectCollection.copy();
    }

    public boolean paste() {
        return mUiObjectCollection.paste();
    }

    public boolean select() {
        return mUiObjectCollection.select();
    }

    public boolean cut() {
        return mUiObjectCollection.cut();
    }

    public boolean collapse() {
        return mUiObjectCollection.collapse();
    }


    public boolean expand() {
        return mUiObjectCollection.expand();
    }

    public boolean dismiss() {
        return mUiObjectCollection.dismiss();
    }

    public boolean show() {
        return mUiObjectCollection.show();
    }

    public boolean scrollForward() {
        return mUiObjectCollection.scrollForward();
    }

    public boolean scrollBackward() {
        return mUiObjectCollection.scrollBackward();
    }

    public boolean scrollUp() {
        return mUiObjectCollection.scrollUp();
    }

    public boolean scrollDown() {
        return mUiObjectCollection.scrollDown();
    }

    public boolean scrollLeft() {
        return mUiObjectCollection.scrollLeft();
    }

    public boolean scrollRight() {
        return mUiObjectCollection.scrollRight();
    }

    public boolean contextClick() {
        return mUiObjectCollection.contextClick();
    }

    public boolean setSelection(int s, int e) {
        return mUiObjectCollection.setSelection(s, e);
    }

    public boolean setText(CharSequence text) {
        return mUiObjectCollection.setText(text);
    }

    public boolean setProgress(float value) {
        return mUiObjectCollection.setProgress(value);
    }

    public boolean scrollTo(int row, int column) {
        return mUiObjectCollection.scrollTo(row, column);
    }

    public UiCollection each(Consumer<UiObject> consumer) {
        mUiObjectCollection.each(consumer);
        return this;
    }

    public UiCollection find(UiGlobalSelector selector) {
        return new UiCollection(mUiObjectCollection.find(selector), getParentScope());
    }

    @Nullable
    public UiObject findOne(UiGlobalSelector selector) {
        return mUiObjectCollection.findOne(selector);
    }

    public boolean empty() {
        return mUiObjectCollection.empty();
    }

    public boolean nonEmpty() {
        return mUiObjectCollection.nonEmpty();
    }


    @Override
    public boolean has(int index, Scriptable start) {
        return index > 0 && index < mUiObjectCollection.size();
    }

    @Override
    public Object get(int index, Scriptable start) {
        return mUiObjectCollection.get(index);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if ("length".equals(name)) {
            return mUiObjectCollection.size();
        }
        return super.get(name, start);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        if ("length".equals(name)) {
            return true;
        }
        return super.has(name, start);
    }
}

