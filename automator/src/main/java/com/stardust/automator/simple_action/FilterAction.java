package com.stardust.automator.simple_action;

import android.graphics.Rect;

import com.stardust.automator.UiObject;
import com.stardust.automator.filter.BoundsFilter;
import com.stardust.view.accessibility.AccessibilityNodeInfoHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class FilterAction extends SimpleAction {


    public interface Filter {

        List<UiObject> filter(UiObject root);
    }

    public static class TextFilter implements Filter {

        String mText;
        int mIndex;

        public TextFilter(String text, int index) {
            mText = text;
            mIndex = index;
        }

        @Override
        public List<UiObject> filter(UiObject root) {
            List<UiObject> list = root.findByText(mText);
            if (mIndex == -1)
                return list;
            if (mIndex >= list.size())
                return Collections.emptyList();
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
        public List<UiObject> filter(UiObject root) {
            List<UiObject> list = new ArrayList<>();
            findAccessibilityNodeInfosByBounds(root, list);
            return list;
        }

        private void findAccessibilityNodeInfosByBounds(UiObject root, List<UiObject> list) {
            if (root == null)
                return;
            Rect rect = new Rect();
            root.getBoundsInScreen(rect);
            if (rect.equals(mBoundsInScreen)) {
                list.add(root);
            }
            int oldSize = list.size();
            for (int i = 0; i < root.getChildCount(); i++) {
                UiObject child = root.child(i);
                if (child == null)
                    continue;
                findAccessibilityNodeInfosByBounds(child, list);
            }
            if (oldSize == list.size() && rect.contains(mBoundsInScreen)) {
                list.add(root);
            }
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
        public List<UiObject> filter(UiObject root) {
            List<UiObject> editableList = findEditable(root);
            if (mIndex == -1)
                return editableList;
            if (mIndex >= editableList.size())
                return Collections.emptyList();
            return Collections.singletonList(editableList.get(mIndex));
        }

        public static List<UiObject> findEditable(UiObject root) {
            if (root == null) {
                return Collections.emptyList();
            }
            if (root.isEditable()) {
                return Collections.singletonList(root);
            }
            List<UiObject> list = new LinkedList<>();
            for (int i = 0; i < root.getChildCount(); i++) {
                list.addAll(findEditable(root.child(i)));
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
        public List<UiObject> filter(UiObject root) {
            return root.findByViewId(mId);
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

    @Override
    public boolean perform(UiObject root) {
        if (root == null)
            return false;
        List<UiObject> list = mFilter.filter(root);
        return perform(list);
    }

    public abstract boolean perform(List<UiObject> nodes);

    public static class SimpleFilterAction extends FilterAction {

        private int mAction;

        public SimpleFilterAction(int action, Filter filter) {
            super(filter);
            mAction = action;
        }

        @Override
        public boolean perform(List<UiObject> nodes) {
            if (nodes == null || nodes.isEmpty())
                return false;
            boolean succeed = true;
            for (UiObject nodeInfo : nodes) {
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
