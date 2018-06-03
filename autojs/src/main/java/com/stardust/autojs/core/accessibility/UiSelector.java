package com.stardust.autojs.core.accessibility;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.automator.ActionArgument;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;
import com.stardust.automator.filter.DfsFilter;
import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

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

public class UiSelector extends UiGlobalSelector {


    private static final String TAG = "UiSelector";

    private AccessibilityBridge mAccessibilityBridge;
    private AccessibilityNodeInfoAllocator mAllocator = null;

    public UiSelector(AccessibilityBridge accessibilityBridge) {
        mAccessibilityBridge = accessibilityBridge;
    }

    public UiSelector(AccessibilityBridge accessibilityBridge, AccessibilityNodeInfoAllocator allocator) {
        mAccessibilityBridge = accessibilityBridge;
        mAllocator = allocator;
    }

    @NonNull
    @ScriptInterface
    public UiObjectCollection find() {
        ensureAccessibilityServiceEnabled();
        AccessibilityNodeInfo root = mAccessibilityBridge.getRootInActiveWindow();
        if (BuildConfig.DEBUG)
            Log.d(TAG, "find: root = " + root);
        if (root == null) {
            return UiObjectCollection.EMPTY;
        }
        if (root.getPackageName() != null && mAccessibilityBridge.getConfig().whiteListContains(root.getPackageName().toString())) {
            Log.d(TAG, "package in white list, return null");
            return UiObjectCollection.EMPTY;
        }
        return findOf(UiObject.createRoot(root, mAllocator));
    }

    @Override
    public UiGlobalSelector textMatches(String regex) {
        return super.textMatches(convertRegex(regex));
    }

    // TODO: 2018/1/30 更好的实现方式。
    private String convertRegex(String regex) {
        if (regex.startsWith("/") && regex.endsWith("/") && regex.length() > 2) {
            return regex.substring(1, regex.length() - 1);
        }
        return regex;
    }

    @Override
    public UiGlobalSelector classNameMatches(String regex) {
        return super.classNameMatches(convertRegex(regex));
    }

    @Override
    public UiGlobalSelector idMatches(String regex) {
        return super.idMatches(convertRegex(regex));
    }

    @Override
    public UiGlobalSelector packageNameMatches(String regex) {
        return super.packageNameMatches(convertRegex(regex));
    }

