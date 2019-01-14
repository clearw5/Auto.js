package com.stardust.autojs.core.image;

import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;

/**
 * Created by Stardust on 2017/12/31.
 */

public class Colors {

    public static final int BLACK = 0xFF000000;
    public static final int DKGRAY = 0xFF444444;
    public static final int GRAY = 0xFF888888;
    public static final int LTGRAY = 0xFFCCCCCC;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int RED = 0xFFFF0000;
    public static final int GREEN = 0xFF00FF00;
    public static final int BLUE = 0xFF0000FF;
    public static final int YELLOW = 0xFFFFFF00;
    public static final int CYAN = 0xFF00FFFF;
    public static final int MAGENTA = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;

    public int rgb(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }

    public int argb(int alpha, int red, int green, int blue) {
        return Color.argb(alpha, red, green, blue);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public float luminance(int color) {
        double red = Color.red(color) / 255.0;
        red = red < 0.03928 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
        double green = Color.green(color) / 255.0;
        green = green < 0.03928 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
        double blue = Color.blue(color) / 255.0;
        blue = blue < 0.03928 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);
        return (float) ((0.2126 * red) + (0.7152 * green) + (0.0722 * blue));
    }

    public int parseColor(String colorString) {
        return Color.parseColor(colorString);
    }

    public void RGBToHSV(int red, int green, int blue, float[] hsv) {
        Color.RGBToHSV(red, green, blue, hsv);
    }

    public void colorToHSV(int color, float[] hsv) {
        Color.colorToHSV(color, hsv);
    }

    public int HSVToColor(float[] hsv) {
        return Color.HSVToColor(hsv);
    }

    public int HSVToColor(int alpha, float[] hsv) {
        return Color.HSVToColor(alpha, hsv);
    }

    public String toString(int color) {
        StringBuilder c = new StringBuilder(Integer.toHexString(color));
        while (c.length() < 6) {
            c.insert(0, "0");
        }
        return "#" + c;
    }

    public boolean equals(int c1, int c2) {
        return (c1 & 0xffffff) == (c2 & 0xffffff);
    }

    public boolean equals(int c1, String c2) {
        return equals(c1, parseColor(c2));
    }

    public boolean equals(String c1, int c2) {
        return equals(parseColor(c1), c2);
    }

    public boolean equals(String c1, String c2) {
        return equals(parseColor(c1), parseColor(c2));
    }


}
