package com.stardust.scriptdroid.tool;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

/**
 * Intent工具，用于Activity之间的跳转，调起其他应用程序等
 */
public class IntentTool {

    public static boolean goToQQ(Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /****************
     * 发起添加群流程。群号：免Root脚本精灵交流群(556928653) 的 key 为： vjHXzZlpGcXNe-YEWzQ85mm_z8y-curC
     * 调用 joinQQGroup(vjHXzZlpGcXNe-YEWzQ85mm_z8y-curC) 即可发起手Q客户端申请加群 免Root脚本精灵交流群(556928653)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void goToMail(Context context, String sendTo, @Nullable String title, @Nullable String content) {
        Uri uri = Uri.parse("mailto:" + sendTo);
        String[] email = {sendTo};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_CC, email);
        if (title != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        if (content != null)
            intent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_choose_email_app)));
    }

    public static void goToMail(Context context, String sendTo) {
        goToMail(context, sendTo, null, null);
    }

    public static void goToLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void shareText(Context context, String text) {
        context.startActivity(new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain"));
    }

    public static boolean goToAppSetting(Context context, String packageName) {
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

    public static boolean goToAppSetting(Context context) {
        return goToAppSetting(context, context.getPackageName());
    }
}
