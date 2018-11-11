package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nick on 8/10/15.
 * <p>
 * Taken from http://stackoverflow.com/questions/8343971/how-to-parse-a-dimension-string-and-convert-it-to-a-dimension-value
 */
public class Dimensions {

    private static final ValueMapper<Integer> UNITS = new ValueMapper<Integer>("unit")
            .map("px", TypedValue.COMPLEX_UNIT_PX)
            .map("dip", TypedValue.COMPLEX_UNIT_DIP)
            .map("dp", TypedValue.COMPLEX_UNIT_DIP)
            .map("sp", TypedValue.COMPLEX_UNIT_SP)
            .map("pt", TypedValue.COMPLEX_UNIT_PT)
            .map("in", TypedValue.COMPLEX_UNIT_IN)
            .map("mm", TypedValue.COMPLEX_UNIT_MM);


    private static final Pattern DIMENSION_PATTERN = Pattern.compile("([+-]?[0-9.]+)([a-zA-Z]*)");

    public static int parseToPixel(String dimension, View view, ViewGroup parent, boolean horizontal) {
        if (dimension.endsWith("%") && parent != null) {
            float pct = Float.parseFloat(dimension.substring(0, dimension.length() - 1)) / 100.0f;
            return (int) (pct * (horizontal ? parent.getMeasuredWidth() : parent.getMeasuredHeight()));
        }
        return parseToIntPixel(dimension, view.getContext());
    }

    public static float parseToPixel(String dimension, View view) {
        return parseToPixel(dimension, view.getContext());
    }

    public static float parseToPixel(View view, String dimension) {
        return parseToPixel(dimension, view.getContext());
    }

    public static float parseToPixel(String dimension, Context context) {
        if (dimension.startsWith("?")) {
            int[] attr = {context.getResources().getIdentifier(dimension.substring(1), "attr",
                    context.getPackageName())};
            TypedArray ta = context.obtainStyledAttributes(attr);
            float d = ta.getDimension(0, 0);
            ta.recycle();
            return d;
        }
        Matcher m = DIMENSION_PATTERN.matcher(dimension);
        if (!m.matches()) {
            throw new InflateException("dimension cannot be resolved: " + dimension);
        }
        int unit = m.groupCount() == 2 ? UNITS.get(m.group(2), TypedValue.COMPLEX_UNIT_DIP) : TypedValue.COMPLEX_UNIT_DIP;
        float value = Integer.valueOf(m.group(1));
        return TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

    public static int parseToIntPixel(String value, View view) {
        return Math.round(parseToPixel(value, view));
    }

    public static int parseToIntPixel(String value, Context context) {
        return Math.round(parseToPixel(value, context));
    }

    public static int[] parseToIntPixelArray(View view, String value) {
        String[] split = value.split(" ");
        int[] pixels = new int[4];
        for (int i = 0; i < split.length; i++) {
            pixels[i] = parseToIntPixel(split[i], view);
        }
        if (split.length == 1) {
            pixels[1] = pixels[2] = pixels[3] = pixels[0];
        } else if (split.length == 2) {
            pixels[2] = pixels[0];
            pixels[3] = pixels[1];
        }
        return pixels;
    }
}




