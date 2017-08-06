package com.stardust.autojs.runtime.accessibility;

/**
 * Created by Stardust on 2017/4/29.
 */

public class AutomatorConfig {

    private static boolean isUnintendedGuardEnabled = false;


    public static boolean isUnintendedGuardEnabled() {
        return isUnintendedGuardEnabled;
    }

    public static void setIsUnintendedGuardEnabled(boolean isUnintendedGuardEnabled) {
        AutomatorConfig.isUnintendedGuardEnabled = isUnintendedGuardEnabled;
    }
}
