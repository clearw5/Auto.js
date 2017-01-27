package com.stardust.scriptdroid.shortcut;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.stardust.scriptdroid.R;

import java.util.ArrayList;
import java.util.List;

public class ShortcutHelper {
    public final static String TAG = ShortcutHelper.class.getSimpleName();
    public static final String ACTION_DESKTOP_LINK = "com.scu.zqc.action.SHORTCUT";
    public static final String READ_SETTINGS = "com.android.launcher.permission.READ_SETTINGS";
    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    public static final String MAIN_ACTIVITY = "com.scu.shortcut.MainActivity";
    public static final String APP_STORE_URL = "http://m.app.so.com/?src=browser";
    public static final String EXTRA_DUPLICATE = "duplicate";

    public static void addShortCut(final Context context, Bitmap src) {
        String appStoreShortcutName = context.getString(R.string.app_name);
        if (getIsAddShortCut(context, appStoreShortcutName)) {
//            uninstallShortcut(context, appStoreShortcutName);
            try {
                int size = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, size, size, true);
                createFavLinkShortCut(context, appStoreShortcutName, APP_STORE_URL, scaledBitmap, false, true);
            } catch (Exception e) {
            }
        }
    }

    public static boolean uninstallShortcut(Context context, String name, Intent shortCut) {
        Intent intent = new Intent(ACTION_UNINSTALL_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCut);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        context.sendBroadcast(intent);
        return true;
    }

    public static boolean createFavLinkShortCut(Context context, String name, String url, Parcelable icon,
                                                boolean isSendToQihooDesktop, boolean skipCheckExist) {
        if (context == null || TextUtils.isEmpty(url) || TextUtils.isEmpty(name) || icon == null) {
            return false;
        }
        // 先查询launcher页面是否已经生成其快捷方式
        if (getIsAddShortCut(context, name) && !skipCheckExist) {
            return false;
        }

        Intent shortcutIntent = getShortCutIntent(context, url, MAIN_ACTIVITY);
        shortcutIntent.setAction(ACTION_DESKTOP_LINK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent installIntent = getInstallIntent(shortcutIntent, name, icon);

        // 将创建快捷方式的intent指定到桌面应用程序包来处理.
        String deskPackageName = getLauncherPackageName(context);
        if (null != deskPackageName && !TextUtils.isEmpty(deskPackageName)) {
            installIntent.setPackage(deskPackageName);
        }

        if (isSendToQihooDesktop) {
            installIntent.putExtra("from", context.getPackageName());
        }

        context.sendBroadcast(installIntent);
        return true;
    }

    /**
     * 获取正在运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
     */
    public static String getLauncherPackageName(Context context) {
        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
            if (res != null && res.activityInfo == null) {
                // should not happen. A home is always installed, isn't it?
                return null;
            }

            if (res != null && ("android").equals(res.activityInfo.packageName)) {
                // 有多个桌面程序存在，且未指定默认项时；
                return null;
            } else {
                return res.activityInfo.packageName;
            }
        } catch (Exception e) {

        }

        return null;
    }

    private static Intent getInstallIntent(Context context, Intent shortcutIntent, int name, int res) {
        Intent intent = getInstallIntent(shortcutIntent, context.getString(name), null);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, res);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        return intent;
    }

    private static Intent getInstallIntent(Intent shortcutIntent, String name, Parcelable iconResource) {
        Intent intent = new Intent(ACTION_INSTALL_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        if (TextUtils.isEmpty(name)) {
            if (Build.VERSION.SDK_INT < 11 || "SM-N9008V".equals(Build.MODEL)) {
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "添加");
            } else {
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "\t");
            }
        } else {
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        }
        if (iconResource != null) {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconResource);
        }
        intent.putExtra(EXTRA_DUPLICATE, false);
        return intent;
    }

    //判断应用是否安装
    public static boolean isPkgInstalled(Context c, String pkgName) {
        PackageManager mPm = c.getPackageManager();
        if (mPm == null) {
            return false;
        }
        PackageInfo pkginfo = null;
        try {
            pkginfo = mPm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }
        return pkginfo == null ? false : true;
    }

    public static Intent getShortCutIntent(Context context, String action, String className) {
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        if ("NX511J".equals(Build.MODEL)) {
            shortcutIntent.setPackage(context.getPackageName());
        } else {
            shortcutIntent.setClassName(context, className);
        }
        shortcutIntent.setData(Uri.parse(action));
        shortcutIntent.putExtra("value", "test");
        return shortcutIntent;
    }

    public static boolean createShortCutWithIntentInSilence(Context context, String name, Intent shortCutIntent, Parcelable icon, boolean isSendToQihooDesktop) {
        if (context == null || TextUtils.isEmpty(name) || icon == null) {
            return false;
        }
        // 先查询launcher页面是否已经生成其快捷方式
        if (getIsAddShortCut(context, name)) {
            return false;
        }
        Intent installIntent = getInstallIntent(shortCutIntent, name, icon);
        // 将创建快捷方式的intent指定到桌面应用程序包来处理.
        String deskPackageName = getLauncherPackageName(context);
        if (null != deskPackageName && !TextUtils.isEmpty(deskPackageName)) {
            installIntent.setPackage(deskPackageName);
        }
        if (isSendToQihooDesktop) {
            installIntent.putExtra("from", context.getPackageName());
        }
        context.sendBroadcast(installIntent);
        return true;
    }

    public static boolean createFavLinkShortCutWithShortcutIntent(Context context, String name, String url, Parcelable icon,
                                                                  boolean isSendToQihooDesktop, boolean skipCheckExist, Intent shortcutIntent) {

        if (context == null || TextUtils.isEmpty(url) || TextUtils.isEmpty(name) || icon == null) {
            return false;
        }

        // 先查询launcher页面是否已经生成其快捷方式
        if (getIsAddShortCut(context, name) && !skipCheckExist) {
            return false;
        }

        Intent installIntent = getInstallIntent(shortcutIntent, name, icon);

        // 将创建快捷方式的intent指定到桌面应用程序包来处理.
        String deskPackageName = getLauncherPackageName(
                context);

        if (null != deskPackageName && !TextUtils.isEmpty(deskPackageName)) {
            installIntent.setPackage(deskPackageName);
        }

        if (isSendToQihooDesktop) {
            installIntent.putExtra("from", context.getPackageName());
        }

        context.sendBroadcast(installIntent);
        return true;
    }

    public static boolean getIsAddShortCut(Context context, String name) {
        // 如果只是按照title来比较是否存在的话可能存在重名应用，所以还需要根据intent这个字段一起判断。
        String intentTag = "com.scu.zqc";
        Cursor c = null;
        try {
            final ContentResolver cr = context.getContentResolver();
            String AUTHORITY = getAuthority(context);
            Log.d(TAG, "AUTHORITY = " + AUTHORITY);

            if (null == AUTHORITY) {
                AUTHORITY = getSuitableAuthority(context);
            }

            final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"},
                    "title=? and intent like ?", new String[]{name, "%" + intentTag + "%"}, null);

            if (null != c && c.getCount() > 0) {
                Log.d(TAG, "the shortCut of" + name + "has been created!");
                return true;
            } else {
                if (c != null) {
                    c.close();
                }
                // 有的手机上需要用下面的方法才能获取到快捷方式是否存在
                AUTHORITY = getAuthorityFromPermission(context, READ_SETTINGS);
                final Uri CONTENT_URI_AUTHORITY = Uri.parse("content://" + AUTHORITY +
                        "/favorites?notify=true");
                c = cr.query(CONTENT_URI_AUTHORITY, new String[]{"title", "iconResource"},
                        "title=? and intent like ?", new String[]{name, "%" + intentTag + "%"}, null);

                if (null != c && c.getCount() > 0) {
                    Log.d(TAG, "the shortCut of" + name + "has been created!");
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            return getIsAddShortCutFromPermission(context, name);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return false;
    }

    private static boolean getIsAddShortCutFromPermission(Context context, String name) {
        Cursor c = null;
        String intentTag = context.getPackageName();
        String AUTHORITY = getAuthorityFromPermission(context, READ_SETTINGS);
        try {
            final ContentResolver cr = context.getContentResolver();
            final Uri CONTENT_URI_AUTHORITY = Uri.parse("content://" + AUTHORITY +
                    "/favorites?notify=true");
            c = cr.query(CONTENT_URI_AUTHORITY, new String[]{"title", "iconResource"},
                    "title=? and intent like ?", new String[]{name, "%" + intentTag + "%"}, null);
            if (null != c && c.getCount() > 0) {
                Log.d(TAG, "the shortCut of" + name + "has been created!");
                return true;
            }
        } catch (Exception e) {
            // : handle exception
            Log.e(TAG, "exception: " + e.getMessage());
        } finally {
            if (null != c) {
                c.close();
            }
        }

        return false;
    }

    /**
     * 通过permission来找不同机型的Authority
     *
     * @return
     */
    public static String getAuthority(Context context) {
        String authority = ".launcher.settings";
        String authority2 = ".launcher2.settings";
        String authority3 = ".launcher3.settings";
        List<PackageInfo> packs = null;
        try {
            packs = context.getPackageManager().getInstalledPackages(
                    PackageManager.GET_PROVIDERS);

            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;

                if (null != providers) {
                    for (ProviderInfo provider : providers) {
                        if (!TextUtils.isEmpty(provider.authority) && (provider.authority.contains(authority) ||
                                provider.authority.contains(authority2) ||
                                provider.authority.contains(authority3))) {
                            return provider.authority;
                        }
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            packs = null;
        }
        return null;
    }

    private static String getAuthorityFromPermission(Context context,
                                                     String permission) {
        if (permission == null) {
            return null;
        }
        try {
            List<PackageInfo> packs = context.getPackageManager()
                    .getInstalledPackages(PackageManager.GET_PROVIDERS);

            if (packs != null) {
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;

                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (permission.equals(provider.readPermission)) {
                                return provider.authority;
                            }

                            if (permission.equals(provider.writePermission)) {
                                return provider.authority;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取合适的Authority访问launcher数据库<br/>
     * 优先级：".launcher3.settings" > ".launcher2.settings" > ".launcher.settings" > ".settings"
     */
    public static String getSuitableAuthority(Context context) {
        String AUTHORITY = null;
        String authority = ".launcher.settings";
        String authority2 = ".launcher2.settings";
        String authority3 = ".launcher3.settings";
        String authority4 = ".settings";

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(
                PackageManager.GET_PROVIDERS);

        List<String> authorityList = new ArrayList<String>();
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;

            if (null != providers) {
                for (ProviderInfo provider : providers) {
                    if (!TextUtils.isEmpty(provider.authority) && (provider.authority.contains(authority)
                            || provider.authority.contains(authority2) ||
                            provider.authority.contains(authority3))) {
                        authorityList.add(provider.authority);
                    }
                }
            }
        }
        String AUTHORITY_TEMP_1 = null;
        String AUTHORITY_TEMP_2 = null;
        String AUTHORITY_TEMP_3 = null;

        for (String item : authorityList) {
            if (item.contains(authority3) && TextUtils.isEmpty(AUTHORITY_TEMP_3)) {
                AUTHORITY_TEMP_3 = item;
            } else if (item.contains(authority2) && TextUtils.isEmpty(AUTHORITY_TEMP_2)) {
                AUTHORITY_TEMP_2 = item;
            } else if (item.contains(authority) && TextUtils.isEmpty(AUTHORITY_TEMP_1)) {
                AUTHORITY_TEMP_1 = item;
            }
        }
        if (!TextUtils.isEmpty(AUTHORITY_TEMP_3)) {
            AUTHORITY = AUTHORITY_TEMP_3;//优先适配launcher3
        } else if (!TextUtils.isEmpty(AUTHORITY_TEMP_2)) {
            AUTHORITY = AUTHORITY_TEMP_2;
        } else if (!TextUtils.isEmpty(AUTHORITY_TEMP_1)) {
            AUTHORITY = AUTHORITY_TEMP_1;
        }
        if (TextUtils.isEmpty(AUTHORITY)) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;

                if (null != providers) {
                    for (ProviderInfo provider : providers) {
                        if (!TextUtils.isEmpty(provider.authority) && provider.authority.endsWith(authority4)) {
                            return provider.authority;
                        }
                    }
                }
            }
        }
        return AUTHORITY;
    }
}
