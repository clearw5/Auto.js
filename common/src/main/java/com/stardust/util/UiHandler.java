package com.stardust.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Stardust on 2017/5/2.
 */

public class UiHandler extends Handler {


    private Context mContext;

    public UiHandler(Context context) {
        super(Looper.getMainLooper());
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void toast(final String message) {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toast(final int resId) {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
