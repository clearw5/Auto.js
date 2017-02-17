package com.stardust.scriptdroid.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.scriptdroid.App;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityWatchDogService extends AccessibilityService {

    private static final String TAG = "AccessibilityWatchDog";

    private static final SortedMap<Integer, AccessibilityDelegate> mDelegates = new TreeMap<>();
    private static WeakReference<AccessibilityWatchDogService> instance;

    public static void addDelegate(AccessibilityDelegate delegate, int uniquePriority) {
        synchronized (mDelegates) {
            mDelegates.put(uniquePriority, delegate);
        }
    }

    public static boolean containsPriority(int priority) {
        synchronized (mDelegates) {
            return mDelegates.containsKey(priority);
        }
    }

    public static AccessibilityDelegate getDelegate(int priority) {
        synchronized (mDelegates) {
            return mDelegates.get(priority);
        }
    }

    public static void addDelegateIfNeeded(int priority, Class<? extends AccessibilityDelegate> delegateClass) {
        try {
            addDelegateIfNeeded(priority, delegateClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addDelegateIfNeeded(int priority, AccessibilityDelegate delegate) {
        synchronized (mDelegates) {
            if (!mDelegates.containsKey(priority)) {
                mDelegates.put(priority, delegate);
            }
        }
    }

    public static boolean isEnable() {
        return AccessibilityServiceUtils.isAccessibilityServiceEnabled(App.getApp(), AccessibilityWatchDogService.class);
    }

    public static AccessibilityWatchDogService getInstance() {
        if (instance == null)
            return null;
        return instance.get();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent: " + event);
        synchronized (mDelegates) {
            for (Map.Entry<Integer, AccessibilityDelegate> entry : mDelegates.entrySet()) {
                if (entry.getValue().onAccessibilityEvent(this, event))
                    break;
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // FIXME: 2017/2/12 有时在无障碍中开启服务后这里不会调用服务也不会运行，安卓的BUG???
        Log.v(TAG, "onServiceConnected");
        instance = new WeakReference<>(this);
    }

    public static void disable() {
        if (instance != null && instance.get() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instance.get().disableSelf();
        } else {
            AccessibilityServiceUtils.goToAccessibilitySetting(App.getApp());
        }
    }

}
