package com.stardust.util;

import android.support.annotation.Nullable;

/**
 * Created by Stardust on 2017/4/5.
 */

public class DeveloperUtils {

    private static final String PACKAGE_NAME = "com.stardust.scriptdroid";

    public static void ensureRunningPackageNotSelf(@Nullable String runningPackage) {
        if (PACKAGE_NAME.equals(runningPackage)) {
            throw new SecurityException();
        }
    }

    public static boolean isSelfPackage(@Nullable String runningPackage) {
        return PACKAGE_NAME.equals(runningPackage);
    }
}
