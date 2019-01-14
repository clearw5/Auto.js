package com.stardust.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import android.widget.Toast;

/**
 * Created by Stardust on 2018/3/22.
 */

public class GlobalAppContext {

    @SuppressLint("StaticFieldLeak")
    private static Context sApplicationContext;
    private static Handler sHandler;

    public static void set(Application a) {
        sHandler = new Handler(Looper.getMainLooper());
        sApplicationContext = a.getApplicationContext();
    }

    public static Context get() {
        if (sApplicationContext == null)
            throw new IllegalStateException("Call GlobalAppContext.set() to set a application context");
        return sApplicationContext;
    }

    public static String getString(int resId) {
        return get().getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return get().getString(resId, formatArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getColor(int id) {
        return get().getColor(id);
    }

    public static void toast(final String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(get(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(get(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toast(final int resId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(get(), resId, Toast.LENGTH_SHORT).show();
            return;
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(get(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toast(final int resId, final Object... args) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(get(), getString(resId, args), Toast.LENGTH_SHORT).show();
            return;
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(get(), getString(resId, args), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    public static void postDelayed(Runnable r, long m) {
        sHandler.postDelayed(r, m);
    }
}
