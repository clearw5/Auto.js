package org.autojs.autojs.ui.floating;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.util.FloatingPermission;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;
import org.autojs.autojs.App;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.floating.CircularMenu;
import com.stardust.util.IntentUtil;

import java.lang.ref.WeakReference;

import ezy.assist.compat.SettingsCompat;

import static com.stardust.autojs.util.FloatingPermission.manageDrawOverlays;

/**
 * Created by Stardust on 2017/9/30.
 */

public class FloatyWindowManger {

    private static WeakReference<CircularMenu> sCircularMenu;

    public static void addWindow(Context context, FloatyWindow window) {
        context.startService(new Intent(context, FloatyService.class));
        FloatingPermission.ensurePermissionGranted(context);
        try {
            FloatyService.addWindow(window);
            // SecurityException: https://github.com/hyb1996-guest/AutoJsIssueReport/issues/4781
        } catch (Exception e) {
            e.printStackTrace();
            manageDrawOverlays(context);
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();

        }
    }

    public static boolean isCircularMenuShowing() {
        return sCircularMenu != null && sCircularMenu.get() != null;
    }

    public static void showCircularMenuIfNeeded() {
        if (isCircularMenuShowing()) {
            return;
        }
        showCircularMenu();
    }

    public static boolean showCircularMenu() {
        if (!SettingsCompat.canDrawOverlays(GlobalAppContext.get())) {
            Toast.makeText(GlobalAppContext.get(), R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            manageDrawOverlays(GlobalAppContext.get());
            return false;
        } else {
            GlobalAppContext.get().startService(new Intent(GlobalAppContext.get(), FloatyService.class));
            CircularMenu menu = new CircularMenu(GlobalAppContext.get());
            sCircularMenu = new WeakReference<>(menu);
            return true;
        }
    }

    public static void hideCircularMenu() {
        if (sCircularMenu == null)
            return;
        CircularMenu menu = sCircularMenu.get();
        if (menu != null)
            menu.close();
        sCircularMenu = null;
    }
}
