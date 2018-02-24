package com.stardust.util;

import android.support.annotation.NonNull;

/**
 * Created by Stardust on 2017/5/3.
 */

public class TextUtils {

    public static String join(CharSequence delimiter, Object... tokens) {
        return android.text.TextUtils.join(delimiter, tokens);
    }


    @NonNull
    public static String toEmptyIfNull(String message) {
        if (message == null)
            return "";
        return message;
    }

    public static int lastIndexOf(CharSequence text, char ch, int fromIndex) {
        if (text instanceof String) {
            return ((String) text).lastIndexOf(ch, fromIndex);
        }
        int i = Math.min(fromIndex, text.length() - 1);
        for (; i >= 0; i--) {
            if (text.charAt(i) == ch) {
                return i;
            }
        }
        return -1;

    }

    public static int indexOf(CharSequence text, char ch, int fromIndex) {
        if (text instanceof String) {
            return ((String) text).indexOf(ch, fromIndex);
        }
        final int max = text.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        // handle most cases here (ch is a BMP code point or a
        // negative value (invalid code point))
        for (int i = fromIndex; i < max; i++) {
            if (text.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }
}