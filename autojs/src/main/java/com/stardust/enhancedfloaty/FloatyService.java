package com.stardust.enhancedfloaty;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.floaty.RawWindow;
import com.taobao.idlefish.AccessibilityService;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/5/1.
 */
public class FloatyService extends Service {

    private static CopyOnWriteArraySet<FloatyWindow> windows = new CopyOnWriteArraySet<>();

    private static FloatyService instance;
    private WindowManager mWindowManager;
    private WindowManager accessibilityWindowManager;

    public static void addWindow(FloatyWindow window) {
        if (windows.add(window) && instance != null) {
            instance.appendWindow(window);
        }
    }

    public static void removeWindow(FloatyWindow window) {
        windows.remove(window);
    }

    public static FloatyService getInstance() {
        return instance;
    }

    /**
     * 根据无障碍链接情况刷新windowManager
     */
    public void refreshAccessWindowManager() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // 仅仅针对安卓12优化
            return;
        }
        if (AccessibilityService.Companion.getInstance() != null) {
            accessibilityWindowManager = AccessibilityService.Companion.getInstance().getWindowManager();
        } else {
            accessibilityWindowManager = null;
        }
        // TODO 目前设计当无障碍服务丢失后 RawWindow会自动移除 后续考虑将window重新添加 但是目前没有完美的方法
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        refreshAccessWindowManager();
        instance = this;
        for (FloatyWindow delegate : windows) {
            appendWindow(delegate);
        }
    }

    private void appendWindow(FloatyWindow window) {
        if (instance == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                instance.accessibilityWindowManager != null && window instanceof RawWindow) {
            window.onCreate(instance, instance.accessibilityWindowManager);
        } else {
            window.onCreate(instance, instance.mWindowManager);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        for (FloatyWindow delegate : windows) {
            delegate.onServiceDestroy(this);
        }
    }
}
