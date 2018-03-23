package com.stardust.auojs.inrt.launch;

import android.app.Activity;
import android.content.Context;

import com.stardust.app.GlobalAppContext;
import com.stardust.auojs.inrt.App;

/**
 * Created by Stardust on 2018/3/21.
 */

public class GlobalProjectLauncher extends AssetsProjectLauncher {

    private static GlobalProjectLauncher sInstance;

    public static GlobalProjectLauncher getInstance() {
        if (sInstance == null)
            sInstance = new GlobalProjectLauncher(GlobalAppContext.get());
        return sInstance;
    }

    GlobalProjectLauncher(Context context) {
        super("project", context);
    }
}
