package org.autojs.autojs.tool;

import com.stardust.autojs.core.util.ProcessShell;
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

    private static final String cmd = "enabled=$(settings get system pointer_location)\n" +
            "if [[ $enabled == 1 ]]\n" +
            "then\n" +
            "settings put system pointer_location 0\n" +
            "else\n" +
            "settings put system pointer_location 1\n" +
            "fi\n";

    public static void togglePointerLocation() {
        try {
            ProcessShell.execCommand(cmd, true);
        } catch (Exception ignored) {
        }
    }

    public static void setPointerLocationEnabled(boolean enabled) {
        try {
            ProcessShell.execCommand("settings put system pointer_location " + (enabled ? 1 : 0), true);
        } catch (Exception ignored) {

        }
    }
}
