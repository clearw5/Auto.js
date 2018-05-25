package org.autojs.autojs.theme;

import com.stardust.app.GlobalAppContext;
import org.autojs.autojs.*;
import org.autojs.autojs.R;
import com.stardust.theme.ThemeColorManager;

/**
 * Created by Stardust on 2017/3/12.
 */

public class ThemeColorManagerCompat {

    public static int getColorPrimary() {
        int color = ThemeColorManager.getColorPrimary();
        if (color == 0) {
            return GlobalAppContext.get().getResources().getColor(R.color.colorPrimary);
        } else {
            return color;
        }
    }
}
