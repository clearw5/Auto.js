package com.stardust.automator;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.filter.BooleanFilter;
import com.stardust.automator.filter.BoundsFilter;
import com.stardust.automator.filter.IdFilter;
import com.stardust.automator.filter.PackageNameFilter;
import com.stardust.automator.filter.TextFilter;
import com.stardust.automator.filter.ClassNameFilter;
import com.stardust.automator.filter.DescFilter;
import com.stardust.automator.filter.ListFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
 * Created by Stardust on 2017/3/8.
 */

public class UiGlobalSelector {

    private Queue<ListFilter> mFilters = new LinkedList<>();

    //// 第一类筛选条件

    public UiGlobalSelector id(String id) {
        mFilters.add(IdFilter.equals(id));
        return this;
    }

    public UiGlobalSelector idContains(String str) {
        mFilters.add(IdFilter.contains(str));
        return this;
    }

    public UiGlobalSelector idStartsWith(String prefix) {
        mFilters.add(IdFilter.startsWith(prefix));
        return this;
    }

    public UiGlobalSelector idEndsWith(String suffix) {
        mFilters.add(IdFilter.endsWith(suffix));
        return this;
    }

    public UiGlobalSelector idMatches(String regex) {
        mFilters.add(IdFilter.matches(regex));
        return this;
    }

    public UiGlobalSelector text(String text) {
        mFilters.add(TextFilter.equals(text));
        return this;
    }

    public UiGlobalSelector textContains(String str) {
        mFilters.add(TextFilter.contains(str));
        return this;
    }

    public UiGlobalSelector textStartsWith(String prefix) {
        mFilters.add(TextFilter.startsWith(prefix));
        return this;
    }

    public UiGlobalSelector textEndsWith(String suffix) {
        mFilters.add(TextFilter.endsWith(suffix));
        return this;
    }

    public UiGlobalSelector textMatches(String regex) {
        mFilters.add(TextFilter.matches(regex));
        return this;
    }

    public UiGlobalSelector desc(String desc) {
        mFilters.add(DescFilter.equals(desc));
        return this;
    }

    public UiGlobalSelector descContains(String str) {
        mFilters.add(DescFilter.contains(str));
        return this;
    }

    public UiGlobalSelector descStartsWith(String prefix) {
        mFilters.add(DescFilter.startsWith(prefix));
        return this;
    }

    public UiGlobalSelector descEndsWith(String suffix) {
        mFilters.add(DescFilter.endsWith(suffix));
        return this;
    }

    public UiGlobalSelector descMatches(String regex) {
        mFilters.add(DescFilter.matches(regex));
        return this;
    }

    public UiGlobalSelector className(String className) {
        mFilters.add(ClassNameFilter.equals(className));
        return this;
    }

    public UiGlobalSelector classNameContains(String str) {
        mFilters.add(ClassNameFilter.contains(str));
        return this;
    }

    public UiGlobalSelector classNameStartsWith(String prefix) {
        mFilters.add(ClassNameFilter.startsWith(prefix));
        return this;
    }

    public UiGlobalSelector classNameEndsWith(String suffix) {
        mFilters.add(ClassNameFilter.endsWith(suffix));
        return this;
    }

    public UiGlobalSelector classNameMatches(String regex) {
        mFilters.add(ClassNameFilter.matches(regex));
        return this;
    }

    public UiGlobalSelector packageName(String packageName) {
        mFilters.add(PackageNameFilter.equals(packageName));
        return this;
    }

    public UiGlobalSelector packageNameContains(String str) {
        mFilters.add(PackageNameFilter.contains(str));
        return this;
    }

    public UiGlobalSelector packageNameStartsWith(String prefix) {
        mFilters.add(PackageNameFilter.startsWith(prefix));
        return this;
    }

    public UiGlobalSelector packageNameEndsWith(String suffix) {
        mFilters.add(PackageNameFilter.endsWith(suffix));
        return this;
    }

    public UiGlobalSelector packageNameMatches(String regex) {
        mFilters.add(PackageNameFilter.matches(regex));
        return this;
    }

    public UiGlobalSelector bounds(int l, int t, int r, int b) {
        mFilters.add(new BoundsFilter(new Rect(l, t, r, b), BoundsFilter.TYPE_EQUALS));
        return this;
    }

    public UiGlobalSelector boundsInside(int l, int t, int r, int b) {
        mFilters.add(new BoundsFilter(new Rect(l, t, r, b), BoundsFilter.TYPE_INSIDE));
        return this;
    }

    public UiGlobalSelector boundsInParent(int l, int t, int r, int b) {
        mFilters.add(new BoundsFilter(new Rect(l, t, r, b), BoundsFilter.TYPE_PARENT));
        return this;
    }

    //// 第二类筛选条件 -able

    public UiGlobalSelector checkable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.CHECKABLE, b));
        return this;
    }

    public UiGlobalSelector checked(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.CHECKED, b));
        return this;
    }

    public UiGlobalSelector focusable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.FOCUSABLE, b));
        return this;
    }

    public UiGlobalSelector focused(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.FOCUSED, b));
        return this;
    }

    public UiGlobalSelector visibleToUser(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.VISIBLE_TO_USER, b));
        return this;
    }

    public UiGlobalSelector accessibilityFocused(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.ACCESSIBILITY_FOCUSED, b));
        return this;
    }

    public UiGlobalSelector selected(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.SELECTED, b));
        return this;
    }

    public UiGlobalSelector clickable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.CLICKABLE, b));
        return this;
    }

    public UiGlobalSelector longClickable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.LONG_CLICKABLE, b));
        return this;
    }

    public UiGlobalSelector enabled(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.ENABLED, b));
        return this;
    }

    public UiGlobalSelector password(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.PASSWORD, b));
        return this;
    }

    public UiGlobalSelector scrollable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.SCROLLABLE, b));
        return this;
    }

    public UiGlobalSelector editable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.EDITABLE, b));
        return this;
    }

    public UiGlobalSelector contentInvalid(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.CONTENT_INVALID, b));
        return this;
    }

    public UiGlobalSelector contextClickable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.CONTEXT_CLICKABLE, b));
        return this;
    }

    public UiGlobalSelector multiLine(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.MULTI_LINE, b));
        return this;
    }

    public UiGlobalSelector dismissable(boolean b) {
        mFilters.add(BooleanFilter.get(BooleanFilter.DISMISSABLE, b));
        return this;
    }

    public UiObjectCollection findOf(AccessibilityNodeInfo node) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        list.add(node);
        for (ListFilter filter : mFilters) {
            list = filter.filter(list);
        }
        return UiObjectCollection.of(list);
    }

    public UiObject findOneOf(AccessibilityNodeInfo node) {
        // TODO: 2017/3/9 优化
        return new UiObject(findOf(node).get(0).getInfo());
    }

    public UiGlobalSelector addFilter(ListFilter filter) {
        mFilters.add(filter);
        return this;
    }



}
