package com.stardust.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.File;


public class IntentUtil {

    public static boolean chatWithQQ(Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void sendMailTo(Context context, String sendTo, @Nullable String title, @Nullable String content) {
        Uri uri = Uri.parse("mailto:" + sendTo);
        String[] email = {sendTo};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_CC, email);
        if (title != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        if (content != null)
            intent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(intent, ""));
    }

    public static void sendMailTo(Context context, String sendTo) {
        sendMailTo(context, sendTo, null, null);
    }

    public static boolean browse(Context context, String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ignored) {
            return false;
        }

    }

    public static void shareText(Context context, String text) {
        context.startActivity(new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain"));
    }

    public static boolean goToAppDetailSettings(Context context, String packageName) {
        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse("package:" + packageName));
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException ignored) {
            return false;
        }
    }

    public static boolean goToAppDetailSettings(Context context) {
        return goToAppDetailSettings(context, context.getPackageName());
    }

    public static void installApk(Context context, String path, String fileProviderAuthority) {
        Uri uri = getUriOfFile(context, path, fileProviderAuthority);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static void viewFile(Context context, String path, String fileProviderAuthority) {
        String mimeType = MimeTypes.fromFileOr(path, "*/*");
        viewFile(context, path, mimeType, fileProviderAuthority);
    }

    public static Uri getUriOfFile(Context context, String path, String fileProviderAuthority) {
        Uri uri;
        if (fileProviderAuthority == null) {
            uri = Uri.parse("file://" + path);
        } else {
            uri = FileProvider.getUriForFile(context, fileProviderAuthority, new File(path));
        }
        return uri;
    }

    public static void viewFile(Context context, String path, String mimeType, String fileProviderAuthority) {
        Uri uri = getUriOfFile(context, path, fileProviderAuthority);
        context.startActivity(new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
    }

    public static void editFile(Context context, String path, String fileProviderAuthority) {
        String mimeType = MimeTypes.fromFileOr(path, "*/*");
        Uri uri = getUriOfFile(context, path, fileProviderAuthority);
        context.startActivity(new Intent(Intent.ACTION_EDIT)
                .setDataAndType(uri, mimeType)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
    }
}
