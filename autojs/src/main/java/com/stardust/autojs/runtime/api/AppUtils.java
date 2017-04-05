package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.stardust.autojs.runtime.JavascriptInterface;
import com.stardust.util.IntentUtil;

import java.util.List;

/**
 * Created by Stardust on 2017/4/2.
 */

public class AppUtils {

    private Context mContext;

    public AppUtils(Context context) {
        mContext = context;
    }


    @JavascriptInterface
    public boolean launchPackage(String packageName) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            mContext.startActivity(packageManager.getLaunchIntentForPackage(packageName));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @JavascriptInterface
    public boolean launchApp(String appName) {
        return launchPackage(getPackageName(appName));
    }

    @JavascriptInterface
    public String getPackageName(String appName) {
        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString().equals(appName)) {
                return applicationInfo.processName;
            }
        }
        return "";
    }

    @JavascriptInterface
    public boolean openAppSetting(String packageName) {
        return IntentUtil.goToAppDetailSettings(mContext, packageName);
    }


}