    @Override
    public UiGlobalSelector descMatches(String regex) {
        return super.descMatches(convertRegex(regex));
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    @ScriptInterface
    @NonNull
    public UiObjectCollection untilFind() {
        UiObjectCollection uiObjectCollection = find();
        while (uiObjectCollection.empty()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new ScriptInterruptedException();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
            uiObjectCollection = find();
        }
        return uiObjectCollection;
    }

    @ScriptInterface
    public UiObject findOne(long timeout) {
        if (timeout == -1) {
            return untilFindOne();
        }
        UiObjectCollection uiObjectCollection = find();
        long start = SystemClock.uptimeMillis();
        while (uiObjectCollection.empty()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new ScriptInterruptedException();
            }
            if (SystemClock.uptimeMillis() - start > timeout) {
                return null;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
            uiObjectCollection = find();
        }
        return uiObjectCollection.get(0);
    }

    public UiObject findOnce() {
        return findOnce(0);
    }

    public UiObject findOnce(int index) {
        UiObjectCollection uiObjectCollection = find();
        if (index >= uiObjectCollection.size()) {
            return null;
        }
        return uiObjectCollection.get(index);
    }

    @ScriptInterface
    public UiObject findOne() {
        return untilFindOne();
    }

    @ScriptInterface
    public boolean exists() {
        UiObjectCollection collection = find();
        return collection.nonEmpty();
    }

    @NonNull
    public UiObject untilFindOne() {
        UiObjectCollection collection = untilFind();
        return collection.get(0);
    }

    @ScriptInterface
    public void waitFor() {
        untilFind();
    }

    @ScriptInterface
    public UiSelector id(final String id) {
        if (!id.contains(":")) {
            addFilter(new DfsFilter() {
                @Override
                protected boolean isIncluded(UiObject nodeInfo) {
                    String fullId = mAccessibilityBridge.getInfoProvider().getLatestPackage() + ":id/" + id;
                    return fullId.equals(nodeInfo.getViewIdResourceName());
                }

                @Override
                public String toString() {
                    return "id(\"" + id + "\")";
                }
            });
        } else {
            super.id(id);
        }
        return this;
    }

    @Override
    public UiGlobalSelector idStartsWith(String prefix) {
        if (!prefix.contains(":")) {
            addFilter(new DfsFilter() {
                @Override
                protected boolean isIncluded(UiObject nodeInfo) {
                    String fullIdPrefix = mAccessibilityBridge.getInfoProvider().getLatestPackage() + ":id/" + prefix;
                    String id = nodeInfo.getViewIdResourceName();
                    return id != null && id.startsWith(fullIdPrefix);
                }

                @Override
                public String toString() {
                    return "idStartsWith(\"" + prefix + "\")";
                }
            });
        } else {
            super.idStartsWith(prefix);
        }
        return this;
    }

    private boolean performAction(int action, ActionArgument... arguments) {
        return untilFind().performAction(action, arguments);
    }


    @ScriptInterface
    public boolean click() {
        return performAction(ACTION_CLICK);
    }

    @ScriptInterface
    public boolean longClick() {
        return performAction(ACTION_LONG_CLICK);
    }

    @ScriptInterface
    public boolean accessibilityFocus() {
        return performAction(ACTION_ACCESSIBILITY_FOCUS);
    }

    @ScriptInterface
    public boolean clearAccessibilityFocus() {
        return performAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
    }

    @ScriptInterface
    public boolean focus() {
        return performAction(ACTION_FOCUS);
    }

    @ScriptInterface
    public boolean clearFocus() {
        return performAction(ACTION_CLEAR_FOCUS);
    }

    @ScriptInterface
    public boolean copy() {
        return performAction(ACTION_COPY);
    }

    @ScriptInterface
    public boolean paste() {
        return performAction(ACTION_PASTE);
    }

    @ScriptInterface
    public boolean select() {
        return performAction(ACTION_SELECT);
    }

    @ScriptInterface
    public boolean cut() {
        return performAction(ACTION_CUT);
    }

    @ScriptInterface
    public boolean collapse() {
        return performAction(ACTION_COLLAPSE);
    }

    @ScriptInterface
    public boolean expand() {
        return performAction(ACTION_EXPAND);
    }

    @ScriptInterface
    public boolean dismiss() {
        return performAction(ACTION_DISMISS);
    }

    @ScriptInterface
    public boolean show() {
        return performAction(ACTION_SHOW_ON_SCREEN.getId());
    }

    @ScriptInterface
    public boolean scrollForward() {
        return performAction(ACTION_SCROLL_FORWARD);
    }

    @ScriptInterface
    public boolean scrollBackward() {
        return performAction(ACTION_SCROLL_BACKWARD);
    }

    @ScriptInterface
    public boolean scrollUp() {
        return performAction(ACTION_SCROLL_UP.getId());
    }

    @ScriptInterface
    public boolean scrollDown() {
        return performAction(ACTION_SCROLL_DOWN.getId());
    }

    @ScriptInterface
    public boolean scrollLeft() {
        return performAction(ACTION_SCROLL_LEFT.getId());
    }

    @ScriptInterface
    public boolean scrollRight() {
        return performAction(ACTION_SCROLL_RIGHT.getId());
    }

    @ScriptInterface
    public boolean contextClick() {
        return performAction(ACTION_CONTEXT_CLICK.getId());
    }

    @ScriptInterface
    public boolean setSelection(int s, int e) {
        return performAction(ACTION_SET_SELECTION,
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_START_INT, s),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_END_INT, e));
    }

    @ScriptInterface
    public boolean setText(String text) {
        return performAction(ACTION_SET_TEXT,
                new ActionArgument.CharSequenceActionArgument(ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text));
    }

    @ScriptInterface
    public boolean setProgress(float value) {
        return performAction(ACTION_SET_PROGRESS.getId(),
                new ActionArgument.FloatActionArgument(ACTION_ARGUMENT_PROGRESS_VALUE, value));
    }

    @ScriptInterface
    public boolean scrollTo(int row, int column) {
        return performAction(ACTION_SCROLL_TO_POSITION.getId(),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_ROW_INT, row),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_COLUMN_INT, column));
    }
}
