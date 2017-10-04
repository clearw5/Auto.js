package com.stardust.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Stardust on 2017/8/4.
 */

public class DialogUtils {

    public static <T extends Dialog> T showDialog(T dialog) {
        Context context = dialog.getContext();
        if (!isActivityContext(context)) {
            Window window = dialog.getWindow();
            if (window != null)
                window.setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.show();
        return dialog;
    }


    public static boolean isActivityContext(Context context) {
        if (context instanceof Activity)
            return true;
        if (context instanceof ContextWrapper) {
            return isActivityContext(((ContextWrapper) context).getBaseContext());
        }
        return false;
    }
}
