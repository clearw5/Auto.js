package com.stardust.automator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.util.Consumer;
import com.stardust.util.Func1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.*;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CONTEXT_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_TO_POSITION;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SHOW_ON_SCREEN;

/**
 * Created by Stardust on 2017/3/9.
 */

public class UiObjectCollection {

    public static final UiObjectCollection EMPTY = UiObjectCollection.of(Collections.<UiObject>emptyList());

    public static UiObjectCollection of(List<UiObject> list) {
        return new UiObjectCollection(list);
    }

    private List<UiObject> mNodes;

    private UiObjectCollection(List<UiObject> list) {
        mNodes = list;
    }

    public UiObject[] toArray(){
        return mNodes.toArray(new UiObject[mNodes.size()]);
    }

    public boolean performAction(int action) {
        boolean fail = false;
        for (UiObject node : mNodes) {
            if (!node.performAction(action)) {
                fail = true;
            }
        }
        return !fail;
    }

    public boolean performAction(int action, ActionArgument... arguments) {
        boolean fail = false;
        Bundle bundle = argumentsToBundle(arguments);
        for (UiObject node : mNodes) {
            if (!node.performAction(action, bundle)) {
                fail = true;
            }
        }
        return !fail;
    }

    private Bundle argumentsToBundle(ActionArgument[] arguments) {
        Bundle bundle = new Bundle();
        for (ActionArgument arg : arguments) {
            arg.putIn(bundle);
        }
        return bundle;
    }

    public boolean click() {
        return performAction(ACTION_CLICK);
    }

    public boolean longClick() {
        return performAction(ACTION_LONG_CLICK);
    }

    public boolean accessibilityFocus() {
        return performAction(ACTION_ACCESSIBILITY_FOCUS);
    }

    public boolean clearAccessibilityFocus() {
        return performAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
    }

    public boolean focus() {
        return performAction(ACTION_FOCUS);
    }

    public boolean clearFocus() {
        return performAction(ACTION_CLEAR_FOCUS);
    }

    public boolean copy() {
        return performAction(ACTION_COPY);
    }

    public boolean paste() {
        return performAction(ACTION_PASTE);
    }

    public boolean select() {
        return performAction(ACTION_SELECT);
    }

    public boolean cut() {
        return performAction(ACTION_CUT);
    }

    public boolean collapse() {
        return performAction(ACTION_COLLAPSE);
    }

    public boolean expand() {
        return performAction(ACTION_EXPAND);
    }

    public boolean dismiss() {
        return performAction(ACTION_DISMISS);
    }

    public boolean show() {
        return performAction(ACTION_SHOW_ON_SCREEN.getId());
    }

    public boolean scrollForward() {
        return performAction(ACTION_SCROLL_FORWARD);
    }

    public boolean scrollBackward() {
        return performAction(ACTION_SCROLL_BACKWARD);
    }

    public boolean scrollUp() {
        return performAction(ACTION_SCROLL_UP.getId());
    }

    public boolean scrollDown() {
        return performAction(ACTION_SCROLL_DOWN.getId());
    }

    public boolean scrollLeft() {
        return performAction(ACTION_SCROLL_LEFT.getId());
    }

    public boolean scrollRight() {
        return performAction(ACTION_SCROLL_RIGHT.getId());
    }

    public boolean contextClick() {
        return performAction(ACTION_CONTEXT_CLICK.getId());
    }

    public boolean setSelection(int s, int e) {
        return performAction(ACTION_SET_SELECTION,
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_START_INT, s),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_END_INT, e));
    }

    public boolean setText(CharSequence text) {
        return performAction(ACTION_SET_TEXT,
                new ActionArgument.CharSequenceActionArgument(ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text));
    }

    public boolean setProgress(float value) {
        return performAction(ACTION_SET_PROGRESS.getId(),
                new ActionArgument.FloatActionArgument(ACTION_ARGUMENT_PROGRESS_VALUE, value));

    }

    public boolean scrollTo(int row, int column) {
        return performAction(ACTION_SCROLL_TO_POSITION.getId(),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_ROW_INT, row),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_COLUMN_INT, column));
    }

    public UiObject get(int i) {
        return mNodes.get(i);
    }

    public int size() {
        return mNodes.size();
    }

    public UiObjectCollection each(Consumer<UiObject> consumer) {
        for (UiObject uiObject : mNodes) {
            consumer.accept(uiObject);
        }
        return this;
    }

    public UiObjectCollection find(UiGlobalSelector selector) {
        List<UiObject> list = new ArrayList<>();
        for (UiObject object : mNodes) {
            list.addAll(selector.findOf(object).mNodes);
        }
        return of(list);
    }

    @Nullable
    public UiObject findOne(UiGlobalSelector selector) {
        for (UiObject object : mNodes) {
            UiObject result = selector.findOneOf(object);
            if (result != null)
                return result;
        }
        return null;
    }

    public boolean empty() {
        return size() == 0;
    }

    public boolean nonEmpty() {
        return size() != 0;
    }

}
