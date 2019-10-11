package com.stardust.autojs.core.accessibility;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.stardust.app.AppOpsKt;
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;
import com.stardust.util.IntentUtil;
import com.stardust.util.UiHandler;
import com.stardust.autojs.core.activity.ActivityInfoProvider;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Stardust on 2017/4/2.
 */

public abstract class AccessibilityBridge {

    public interface WindowFilter {
        boolean filter(AccessibilityWindowInfo info);
    }

    public static final int MODE_NORMAL = 0;
    public static final int MODE_FAST = 1;

    public static final int FLAG_FIND_ON_UI_THREAD = 1;
    public static final int FLAG_USE_USAGE_STATS = 2;
    public static final int FLAG_USE_SHELL = 4;

    private int mMode = MODE_NORMAL;
    private int mFlags = 0;
    private final AccessibilityConfig mConfig;
    private WindowFilter mWindowFilter;
    private final UiHandler mUiHandler;
    private final Context mContext;

    public AccessibilityBridge(Context context, AccessibilityConfig config, UiHandler uiHandler) {
        mConfig = config;
        mUiHandler = uiHandler;
        mConfig.seal();
        mContext = context;
    }

    public abstract void ensureServiceEnabled();

    public abstract void waitForServiceEnabled();

    public void post(Runnable r) {
        mUiHandler.post(r);
    }

    @Nullable
    public abstract AccessibilityService getService();

    public List<AccessibilityNodeInfo> windowRoots() {
        AccessibilityService service = getService();
        if (service == null)
            return Collections.emptyList();
        ArrayList<AccessibilityNodeInfo> roots = new ArrayList<>();
        if (mWindowFilter != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (AccessibilityWindowInfo window : service.getWindows()) {
                if (mWindowFilter.filter(window)) {
                    AccessibilityNodeInfo root = window.getRoot();
                    if (root != null) {
                        roots.add(root);
                    }
                }
            }
            return roots;
        }
        if ((mMode & MODE_FAST) != 0) {
            return Collections.singletonList(service.fastRootInActiveWindow());
        }
        return Collections.singletonList(service.getRootInActiveWindow());
    }

    @Nullable
    public AccessibilityNodeInfo getRootInCurrentWindow() {
        AccessibilityService service = getService();
        if (service == null)
            return null;
        if (mWindowFilter != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (AccessibilityWindowInfo window : service.getWindows()) {
                if (mWindowFilter.filter(window)) {
                    return window.getRoot();
                }
            }
            return null;
        }
        if ((mMode & MODE_FAST) != 0) {
            return service.fastRootInActiveWindow();
        }
        return service.getRootInActiveWindow();
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        AccessibilityService service = getService();
        if (service == null)
            return null;
        if ((mMode & MODE_FAST) != 0) {
            return service.fastRootInActiveWindow();
        }
        return service.getRootInActiveWindow();
    }

    public void setWindowFilter(WindowFilter windowFilter) {
        mWindowFilter = windowFilter;
    }

    public abstract ActivityInfoProvider getInfoProvider();

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getFlags() {
        return mFlags;
    }

    public void setFlags(int flags) {
        mFlags = flags;
        if ((mFlags & FLAG_USE_USAGE_STATS) != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!AppOpsKt.isOpPermissionGranted(mContext, AppOpsManager.OPSTR_GET_USAGE_STATS)) {
                IntentUtil.requestAppUsagePermission(mContext);
                throw new SecurityException("没有\"查看使用情况\"权限");
            }
        }
        getInfoProvider().setUseUsageStats((mFlags & FLAG_USE_USAGE_STATS) != 0);
        getInfoProvider().setUseShell((mFlags & FLAG_USE_SHELL) != 0);
    }

    @NonNull
    public abstract AccessibilityNotificationObserver getNotificationObserver();

    public AccessibilityConfig getConfig() {
        return mConfig;
    }
}
