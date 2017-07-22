package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.stardust.autojs.runtime.ScriptInterface;
import com.stardust.util.IntentUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.stardust.pio.PFile.getExtension;

/**
 * Created by Stardust on 2017/4/2.
 */

public class AppUtils {

    private Context mContext;
    private volatile WeakReference<Activity> mCurrentActivity = new WeakReference<>(null);

    public AppUtils(Context context) {
        mContext = context;
    }

    @ScriptInterface
    public boolean launchPackage(String packageName) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            mContext.startActivity(packageManager.getLaunchIntentForPackage(packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @ScriptInterface
    public boolean launchApp(String appName) {
        return launchPackage(getPackageName(appName));
    }

    @ScriptInterface
    public String getPackageName(String appName) {
        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString().equals(appName)) {
                return applicationInfo.processName;
            }
        }
        return null;
    }

    @ScriptInterface
    public boolean openAppSetting(String packageName) {
        return IntentUtil.goToAppDetailSettings(mContext, packageName);
    }

    @Nullable
    public Activity getCurrentActivity() {
        Log.d("App", "getCurrentActivity: " + mCurrentActivity.get());
        return mCurrentActivity.get();
    }

    @ScriptInterface
    public void uninstall(String packageName) {
        mContext.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ScriptInterface
    public void viewFile(String path) {
        if (path == null)
            throw new NullPointerException("path == null");
        path = "file://" + path;
        String ext = getExtension(path);
        String mimeType = TextUtils.isEmpty(ext) ? "*/*" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        mContext.startActivity(new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse(path), mimeType)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ScriptInterface
    public void editFile(String path) {
        if (path == null)
            throw new NullPointerException("path == null");
        path = "file://" + path;
        String ext = getExtension(path);
        String mimeType = TextUtils.isEmpty(ext) ? "*/*" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        mContext.startActivity(new Intent(Intent.ACTION_EDIT)
                .setDataAndType(Uri.parse(path), mimeType)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ScriptInterface
    public void openUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        mContext.startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void setCurrentActivity(Activity currentActivity) {
        mCurrentActivity = new WeakReference<>(currentActivity);
        Log.d("App", "setCurrentActivity: " + currentActivity);
    }
}
