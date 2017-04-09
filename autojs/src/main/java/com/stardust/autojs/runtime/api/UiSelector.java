package com.stardust.autojs.runtime.api;

import android.accessibilityservice.AccessibilityService;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.JavascriptInterface;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.automator.ActionArgument;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;
import com.stardust.automator.filter.DfsFilter;
import com.stardust.util.DeveloperUtils;
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

    @Deprecated
    private class FindCommand extends AccessibilityEventCommandHost.AbstractCommand {

        UiObjectCollection result;

        @Override
        public void execute(AccessibilityService service, AccessibilityEvent event) {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root != null) {
                result = findOf(root);
            }
        }

    }

    private static final String TAG = "UiSelector";

    private AccessibilityBridge mAccessibilityBridge;
    private AccessibilityNodeInfoAllocator mAllocator = AccessibilityNodeInfoAllocator.getGlobal();

    public UiSelector(AccessibilityBridge accessibilityBridge) {
        mAccessibilityBridge = accessibilityBridge;
    }

    public UiSelector(AccessibilityBridge accessibilityBridge, AccessibilityNodeInfoAllocator allocator) {
        mAccessibilityBridge = accessibilityBridge;
        mAllocator = allocator;
    }

    @JavascriptInterface
    public UiObjectCollection find() {
        ensureAccessibilityServiceEnabled();
        if (isRunningPackageSelf()) {
            return null;
        }
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service != null) {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root != null) {
                return findOf(mAllocator, root);
            }
        }
        return null;
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    private boolean isRunningPackageSelf() {
        return DeveloperUtils.isRunningPackageSelf(mAccessibilityBridge.getInfoProvider().getLatestPackage());
    }


    @JavascriptInterface
    @NonNull
    public UiObjectCollection untilFind() {
        UiObjectCollection uiObjectCollection;
        do {
            if (Thread.currentThread().isInterrupted()) {
                throw new ScriptStopException(new InterruptedException());
            }
            uiObjectCollection = find();
        } while (uiObjectCollection == null || uiObjectCollection.size() == 0);
        return uiObjectCollection;
    }

    @JavascriptInterface
    public UiObject findOne() {
        return untilFindOne();
    }

    @NonNull
    public UiObject untilFindOne() {
        UiObjectCollection collection = untilFind();
        return new UiObject(collection.get(0).getInfo());
    }

    @JavascriptInterface
    public UiSelector id(final String id) {
        if (!id.contains(":")) {
            addFilter(new DfsFilter() {
                @Override
                protected boolean isIncluded(AccessibilityNodeInfo nodeInfo) {
                    String fullId = mAccessibilityBridge.getInfoProvider().getLatestPackage() + ":id/" + id;
                    return fullId.equals(nodeInfo.getViewIdResourceName());
                }
            });
        } else {
            super.id(id);
        }
        return this;
    }


    private boolean performAction(int action, ActionArgument... arguments) {
        return untilFind().performAction(action, arguments);
    }

    @JavascriptInterface
    public boolean click() {
        return performAction(ACTION_CLICK);
    }

    @JavascriptInterface
    public boolean longClick() {
        return performAction(ACTION_LONG_CLICK);
    }

    @JavascriptInterface
    public boolean accessibilityFocus() {
        return performAction(ACTION_ACCESSIBILITY_FOCUS);
    }

    @JavascriptInterface
    public boolean clearAccessibilityFocus() {
        return performAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
    }

    @JavascriptInterface
    public boolean focus() {
        return performAction(ACTION_FOCUS);
    }

    @JavascriptInterface
    public boolean clearFocus() {
        return performAction(ACTION_CLEAR_FOCUS);
    }

    @JavascriptInterface
    public boolean copy() {
        return performAction(ACTION_COPY);
    }

    @JavascriptInterface
    public boolean paste() {
        return performAction(ACTION_PASTE);
    }

    @JavascriptInterface
    public boolean select() {
        return performAction(ACTION_SELECT);
    }

    @JavascriptInterface
    public boolean cut() {
        return performAction(ACTION_CUT);
    }

    @JavascriptInterface
    public boolean collapse() {
        return performAction(ACTION_COLLAPSE);
    }

    @JavascriptInterface
    public boolean expand() {
        return performAction(ACTION_EXPAND);
    }

    @JavascriptInterface
    public boolean dismiss() {
        return performAction(ACTION_DISMISS);
    }

    @JavascriptInterface
    public boolean show() {
        return performAction(ACTION_SHOW_ON_SCREEN.getId());
    }

    @JavascriptInterface
    public boolean scrollForward() {
        return performAction(ACTION_SCROLL_FORWARD);
    }

    @JavascriptInterface
    public boolean scrollBackward() {
        return performAction(ACTION_SCROLL_BACKWARD);
    }

    @JavascriptInterface
    public boolean scrollUp() {
        return performAction(ACTION_SCROLL_UP.getId());
    }

    @JavascriptInterface
    public boolean scrollDown() {
        return performAction(ACTION_SCROLL_DOWN.getId());
    }

    @JavascriptInterface
    public boolean scrollLeft() {
        return performAction(ACTION_SCROLL_LEFT.getId());
    }

    @JavascriptInterface
    public boolean scrollRight() {
        return performAction(ACTION_SCROLL_RIGHT.getId());
    }

    @JavascriptInterface
    public boolean contextClick() {
        return performAction(ACTION_CONTEXT_CLICK.getId());
    }

    @JavascriptInterface
    public boolean setSelection(int s, int e) {
        return performAction(ACTION_SET_SELECTION,
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_START_INT, s),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_END_INT, e));
    }

    @JavascriptInterface
    public boolean setText(String text) {
        return performAction(ACTION_SET_TEXT,
                new ActionArgument.CharSequenceActionArgument(ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text));
    }

    @JavascriptInterface
    public boolean setProgress(float value) {
        return performAction(ACTION_SET_PROGRESS.getId(),
                new ActionArgument.FloatActionArgument(ACTION_ARGUMENT_PROGRESS_VALUE, value));
    }

    @JavascriptInterface
    public boolean scrollTo(int row, int column) {
        return performAction(ACTION_SCROLL_TO_POSITION.getId(),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_ROW_INT, row),
                new ActionArgument.IntActionArgument(ACTION_ARGUMENT_COLUMN_INT, column));
    }

}
