package com.stardust.automator.simple_action;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class FilterAction extends SimpleAction {


    public interface Filter {

        List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root);
    }

    public static class TextFilter implements Filter {

        String mText;
        int mIndex;

        public TextFilter(String text, int index) {
            mText = text;
            mIndex = index;
        }

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = AccessibilityNodeInfoAllocator.getGlobal().findAccessibilityNodeInfosByText(root, mText);
            if (mIndex == -1)
                return list;
            if (mIndex >= list.size())
                return Collections.EMPTY_LIST;
            return Collections.singletonList(list.get(mIndex));
        }

        @Override
        public String toString() {
            return "TextFilter{" +
                    "mText='" + mText + '\'' +
                    ", mIndex=" + mIndex +
                    '}';
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
                AccessibilityNodeInfo child = AccessibilityNodeInfoAllocator.getGlobal().getChild(root, i);
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

        @Override
        public String toString() {
            return "BoundsFilter{" +
                    "mBoundsInScreen=" + mBoundsInScreen +
                    '}';
        }
    }

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

        public static List<AccessibilityNodeInfo> findEditable(AccessibilityNodeInfo root) {
            if (root == null) {
                return Collections.emptyList();
            }
            if (root.isEditable()) {
                return Collections.singletonList(root);
            }
            List<AccessibilityNodeInfo> list = new LinkedList<>();
            for (int i = 0; i < root.getChildCount(); i++) {
                list.addAll(findEditable(AccessibilityNodeInfoAllocator.getGlobal().getChild(root, i)));
            }
            return list;
        }

        @Override
        public String toString() {
            return "EditableFilter{" +
                    "mIndex=" + mIndex +
                    '}';
        }
    }

    public static class IdFilter implements Filter {

        private final String mId;

        public IdFilter(String id) {
            this.mId = id;
        }

        @Override
        public List<AccessibilityNodeInfo> filter(AccessibilityNodeInfo root) {
            return AccessibilityNodeInfoAllocator.getGlobal().findAccessibilityNodeInfosByViewId(root, mId);
        }

        @Override
        public String toString() {
            return "IdFilter{" +
                    "mId='" + mId + '\'' +
                    '}';
        }
    }


    private Filter mFilter;

    public FilterAction(Filter filter) {
        mFilter = filter;
    }

    public boolean perform(AccessibilityNodeInfo root) {
        if (root == null)
            return false;
        List<AccessibilityNodeInfo> list = mFilter.filter(root);
        boolean succeed = perform(list);
        AccessibilityNodeInfoAllocator.recycleList(root, list);
        return succeed;
    }

    public abstract boolean perform(List<AccessibilityNodeInfo> nodes);

    public static class SimpleFilterAction extends FilterAction {

        private int mAction;

        public SimpleFilterAction(int action, Filter filter) {
            super(filter);
            mAction = action;
        }

        @Override
        public boolean perform(List<AccessibilityNodeInfo> nodes) {
            if (nodes == null || nodes.isEmpty())
                return false;
            boolean succeed = true;
            for (AccessibilityNodeInfo nodeInfo : nodes) {
                if (!nodeInfo.performAction(mAction)) {
                    succeed = false;
                }
            }
            return succeed;
        }
    }

    @Override
    public String toString() {
        return "FilterAction{" +
                "mFilter=" + mFilter +
                '}';
    }
}
