package com.stardust.scriptdroid.ui.edit.completion;

import android.graphics.Color;

import com.stardust.util.MapEntries;

import java.util.Map;

/**
 * Created by Stardust on 2017/9/28.
 */

public class InputMethodEnhancedBarColors {

    private static final Map<String, Integer> sEditorThemeColors = new MapEntries<String, Integer>()
            .entry("3024-day", 0xf7f7f7)
            .entry("3024-night", 0x090300)
            .entry("abcdef", 0x0f0f0f)
            .entry("ambiance-mobile", 0xffffff)
            .entry("ambiance", 0xffffff)
            .entry("base16-dark", 0x151515)
            .entry("base16-light", 0xf5f5f5)
            .entry("bespin", 0x28211c)
            .entry("dracula", 0x282a36)
            .entry("duotone-dark", 0x2a2734)
            .entry("duotone-light", 0xfaf8f5)
            .entry("eclipse", 0xffffff)
            .entry("icecoder", 0x1d1d1b)
            .entry("material", 0x263238)
            .entry("mbo", 0x2c2c2c)
            .entry("mdn-like", 0xfefefe)
            .entry("monokai", 0x272822)
            .entry("neat", 0xffffff)
            .entry("neo", 0xffffff)
            .entry("night", 0x0a001f)
            .entry("panda-syntax", 0x292a2b)
            .entry("paraiso-dark", 0x2f1e2e)
            .entry("paraiso-light", 0xe7e9db)
            .entry("pastel-on-dark", 0x2c2827)
            .entry("railscasts", 0x2b2b2b)
            .entry("rubyblue", 0x112435)
            .entry("seti", 0x151718)
            .entry("solarized", 0xffffff)
            .entry("the-matrix", 0x000000)
            .entry("tomorrow-night-bright", 0x000000)
            .entry("tomorrow-night-eighties", 0x000000)
            .entry("ttcn", 0xffffff)
            .entry("twilight", 0x141414)
            .entry("xq-light", 0xffffff)
            .entry("zenburn", 0x3f3f3f)
            .map();

    public static int getBackgroundColor(String theme) {
        int themeColor = sEditorThemeColors.get(theme);
        if (themeColor == 0xffffff) {
            return 0x828389;
        }
        return themeColor | 0xdd000000;
    }

    public static int getTextColor(String theme) {
        int c = sEditorThemeColors.get(theme);
        if (isBrightColor(c)) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    public static boolean isBrightColor(int color) {
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        return brightness >= 200;
    }

}
