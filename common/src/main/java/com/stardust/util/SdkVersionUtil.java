package com.stardust.util;

/**
 * Created by Stardust on 2017/4/3.
 */

public class SdkVersionUtil {

    private static final String[] SDK_VERSIONS = {
            "1.0", "1.1", "1.5", "1.6", "2.0", "2.0.1", "2.1.x", "2.2.x",
            "2.3", "2.3.3", "3.0.x", "3.1.x", "3.2", "4.0", "4.0.3", "4.1",
            "4.2", "4.3", "4.4.2", "4.4W", "5.0", "5.1", "6.0", "7.0", "7.1",
            "8.0"
    };

    public static String sdkIntToString(int i) {
        if (i > 26) {
            return "Unknown";
        }
        return SDK_VERSIONS[i - 1];
    }


}
