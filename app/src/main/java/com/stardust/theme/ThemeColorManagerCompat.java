package com.stardust.theme;

import com.stardust.scriptdroid.*;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/3/12.
 */

public class ThemeColorManagerCompat {

    public static int getColorPrimary() {
        int color = ThemeColorManager.getColorPrimary();
        if (color == 0) {
            return App.getApp().getResources().getColor(R.color.colorPrimary);
        } else {
            return color;
        }
    }
}
