package com.stardust.autojs.core.image;

import android.support.annotation.Nullable;
import android.util.Log;

import org.opencv.core.Mat;

/**
 * Created by Stardust on 2018/4/2.
 */

public class OpenCVHelper {


    private static final String LOG_TAG = "OpenCv";

    public static void release(@Nullable Mat mat) {
        if (mat == null)
            return;
        mat.release();
    }

}
