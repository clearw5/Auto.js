package com.stardust.automator.simple_action;

import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class SimpleAction {


    private volatile AccessibilityNodeInfoAllocator mAllocator = AccessibilityNodeInfoAllocator.NONE;
    private volatile boolean mValid = true;
    private volatile boolean mResult = false;

    public abstract boolean perform(AccessibilityNodeInfo root);

    public boolean getResult() {
        return mResult;
    }

    public void setResult(boolean result) {
        mResult = result;
    }

    public void setValid(boolean valid) {
        mValid = valid;
    }

    public boolean isValid() {
        return mValid;
    }

    public AccessibilityNodeInfoAllocator getAllocator() {
        return mAllocator;
    }

    public void setAllocator(@NonNull AccessibilityNodeInfoAllocator allocator) {
        mAllocator = allocator;
    }

}
