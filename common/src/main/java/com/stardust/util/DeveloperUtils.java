package com.stardust.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Stardust on 2017/4/5.
 */

public class DeveloperUtils {

    private static final String PACKAGE_NAME = "com.stardust.scriptdroid";
    private static final String SIGNATURE = "nPNPcy4Lk/eP6fLvZitP0VPbHdFCbKua77m59vis5fA=\n";

    public static void ensureRunningPackageNotSelf(@Nullable String runningPackage) {
        if (PACKAGE_NAME.equals(runningPackage)) {
            throw new SecurityException();
        }
    }

    public static boolean isSelfPackage(@Nullable String runningPackage) {
        return PACKAGE_NAME.equals(runningPackage);
    }

    @Nullable
    public static String getSignatureSHA(Context context, String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                final String sha = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                builder.append(sha);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法仅防止那些不会改源码直接用apk编辑器修改应用内字符串(QQ群号)等的恶意用户行为。
     * 为了开源社区的发展，请善用源码:-)
     */
    public static boolean checkSignature(Context context) {
        return SIGNATURE.equals(getSignatureSHA(context, context.getPackageName()));
    }

    public static boolean checkSignature(Context context, String packageName) {
        return SIGNATURE.equals(getSignatureSHA(context, packageName));
    }


    public static String selfPackage() {
        return PACKAGE_NAME;
    }


    public static boolean isActivityRegistered(Context context, Class<? extends Activity> c) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(new Intent(context, c),
                PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    public static boolean isServiceRegistered(Context context, Class<? extends Service> c) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(new Intent(context, c),
                PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }
}
