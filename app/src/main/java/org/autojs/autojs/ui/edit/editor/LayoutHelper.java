package org.autojs.autojs.ui.edit.editor;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcelable;
import android.text.Layout;
import android.text.TextUtils;

/**
 * Created by Stardust on 2018/2/13.
 */

public class LayoutHelper {


    private static final Rect sTempRect = new Rect();

    public static long getLineRangeForDraw(Layout layout, Canvas canvas) {
        int dtop, dbottom;
        synchronized (sTempRect) {
            if (!canvas.getClipBounds(sTempRect)) {
                // Negative range end used as a special flag
                return packRangeInLong(0, -1);
            }

            dtop = sTempRect.top;
            dbottom = sTempRect.bottom;
        }

        final int top = Math.max(dtop, 0);
        final int bottom = Math.min(layout.getLineTop(layout.getLineCount()), dbottom);

        if (top >= bottom) return packRangeInLong(0, -1);
        return packRangeInLong(layout.getLineForVertical(top), layout.getLineForVertical(bottom));
    }

    /**
     * Pack 2 int values into a long, useful as a return value for a range
     */
    public static long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | end;
    }

    /**
     * Get the start value from a range packed in a long by {@link #packRangeInLong(int, int)}
     */
    public static int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }

    public static int unpackRangeEndFromLong(long range) {
        return (int) (range & 0x00000000FFFFFFFFL);
    }


    public static int getLineOfChar(Layout layout, int charIndex) {
        int low = 0;
        int high = layout.getLineCount() - 1;
        while (low < high) {
            int mid = (low + high) >>> 1;
            int midVal = layout.getLineEnd(mid);

            if (charIndex > midVal) {
                low = mid + 1;
            } else if (charIndex < midVal) {
                high = mid;
            } else {
                return Math.min(layout.getLineCount() - 1, mid + 1);
            }
        }
        return low;
    }

    public static int getVisibleLineAt(Layout layout, float x, float y) {
        if(layout == null) {
            return -1;
        }
        return 0;
    }
}
