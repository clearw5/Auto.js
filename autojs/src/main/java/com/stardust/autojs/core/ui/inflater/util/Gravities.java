package com.stardust.autojs.core.ui.inflater.util;

import android.view.Gravity;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Gravities {

    public static int parse(String g) {
        int gravity = Gravity.NO_GRAVITY;
        String[] parts = g.toLowerCase().split("[|]");
        for (String part : parts) {
            switch (part) {
                case "center":
                    gravity = gravity | Gravity.CENTER;
                    break;
                case "left":
                case "textStart":
                    gravity = gravity | Gravity.LEFT;
                    break;
                case "right":
                case "textEnd":
                    gravity = gravity | Gravity.RIGHT;
                    break;
                case "top":
                    gravity = gravity | Gravity.TOP;
                    break;
                case "bottom":
                    gravity = gravity | Gravity.BOTTOM;
                    break;
                case "center_horizontal":
                    gravity = gravity | Gravity.CENTER_HORIZONTAL;
                    break;
                case "center_vertical":
                    gravity = gravity | Gravity.CENTER_VERTICAL;
                    break;
            }
        }
        return gravity;
    }



}
