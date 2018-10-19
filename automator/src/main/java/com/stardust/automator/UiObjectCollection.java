package com.stardust.automator;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.util.Consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_COLUMN_INT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_ROW_INT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLEAR_FOCUS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_COLLAPSE;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_COPY;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CUT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_DISMISS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_EXPAND;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_FOCUS;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_LONG_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_PASTE;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SELECT;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SET_SELECTION;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
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

    public UiObject[] toArray() {
        return mNodes.toArray(new UiObject[0]);
    }

    public <T> T[] toArray(T[] a) {
        return mNodes.toArray(a);
    }

    public boolean add(UiObject uiObject) {
        return mNodes.add(uiObject);
    }

    public boolean remove(Object o) {
        return mNodes.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return mNodes.containsAll(c);
    }


    public boolean addAll(Collection<? extends UiObject> c) {
        return mNodes.addAll(c);
    }


    public boolean addAll(int index, Collection<? extends UiObject> c) {
        return mNodes.addAll(index, c);
    }


    public boolean removeAll(Collection<?> c) {
        return mNodes.removeAll(c);
    }


    public boolean retainAll(Collection<?> c) {
        return mNodes.retainAll(c);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public void replaceAll(UnaryOperator<UiObject> operator) {
        mNodes.replaceAll(operator);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public void sort(Comparator<? super UiObject> c) {
        mNodes.sort(c);
    }


    public void clear() {
        mNodes.clear();
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


    public UiObject set(int index, UiObject element) {
        return mNodes.set(index, element);
    }


    public void add(int index, UiObject element) {
        mNodes.add(index, element);
    }


    public UiObject remove(int index) {
        return mNodes.remove(index);
    }


    public int indexOf(Object o) {
        return mNodes.indexOf(o);
    }


    public int lastIndexOf(Object o) {
        return mNodes.lastIndexOf(o);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public ListIterator<UiObject> listIterator() {
        return mNodes.listIterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public ListIterator<UiObject> listIterator(int index) {
        return mNodes.listIterator(index);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public List<UiObject> subList(int fromIndex, int toIndex) {
        return mNodes.subList(fromIndex, toIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public Spliterator<UiObject> spliterator() {
        return mNodes.spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public boolean removeIf(Predicate<? super UiObject> filter) {
        return mNodes.removeIf(filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public Stream<UiObject> stream() {
        return mNodes.stream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public Stream<UiObject> parallelStream() {
        return mNodes.parallelStream();
    }

    public int size() {
        return mNodes.size();
    }


    public boolean isEmpty() {
        return mNodes.isEmpty();
    }


    public boolean contains(Object o) {
        return mNodes.contains(o);
    }


    public Iterator<UiObject> iterator() {
        return mNodes.iterator();
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
