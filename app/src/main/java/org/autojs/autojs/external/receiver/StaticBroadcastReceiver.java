package org.autojs.autojs.external.receiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticBroadcastReceiver extends BaseBroadcastReceiver {

    static final List<String> ACTIONS = new ArrayList<>(Arrays.asList(
            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.QUICKBOOT_POWERON",
            "android.intent.action.TIME_SET",
            "android.intent.action.TIMEZONE_CHANGED",
            "android.intent.action.PACKAGE_ADDED",
            "android.intent.action.PACKAGE_CHANGED",
            "android.intent.action.PACKAGE_DATA_CLEARED",
            "android.intent.action.PACKAGE_REMOVED",
            "android.intent.action.PACKAGE_RESTARTED",
            "android.intent.action.UID_REMOVED",
            "android.intent.action.ACTION_POWER_CONNECTED",
            "android.intent.action.ACTION_POWER_DISCONNECTED",
            "android.intent.action.ACTION_SHUTDOWN",
            "android.intent.action.DATE_CHANGED",
            "android.intent.action.DREAMING_STARTED",
            "android.intent.action.DREAMING_STOPPED",
            "android.intent.action.HEADSET_PLUG",
            "android.intent.action.INPUT_METHOD_CHANGED",
            "android.intent.action.LOCALE_CHANGED",
            "android.intent.action.MEDIA_BUTTON",
            "android.intent.action.MEDIA_CHECKING",
            "android.intent.action.MEDIA_MOUNTED",
            "android.intent.action.PACKAGE_FIRST_LAUNCH",
            "android.intent.action.PROVIDER_CHANGED",
            "android.intent.action.WALLPAPER_CHANGED",
            "android.intent.action.USER_UNLOCKED",
            "android.intent.action.USER_PRESENT",
            "android.net.conn.CONNECTIVITY_CHANGE"
    ));

    static final List<String> PACKAGE_ACTIONS = new ArrayList<>(Arrays.asList(
            "android.intent.action.PACKAGE_ADDED",
            "android.intent.action.PACKAGE_CHANGED",
            "android.intent.action.PACKAGE_DATA_CLEARED",
            "android.intent.action.PACKAGE_REMOVED",
            "android.intent.action.PACKAGE_RESTARTED"
    ));

}
