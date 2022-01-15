package com.stardust.autojs.core.floaty;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.accessibility.AccessibilityService;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

import java.util.concurrent.CopyOnWriteArraySet;

public class AccessibilityFloatyService extends FloatyService {

    private static final CopyOnWriteArraySet<FloatyWindow> windows = new CopyOnWriteArraySet();
    private static AccessibilityFloatyService instance;
    private WindowManager mWindowManager;

    public static void addWindow(FloatyWindow window) {
        if (windows.add(window) && instance != null) {
            window.onCreate(instance, instance.mWindowManager);
        }

    }

    public static void removeWindow(FloatyWindow window) {
        windows.remove(window);
    }

    public void onCreate() {
        super.onCreate();
        if (AccessibilityService.Companion.getInstance() != null) {
            this.mWindowManager = AccessibilityService.Companion.getInstance().getWindowManager();
        }
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        }
        for (FloatyWindow delegate : windows) {
            delegate.onCreate(this, this.mWindowManager);
        }

        instance = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (AccessibilityService.Companion.getInstance() == null) {
            return null;
        }
        return AccessibilityService.Companion.getInstance().onBind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        instance = null;

        for (FloatyWindow delegate : windows) {
            delegate.onServiceDestroy(this);
        }

    }
}
