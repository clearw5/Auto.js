package com.stardust.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Stardust on 2017/8/4.
 */

public class DialogUtils {

    public static <T extends Dialog> T showDialog(final T dialog) {
        Context context = dialog.getContext();

        if (!isActivityContext(context)) {
            Window window = dialog.getWindow();
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            if (window != null)
                window.setType(type);
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            dialog.show();
        } else {
            GlobalAppContext.post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }
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
