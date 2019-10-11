package com.stardust.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import androidx.annotation.Nullable;
import android.util.Base64;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Stardust on 2017/4/5.
 */

public class DeveloperUtils {

    private static final String PACKAGE_NAME = "org.autojs.autojs";
    private static final String SIGNATURE = "nPNPcy4Lk/eP6fLvZitP0VPbHdFCbKua77m59vis5fA=";
    private static final String LOG_TAG = "DeveloperUtils";
    private static final ExecutorService sExecutor = UnderuseExecutors.getExecutor();
    private static final String SALT = "let\nlife\nbe\nbeautiful\nlike\nsummer\nflowers\nand\ndeath\nlike\nautumn\nleaves\n.";

    public static boolean isSelfPackage(@Nullable String runningPackage) {
        return PACKAGE_NAME.equals(runningPackage);
    }

    @Nullable
    public static String getSignatureSHA(Context context, String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = getPackageInfo(context, packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;
            Signature[] signatures = packageInfo.signatures;
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                final String sha = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                builder.append(sha);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PackageInfo getPackageInfo(Context context, String packageName, int flags) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 此方法仅防止那些不会改源码直接用apk编辑器修改应用内字符串(QQ群号)等的恶意用户行为。
     * 为了开源社区的发展，请善用源码:-)
     */
    public static boolean checkSignature(Context context) {
        return checkSignature(context, context.getPackageName());
    }

    public static boolean checkSignature(Context context, String packageName) {
        String sha = getSignatureSHA(context, packageName);
        if (sha == null)
            return false;
        if (sha.endsWith("\n")) {
            sha = sha.substring(0, sha.length() - 1);
        }
        return SIGNATURE.equals(sha);
    }


    public static String selfPackage() {
        return PACKAGE_NAME;
    }


    public static boolean isActivityRegistered(Context context, Class<? extends Activity> c) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packageInfo.activities;
            if (activities == null) {
                return false;
            }
            for (ActivityInfo info : activities) {
                if (c.getName().equals(info.name)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isServiceRegistered(Context context, Class<? extends Service> c) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
            ServiceInfo[] activities = packageInfo.services;
            if (activities == null) {
                return false;
            }
            for (ServiceInfo info : activities) {
                if (c.getName().equals(info.name)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkDexFile(Context context, long[] crc) {
        String apkPath = context.getPackageCodePath();
        try {
            ZipFile zipFile = new ZipFile(apkPath);
            for (int i = 0; i < crc.length; i++) {
                String dexFile;
                if (i == 0) {
                    dexFile = "classes.dex";
                } else {
                    dexFile = "classes" + (i + 1) + ".dex";
                }
                ZipEntry dexEntry = zipFile.getEntry(dexFile);
                long dexEntryCrc = dexEntry.getCrc();
                //Log.d(LOG_TAG, String.valueOf(dexEntryCrc));
                if (dexEntryCrc != crc[i]) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void verifyApk(Activity activity, final int crcRes) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Activity a = activityWeakReference.get();
                if (a == null)
                    return;
                if (!checkSignature(a)) {
                    a.finish();
                    return;
                }
                //long[] crc = readCrc(a.getString(crcRes));
                //if (!checkDexFile(a, crc)) {
                //a.finish();
                //}

            }
        });
    }

    private static long[] readCrc(String crcStr) {
        String[] crcStrings = crcStr.split("\n");
        StringBuilder iHash = new StringBuilder();
        long[] crc = new long[crcStrings.length - 1];
        for (int i = 0; i < crc.length; i++) {
            crc[i] = Long.parseLong(crcStrings[i]);
            iHash.append(iHash(crcStrings[i]));
        }
        if (!crcStrings[crcStrings.length - 1].equals(iHash.toString())) {
            return new long[crc.length];
        }
        return crc;
    }

    private static String iHash(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < 8; i++) {
                data = Base64.encodeToString(md5.digest((data + SALT).getBytes()), Base64.NO_WRAP);
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
