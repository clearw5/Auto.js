package com.stardust.scriptdroid.external;

import android.os.Bundle;

/**
 * Created by Stardust on 2017/4/1.
 */

public class CommonUtils {

    public static final String EXTRA_KEY_PATH = "path";

    public static final String EXTRA_KEY_PRE_EXECUTE_SCRIPT = "script";

    public static boolean isTaskerBundleValid(Bundle bundle) {
        return bundle.containsKey(CommonUtils.EXTRA_KEY_PATH) || bundle.containsKey(EXTRA_KEY_PRE_EXECUTE_SCRIPT);
    }

}
