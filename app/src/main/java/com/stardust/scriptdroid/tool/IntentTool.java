package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.R;

/**
 * Intent工具，用于Activity之间的跳转，调起其他应用程序等
 */
public class IntentTool {

    public static void goToQQ(Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void goToMail(Context context, String sendTo, @Nullable String title, @Nullable String content) {
        Uri uri = Uri.parse("mailto:" + sendTo);
        String[] email = {sendTo};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(intent);
    }

    public static void shareText(Context context, String text) {
        context.startActivity(new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain"));
    }
}
