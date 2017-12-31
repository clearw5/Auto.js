package com.stardust.scriptdroid.build;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.stardust.BuildConfig;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.DeveloperUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Stardust on 2017/11/29.
 */

public class ApkBuilderPluginHelper {

    private static final String PLUGIN_PACKAGE_NAME = "org.autojs.apkbuilderplugin";
    private static final String TEMPLATE_APK_PATH = "template.apk";

    public static boolean checkPlugin(Context context) {
        return DeveloperUtils.checkSignature(context, PLUGIN_PACKAGE_NAME);
    }

    public static InputStream openTemplateApk(Context context) {
        try {
            //if (BuildConfig.DEBUG) {
            //  return context.getAssets().open(TEMPLATE_APK_PATH);
            //}
            return context.getPackageManager().getResourcesForApplication(PLUGIN_PACKAGE_NAME)
                    .getAssets().open(TEMPLATE_APK_PATH);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
