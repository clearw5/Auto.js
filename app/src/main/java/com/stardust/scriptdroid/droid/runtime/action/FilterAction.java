package com.stardust.scriptdroid.droid.runtime.action;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class FilterAction extends Action {


    public interface Filter {

        List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root);
    }

    public static class TextFilter implements Filter {

        String mText;

        public TextFilter(String text) {
            mText = text;
        }

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root) {
            return root.findAccessibilityNodeInfosByText(mText);
        }
    }

    public static class BoundsFilter implements Filter {

        Rect mBoundsInScreen;

        public BoundsFilter(Rect boundsInScreen) {
            mBoundsInScreen = boundsInScreen;
        }

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root) {
            return Collections.singletonList(findAccessibilityNodeInfosByBounds(root));
        }

        private AccessibilityNodeInfo findAccessibilityNodeInfosByBounds(AccessibilityNodeInfo root) {
            if (root == null)
                return null;
            Rect rect = new Rect();
            root.getBoundsInScreen(rect);
            if (rect.equals(mBoundsInScreen)) {
                return root;
            }
            for (int i = 0; i < root.getChildCount(); i++) {
                AccessibilityNodeInfo child = root.getChild(i);
                if (child == null)
                    continue;
                AccessibilityNodeInfo nodeInfo = findAccessibilityNodeInfosByBounds(child);
                if (nodeInfo != null)
                    return nodeInfo;
                else
                    child.recycle();
            }
            return null;
        }
    }


    private Filter mFilter;

    public FilterAction(Filter filter) {
        mFilter = filter;
    }

    public boolean perform(AccessibilityNodeInfo root) {
        if (root == null)
            return false;
        return perform(mFilter.filter(root));
    }

    public abstract boolean perform(List<AccessibilityNodeInfo> nodes);

    public static class EditableFilter implements Filter {

        private int mIndex;

        public EditableFilter(int index) {
            mIndex = index;
        }

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> editableList = findEditable(root);
            if (mIndex == -1)
                return editableList;
            if (mIndex >= editableList.size())
                return Collections.EMPTY_LIST;
            return Collections.singletonList(editableList.get(mIndex));
        }

        @SuppressWarnings("unchecked")
        public static List<AccessibilityNodeInfo> findEditable(AccessibilityNodeInfo root) {
            if (root == null) {
                return Collections.EMPTY_LIST;
            }
            if (root.isEditable()) {
                return Collections.singletonList(root);
            }
            List<AccessibilityNodeInfo> list = new LinkedList<>();
            for (int i = 0; i < root.getChildCount(); i++) {
                list.addAll(findEditable(root.getChild(i)));
            }
            return list;
        }
    }
}
