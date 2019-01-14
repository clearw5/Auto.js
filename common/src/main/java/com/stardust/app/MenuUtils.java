package com.stardust.app;

import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class MenuUtils {

    public static void setMenuIconColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null) {
                DrawableCompat.setTint(icon, color);
            }
            SubMenu subMenu = item.getSubMenu();
            if (subMenu != null) {
                setMenuIconColor(subMenu, color);
            }
        }
    }
}
