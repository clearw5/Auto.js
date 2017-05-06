package com.stardust.view.accessibility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.BuildConfig;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/22.
 */

public class AccessibilityNodeInfoAllocator {

    public static final AccessibilityNodeInfoAllocator NONE = new NoOpAllocator();

    private static final String TAG = "AccessibilityAllocator";
    private static final boolean DEBUG = BuildConfig.DEBUG;


    private static final AccessibilityNodeInfoAllocator GLOBAL = new AccessibilityNodeInfoAllocator();

    public static AccessibilityNodeInfoAllocator getGlobal() {
        return GLOBAL;
    }

    private Map<AccessibilityNodeInfo, String> mAccessibilityNodeInfoList = new HashMap<>();

    public AccessibilityNodeInfo getChild(AccessibilityNodeInfo parent, int i) {
        return add(parent.getChild(i));
    }

    public AccessibilityNodeInfoCompat getChild(AccessibilityNodeInfoCompat parent, int i) {
        AccessibilityNodeInfoCompat compat = parent.getChild(i);
        add((AccessibilityNodeInfo) compat.getInfo());
        return compat;
    }

    public AccessibilityNodeInfo getParent(AccessibilityNodeInfo n) {
        return add(n.getParent());
    }


    public AccessibilityNodeInfoCompat getParent(AccessibilityNodeInfoCompat n) {
        AccessibilityNodeInfoCompat compat = n.getParent();
        add((AccessibilityNodeInfo) compat.getInfo());
        return compat;
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(AccessibilityNodeInfo root, String text) {
        List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(text);
        addAll(list);
        return list;
    }


    public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(AccessibilityNodeInfoCompat root, String text) {
        List<AccessibilityNodeInfoCompat> list = root.findAccessibilityNodeInfosByText(text);
        addAll(list);
        return list;
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByViewId(id);
        addAll(list);
        return list;
    }


    public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfoCompat root, String id) {
        List<AccessibilityNodeInfoCompat> list = root.findAccessibilityNodeInfosByViewId(id);
        addAll(list);
        return list;
    }

    public void recycle(AccessibilityNodeInfo nodeInfo) {
        nodeInfo.recycle();
        mAccessibilityNodeInfoList.remove(nodeInfo);
    }

    public void recycle(AccessibilityNodeInfoCompat nodeInfo) {
        recycle((AccessibilityNodeInfo) nodeInfo.getInfo());
    }

    public int recycleAll() {
        int notRecycledCount = 0;
        int size = mAccessibilityNodeInfoList.size();
        for (Map.Entry<AccessibilityNodeInfo, String> pair : mAccessibilityNodeInfoList.entrySet()) {
            try {
                pair.getKey().recycle();
                notRecycledCount++;
                if (DEBUG)
                    Log.w(TAG, pair.getValue());
            } catch (IllegalStateException ignored) {
            }
        }
        Log.v(TAG, "Total: " + size + " Not recycled: " + notRecycledCount);
        return notRecycledCount;
    }

    public AccessibilityNodeInfo add(@Nullable AccessibilityNodeInfo nodeInfo) {
        String stackTrace = DEBUG ? Arrays.toString(Thread.currentThread().getStackTrace()) : null;
        if (nodeInfo != null)
            mAccessibilityNodeInfoList.put(nodeInfo, stackTrace);
        return nodeInfo;
    }

    private void addAll(Collection<?> nodeInfos) {
        String stackTrace = DEBUG ? Arrays.toString(Thread.currentThread().getStackTrace()) : null;
        for (Object nodeInfo : nodeInfos) {
            if (nodeInfo instanceof AccessibilityNodeInfo) {
                mAccessibilityNodeInfoList.put((AccessibilityNodeInfo) nodeInfo, stackTrace);
            } else if (nodeInfo instanceof AccessibilityNodeInfoCompat) {
                mAccessibilityNodeInfoList.put((AccessibilityNodeInfo) ((AccessibilityNodeInfoCompat) nodeInfo).getInfo(), stackTrace);
            }
        }
    }

    public static void recycleList(AccessibilityNodeInfo root, List<AccessibilityNodeInfo> list) {
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (nodeInfo != root && nodeInfo != null) {
                //// FIXME: 2017/5/1 Issue #180
                nodeInfo.recycle();
            }
        }
    }

    private static class NoOpAllocator extends AccessibilityNodeInfoAllocator {

        @Override
        public AccessibilityNodeInfoCompat getParent(AccessibilityNodeInfoCompat n) {
            return n.getParent();
        }

        @Override
        public AccessibilityNodeInfo getParent(AccessibilityNodeInfo n) {
            return n.getParent();
        }

        @Override
        public AccessibilityNodeInfoCompat getChild(AccessibilityNodeInfoCompat parent, int i) {
            return parent.getChild(i);
        }

        @Override
        public AccessibilityNodeInfo getChild(AccessibilityNodeInfo parent, int i) {
            return parent.getChild(i);
        }

        @Override
        public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfoCompat root, String id) {
            return root.findAccessibilityNodeInfosByViewId(id);
        }


        @Override
        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfo root, String id) {
            return root.findAccessibilityNodeInfosByViewId(id);
        }

        @Override
        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(AccessibilityNodeInfo root, String text) {
            return root.findAccessibilityNodeInfosByText(text);
        }

        @Override
        public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(AccessibilityNodeInfoCompat root, String text) {
            return root.findAccessibilityNodeInfosByText(text);
        }

        @Override
        public void recycle(AccessibilityNodeInfo nodeInfo) {
            super.recycle(nodeInfo);
        }

        @Override
        public void recycle(AccessibilityNodeInfoCompat nodeInfo) {
            super.recycle(nodeInfo);
        }

        @Override
        public int recycleAll() {
            return -1;
        }


    }


}
