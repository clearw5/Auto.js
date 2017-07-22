package com.stardust.view.accessibility;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Stardust on 2017/5/2.
 */

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {


    private static final String TAG = "AccessibilityService";

    private static final SortedMap<Integer, AccessibilityDelegate> mDelegates = new TreeMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition ENABLED = LOCK.newCondition();
    private static AccessibilityService instance;
    private static final OnKeyListener.Observer stickOnKeyObserver = new OnKeyListener.Observer();
    private static boolean containsAllEventTypes = false;
    private static final Set<Integer> eventTypes = new HashSet<>();
    private OnKeyListener.Observer mOnKeyObserver = new OnKeyListener.Observer();
    private Handler mHandler;
    private ExecutorService mKeyEventExecutor;

    public static void addDelegate(int uniquePriority, AccessibilityDelegate delegate) {
        mDelegates.put(uniquePriority, delegate);
        Set<Integer> set = delegate.getEventTypes();
        if (set == null)
            containsAllEventTypes = true;
        else
            eventTypes.addAll(set);
    }

    public static boolean isEnabled(Context context) {
        return AccessibilityServiceUtils.isAccessibilityServiceEnabled(context, AccessibilityService.class);
    }

    public static AccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent: " + event);
        if (!containsAllEventTypes && !eventTypes.contains(event.getEventType()))
            return;
        for (Map.Entry<Integer, AccessibilityDelegate> entry : mDelegates.entrySet()) {
            AccessibilityDelegate delegate = entry.getValue();
            Set<Integer> types = delegate.getEventTypes();
            if (types != null && !delegate.getEventTypes().contains(event.getEventType()))
                continue;
            long start = System.currentTimeMillis();
            if (delegate.onAccessibilityEvent(AccessibilityService.this, event))
                break;
            Log.v(TAG, "millis: " + (System.currentTimeMillis() - start) + " delegate: " + entry.getValue().getClass().getName());
        }
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(final KeyEvent event) {
        if (mKeyEventExecutor == null) {
            mKeyEventExecutor = Executors.newSingleThreadExecutor();
        }
        mKeyEventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stickOnKeyObserver.onKeyEvent(event.getKeyCode(), event);
                mOnKeyObserver.onKeyEvent(event.getKeyCode(), event);
            }
        });
        return false;
    }

    public OnKeyListener.Observer getOnKeyObserver() {
        return mOnKeyObserver;
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        try {
            return super.getRootInActiveWindow();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        instance = null;
        if (mKeyEventExecutor != null)
            mKeyEventExecutor.shutdownNow();
        super.onDestroy();
    }


    @Override
    protected void onServiceConnected() {
        Log.v(TAG, "onServiceConnected: " + getServiceInfo().toString());
        instance = this;
        super.onServiceConnected();
        LOCK.lock();
        ENABLED.signalAll();
        LOCK.unlock();
        mHandler = new Handler();
        // FIXME: 2017/2/12 有时在无障碍中开启服务后这里不会调用服务也不会运行，安卓的BUG???
    }

    private AccessibilityNodeInfo superGetRootInActiveWindow() {
        try {
            return super.getRootInActiveWindow();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean disable() {
        if (instance != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instance.disableSelf();
            return true;
        }
        return false;
    }

    public static void waitForEnabled(long timeOut) {
        LOCK.lock();
        try {
            ENABLED.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    public static OnKeyListener.Observer getStickOnKeyObserver() {
        return stickOnKeyObserver;
    }
}
