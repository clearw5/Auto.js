package com.stardust.autojs.runtime.accessibility;

import android.util.ArraySet;

import com.stardust.util.DeveloperUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Stardust on 2017/4/29.
 */

public class AccessibilityConfig {

    private static boolean isUnintendedGuardEnabled = false;

    private List<String> mWhiteList = new ArrayList<>();
    private boolean mSealed = false;

    public AccessibilityConfig() {
        if (isUnintendedGuardEnabled()) {
            mWhiteList.add(DeveloperUtils.selfPackage());
        }
    }

    public static boolean isUnintendedGuardEnabled() {
        return isUnintendedGuardEnabled;
    }

    public static void setIsUnintendedGuardEnabled(boolean isUnintendedGuardEnabled) {
        AccessibilityConfig.isUnintendedGuardEnabled = isUnintendedGuardEnabled;
    }

    public boolean whiteListContains(String packageName) {
        return mWhiteList.contains(packageName);
    }

    public void addWhiteList(String packageName) {
        if (mSealed)
            throw new IllegalStateException("sealed");
        mWhiteList.add(packageName);
    }

    public final void seal() {
        mSealed = true;
    }
}
