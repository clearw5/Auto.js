package com.stardust.scriptdroid.layout_inspector;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.util.UnderuseExecutors;
import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/3/10.
 *
 */

// TODO: 2017/5/8

public class LayoutInspector {

    private volatile NodeInfo mCapture;
    private volatile boolean mDumping = false;
    private Executor mExecutor = UnderuseExecutors.getExecutor();

    public void captureCurrentWindow() {
        AccessibilityService service = AccessibilityWatchDogService.getInstance();
        if (service == null) {
            mCapture = null;
        } else {
            final AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                mCapture = null;
            } else {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDumping = true;
                        mCapture = NodeInfo.capture(root);
                        mDumping = false;
                    }
                });
            }
        }
    }

    public boolean isDumping() {
        return mDumping;
    }

    public void clearCapture() {
        mCapture = null;
    }

    public NodeInfo getCapture() {
        return mCapture;
    }
}
