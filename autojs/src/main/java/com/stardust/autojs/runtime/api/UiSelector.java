package com.stardust.autojs.runtime.api;

import android.accessibilityservice.AccessibilityService;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptInterface;
import com.stardust.autojs.runtime.ScriptInterruptedException;
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
                result = findOf(UiObject.createRoot(root, mAllocator));
            }
        }

    }

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

    @ScriptInterface
    public UiObjectCollection find() {
        ensureAccessibilityServiceEnabled();
        if (AutomatorConfig.isUnintendedGuardEnabled() && isRunningPackageSelf()) {
            Log.d(TAG, "isSelfPackage return null");
            return null;
        }
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service != null) {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root != null) {
                return findOf(UiObject.createRoot(root, mAllocator));
            }
        }
        return null;
    }

    private void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    private boolean isRunningPackageSelf() {
        return DeveloperUtils.isSelfPackage(mAccessibilityBridge.getInfoProvider().getLatestPackage());
    }


    @ScriptInterface
    @NonNull
    public UiObjectCollection untilFind() {
        UiObjectCollection uiObjectCollection;
        do {
            if (Thread.currentThread().isInterrupted()) {
                Log.d(TAG, "Thread isInterrupted");
                throw new ScriptInterruptedException();
            }
            uiObjectCollection = find();
        } while (uiObjectCollection == null || uiObjectCollection.size() == 0);
        return uiObjectCollection;
    }

    @ScriptInterface
    public UiObject findOne() {
        return untilFindOne();
    }

    @NonNull
    public UiObject untilFindOne() {
        UiObjectCollection collection = untilFind();
        return new UiObject(collection.get(0).getInfo());
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
            });
        } else {
            super.id(id);
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
