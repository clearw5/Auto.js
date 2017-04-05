package com.stardust.scriptdroid.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.view.accessibility.AccessibilityDelegate;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityWatchDogService extends AccessibilityService {

    private static final String TAG = "AccessibilityWatchDog";

    private static final SortedMap<Integer, AccessibilityDelegate> mDelegates = new TreeMap<>();
    private static WeakReference<AccessibilityWatchDogService> instance;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public static void addDelegate(int uniquePriority, AccessibilityDelegate delegate) {
        mDelegates.put(uniquePriority, delegate);
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
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent: " + event);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Integer, AccessibilityDelegate> entry : mDelegates.entrySet()) {
                    Log.v(TAG, "delegate: " + entry.getValue().getClass().getName());
                    if (entry.getValue().onAccessibilityEvent(AccessibilityWatchDogService.this, event))
                        break;
                }
            }
        });
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
            AccessibilityServiceTool.goToAccessibilitySetting();
        }
    }

}
