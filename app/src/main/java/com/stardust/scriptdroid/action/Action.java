package com.stardust.scriptdroid.action;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_FOCUS;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_LONG_CLICK;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SET_TEXT;

/**
 * Created by Stardust on 2017/1/21.
 */

public abstract class Action {

    private static WeakReference<Context> mContext;

    public static void setActionContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    public abstract boolean perform(AccessibilityNodeInfo rootNodeInfo);

    interface TargetFilter {
        AccessibilityNodeInfo findTarget(AccessibilityNodeInfo nodeInfo);
    }

    public static class MultiAction extends Action {

        private List<Action> mActions;
        private boolean mDependent;

        public MultiAction(List<Action> actions, boolean dependent) {
            if (actions == null)
                throw new NullPointerException("actions = null");
            mDependent = dependent;
            mActions = actions;
        }

        @Override
        public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
            boolean succeed = true;
            for (Action action : mActions) {
                if (!action.perform(rootNodeInfo)) {
                    succeed = false;
                    if (!mDependent)
                        break;
                }
            }
            return succeed;
        }
    }

    public static abstract class TargetAction extends Action {

        static final int TYPE_ID = 1;
        static final int TYPE_TEXT = 2;
        static final int TYPE_BOUNDS = 3;
        static final int TYPE_DESCRIPTION = 4;

        private int mType;
        private String mString;
        private Rect mBoundsInScreen;

        private TargetAction(String str, int type) {
            if (str == null)
                throw new NullPointerException("str == null");
            if (type != TYPE_TEXT && type != TYPE_ID && type != TYPE_DESCRIPTION)
                throw new IllegalArgumentException("type illegal");
            mString = str;
            mType = type;
        }

        private TargetAction(Rect boundsInScreen) {
            mType = TYPE_BOUNDS;
            mBoundsInScreen = boundsInScreen;
        }

        @Override
        public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
            List<AccessibilityNodeInfo> target = null;
            switch (mType) {
                case TYPE_TEXT:
                    target = rootNodeInfo.findAccessibilityNodeInfosByText(mString);
                    break;
                case TYPE_ID:
                    target = rootNodeInfo.findAccessibilityNodeInfosByViewId(mString);
                    break;
                case TYPE_BOUNDS:
                    AccessibilityNodeInfo nodeInfo = findAccessibilityNodeInfosByBounds(rootNodeInfo, mBoundsInScreen);
                    target = nodeInfo == null ? Collections.EMPTY_LIST : Collections.singletonList(nodeInfo);
                    break;
                case TYPE_DESCRIPTION:
                    target = findAccessibilityNodeInfosByDescription(rootNodeInfo, mString);
            }
            return target != null && perform(target);
        }


        private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByDescription(AccessibilityNodeInfo rootNodeInfo, String description) {
            if (description.startsWith("edittext") && !description.equals("edittext")) {
                int i = Integer.parseInt(description.substring(8));
                return Collections.singletonList(findEditText(rootNodeInfo).get(i));
            }
            if (description.equals("edittext")) {
                return findEditText(rootNodeInfo);
            }
            return null;
        }

        private List<AccessibilityNodeInfo> findEditText(AccessibilityNodeInfo rootNodeInfo) {
            if (rootNodeInfo == null) {
                return Collections.EMPTY_LIST;
            }
            if (rootNodeInfo.isEditable()) {
                return Collections.singletonList(rootNodeInfo);
            }
            List<AccessibilityNodeInfo> list = new LinkedList<>();
            for (int i = 0; i < rootNodeInfo.getChildCount(); i++) {
                list.addAll(findEditText(rootNodeInfo.getChild(i)));
            }
            return list;
        }

        //(882, 1722 - 1036, 1876)
        private AccessibilityNodeInfo findAccessibilityNodeInfosByBounds(AccessibilityNodeInfo root, Rect boundsInScreen) {
            if (root == null)
                return null;
            Rect rect = new Rect();
            root.getBoundsInScreen(rect);
            if (rect.equals(boundsInScreen)) {
                return root;
            }
            for (int i = 0; i < root.getChildCount(); i++) {
                AccessibilityNodeInfo child = root.getChild(i);
                if (child == null)
                    continue;
                AccessibilityNodeInfo nodeInfo = findAccessibilityNodeInfosByBounds(child, boundsInScreen);
                if (nodeInfo != null)
                    return nodeInfo;
                else
                    child.recycle();
            }
            return null;
        }

        abstract boolean perform(@NonNull List<AccessibilityNodeInfo> nodeInfoList);
    }

    public static class SimpleAction extends TargetAction {

        private int mAction;

        SimpleAction(int action, String str, int type) {
            super(str, type);
            mAction = action;
        }

        SimpleAction(int action, Rect boundsInScreen) {
            super(boundsInScreen);
            mAction = action;
        }

        @Override
        boolean perform(@NonNull List<AccessibilityNodeInfo> nodeInfoList) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                performAction(nodeInfo);
            }
            return true;
        }

        boolean performAction(AccessibilityNodeInfo nodeInfo) {
            return nodeInfo.performAction(mAction);
        }

        void setAction(int action) {
            mAction = action;
        }

        int getAction() {
            return mAction;
        }
    }

    public static class SimpleFilterAction extends SimpleAction {

        private TargetFilter mFilter;

        SimpleFilterAction(int action, String str, int type, TargetFilter filter) {
            super(action, str, type);
            mFilter = filter;
        }

        SimpleFilterAction(int action, Rect boundsInScreen, TargetFilter filter) {
            super(action, boundsInScreen);
            mFilter = filter;
        }

        @Override
        boolean perform(@NonNull List<AccessibilityNodeInfo> nodeInfoList) {
            boolean performed = false;
            for (AccessibilityNodeInfo node : nodeInfoList) {
                node = mFilter.findTarget(node);
                if (node != null) {
                    performAction(node);
                    performed = true;
                }
            }
            return performed;
        }

    }

    interface Able {
        boolean isAble(AccessibilityNodeInfo node);
    }

    private static final SparseArray<Able> ACTION_ABLE_MAP = new SparseArray<>();

    static {
        ACTION_ABLE_MAP.put(ACTION_CLICK, new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isClickable();
            }
        });
        ACTION_ABLE_MAP.put(ACTION_LONG_CLICK, new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isLongClickable();
            }
        });
        ACTION_ABLE_MAP.put(ACTION_FOCUS, new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isFocusable();
            }
        });
        ACTION_ABLE_MAP.put(ACTION_SCROLL_FORWARD, new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isScrollable();
            }
        });
        ACTION_ABLE_MAP.put(ACTION_SCROLL_BACKWARD, new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isScrollable();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ACTION_ABLE_MAP.put(ACTION_SET_TEXT, new Able() {
                @Override
                public boolean isAble(AccessibilityNodeInfo node) {
                    return node.isEditable();
                }
            });
        }
    }


    public static class FindDownwardlyDfsFilterAction extends SimpleFilterAction {

        public static FindDownwardlyDfsFilterAction createActionByBounds(int action, Rect boundInScreen) {
            Able able = ACTION_ABLE_MAP.get(action);
            return new FindDownwardlyDfsFilterAction(action, boundInScreen, able);
        }

        FindDownwardlyDfsFilterAction(int action, Rect boundInScreen, Able able) {
            super(action, boundInScreen, new FindDownwardlyDfsTargetFilter(able));
        }

        FindDownwardlyDfsFilterAction(int action, String str, int type, Able able) {
            super(action, str, type, new FindDownwardlyDfsTargetFilter(able));
        }


        private static class FindDownwardlyDfsTargetFilter implements TargetFilter {
            Able mAble;

            FindDownwardlyDfsTargetFilter(Able able) {
                mAble = able;
            }

            @Override
            public AccessibilityNodeInfo findTarget(AccessibilityNodeInfo n) {
                if (n == null)
                    return null;
                if (mAble.isAble(n))
                    return n;
                for (int i = 0; i < n.getChildCount(); i++) {
                    AccessibilityNodeInfo child = n.getChild(i);
                    if (child == null)
                        continue;
                    AccessibilityNodeInfo node = findTarget(child);
                    if (node != null)
                        return node;
                    else
                        child.recycle();
                }
                return null;
            }
        }

    }

    public static class FindUpwardlyFilterAction extends SimpleFilterAction {


        public static FindUpwardlyFilterAction createActionById(int action, String id) {
            return createActionInner(action, id, TYPE_ID);
        }

        public static FindUpwardlyFilterAction createActionByText(int action, String text) {
            return createActionInner(action, text, TYPE_TEXT);
        }

        public static Action createActionByDescription(int action, String description) {
            return createActionInner(action, description, TYPE_DESCRIPTION);
        }

        private static FindUpwardlyFilterAction createActionInner(int action, String str, int type) {
            Able able = ACTION_ABLE_MAP.get(action);
            return new FindUpwardlyFilterAction(action, str, type, able);
        }

        private static class FindUpwardlyTargetFilter implements TargetFilter {
            Able mAble;

            FindUpwardlyTargetFilter(Able able) {
                mAble = able;
            }

            @Override
            public AccessibilityNodeInfo findTarget(AccessibilityNodeInfo n) {
                AccessibilityNodeInfo node = n;
                while (node != null && !mAble.isAble(node)) {
                    AccessibilityNodeInfo parent = node.getParent();
                    if (node != n) {
                        node.recycle();
                    }
                    node = parent;
                }
                return node;
            }
        }

        FindUpwardlyFilterAction(int action, String str, int type, Able able) {
            super(action, str, type, new FindUpwardlyTargetFilter(able));
        }

        FindUpwardlyFilterAction(int action, Rect boundsInScreen, Able able) {
            super(action, boundsInScreen, new FindUpwardlyTargetFilter(able));
        }
    }

    public static class ScrollAction extends Action {

        public static final int SCROLL_FORWARD = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
        public static final int SCROLL_BACKWARD = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;

        private int mScrollAction;
        private int mTimes;

        public ScrollAction(int scrollAction) {
            this(scrollAction, 1);
        }

        public ScrollAction(int scrollAction, int times) {
            if (scrollAction != SCROLL_BACKWARD && scrollAction != SCROLL_FORWARD)
                throw new IllegalArgumentException("scrollAction illegal");
            mScrollAction = scrollAction;
            mTimes = times;
        }

        @Override
        public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
            AccessibilityNodeInfo scrollableNodeInfo = findScrollableNodeInfo(rootNodeInfo);
            if (scrollableNodeInfo == null) {
                return false;
            } else {
                for (int i = 0; i < mTimes; i++) {
                    scrollableNodeInfo.performAction(mScrollAction);
                }
                return true;
            }
        }

        private AccessibilityNodeInfo findScrollableNodeInfo(AccessibilityNodeInfo nodeInfo) {
            if (nodeInfo == null)
                return null;
            if (nodeInfo.isScrollable()) {
                return nodeInfo;
            }
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo node = findScrollableNodeInfo(nodeInfo.getChild(i));
                if (node != null) {
                    return node;
                }
            }
            return null;
        }

    }

    public static class IntentAction extends Action {

        private final Intent mIntent;

        public IntentAction(Intent intent) {
            mIntent = intent;
        }

        @Override
        public boolean perform(AccessibilityNodeInfo rootNodeInfo) {
            if (mContext == null || mContext.get() == null)
                return false;
            mContext.get().startActivity(mIntent);
            return true;
        }

        public Intent getIntent() {
            return mIntent;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static class InputAction extends FindUpwardlyFilterAction {

        private static final Able EDITABLE = new Able() {
            @Override
            public boolean isAble(AccessibilityNodeInfo node) {
                return node.isEditable();
            }
        };
        private String mText;

        public InputAction(String description, String text) {
            super(ACTION_SET_TEXT, description, TYPE_DESCRIPTION, EDITABLE);
            mText = text;
        }

        boolean performAction(AccessibilityNodeInfo nodeInfo) {
            Bundle arg = new Bundle();
            arg.putCharSequence(ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mText);
            return nodeInfo.performAction(ACTION_SET_TEXT, arg);
        }
    }
}
