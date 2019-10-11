package com.stardust.util;

import androidx.annotation.NonNull;

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

    public static int indexOf(CharSequence source,
                              CharSequence target,
                              int fromIndex) {
        if (source instanceof String && target instanceof String) {
            return ((String) source).indexOf((String) target);
        }
        if (fromIndex >= source.length()) {
            return (target.length() == 0 ? source.length() : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (target.length() == 0) {
            return fromIndex;
        }

        char first = target.charAt(0);
        int max = (source.length() - target.length());

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + target.length() - 1;
                for (int k = 1; j < end && source.charAt(j)
                        == target.charAt(k); j++, k++)
                    ;

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence source,
                                  CharSequence target,
                                  int fromIndex) {
        if (source instanceof String && target instanceof String) {
            return ((String) source).lastIndexOf((String) target);
        }
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = source.length() - target.length();
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (target.length() == 0) {
            return fromIndex;
        }

        int strLastIndex = target.length() - 1;
        char strLastChar = target.charAt(strLastIndex);
        int min = target.length() - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source.charAt(i) != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (target.length() - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source.charAt(j--) != target.charAt(k--)) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start + 1;
        }
    }
}