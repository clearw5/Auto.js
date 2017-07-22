package com.stardust.view.accessibility;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.util.UnderuseExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutInspector {

    private static final String LOG_TAG = LayoutInspector.class.getSimpleName();
    private volatile NodeInfo mCapture;
    private volatile boolean mDumping = false;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public void captureCurrentWindow() {
        AccessibilityService service = AccessibilityService.getInstance();
        if (service == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: service = null");
            mCapture = null;
        } else {
            final AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                Log.d(LOG_TAG, "captureCurrentWindow: root = null");
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void refreshChildList(AccessibilityNodeInfo root) {
        if (root == null)
            return;
        root.refresh();
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            refreshChildList(root.getChild(i));
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
