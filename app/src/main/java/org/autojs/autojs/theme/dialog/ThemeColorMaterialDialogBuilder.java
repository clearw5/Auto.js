package org.autojs.autojs.theme.dialog;

import android.content.Context;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorMutable;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ThemeColorMaterialDialogBuilder extends MaterialDialog.Builder implements ThemeColorMutable {
    public ThemeColorMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        ThemeColorManager.add(this);
    }

    @Override
    public void setThemeColor(ThemeColor themeColor) {
        int color = themeColor.colorPrimary;
        positiveColor(color);
        negativeColor(color);
        neutralColor(color);
    }
}
