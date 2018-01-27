package com.stardust.scriptdroid.tool;

import com.stericson.RootShell.RootShell;

/**
 * Created by Stardust on 2018/1/26.
 */

public class RootTool {

    public static boolean isRootAvailable() {
        try {
            return RootShell.isRootAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
