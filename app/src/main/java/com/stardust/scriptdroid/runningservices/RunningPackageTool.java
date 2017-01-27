package com.stardust.scriptdroid.runningservices;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/1/20.
 */

public class RunningPackageTool {


    public static String getRunningPackage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getRunningPackageLollipop(context);
        } else {
            return getRunningPackageBeforeLollipop(context);
        }

    }

    private static String getRunningPackageBeforeLollipop(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        ComponentName runningActivity = am.getRunningTasks(1).get(0).topActivity;
        return runningActivity.getPackageName();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getRunningPackageLollipop(Context context) {
        List<UsageStats> usageStats = getPastTwoSecondsUsageStats(context);
        UsageStats latestStats = findLatestUsageStats(usageStats);
        if (latestStats == null)
            return null;
        return latestStats.getPackageName();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void goToUsageAccessSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static UsageStats findLatestUsageStats(List<UsageStats> usageStats) {
        UsageStats latestStats = null;
        for (UsageStats us : usageStats) {
            if (latestStats == null || latestStats.getLastTimeUsed() < us.getLastTimeUsed()) {
                latestStats = us;
            }
        }
        return latestStats;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressWarnings("unchecked")
    private static List<UsageStats> getPastTwoSecondsUsageStats(Context context) {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 2000, ts);
        if (usageStats == null) {
            usageStats = Collections.EMPTY_LIST;
        }
        return usageStats;
    }
}
