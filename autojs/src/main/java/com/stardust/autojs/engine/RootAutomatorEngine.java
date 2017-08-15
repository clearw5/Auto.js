package com.stardust.autojs.engine;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.autojs.core.inputevent.InputDevices;
import com.stardust.autojs.runtime.api.Shell;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.concurrent.VolatileBox;
import com.stardust.pio.PFile;

import java.io.File;

/**
 * Created by Stardust on 2017/8/1.
 */

public class RootAutomatorEngine extends ScriptEngine.AbstractScriptEngine<AutoFileSource> {

    public static final int VERSION = 1;

    private static final String KEY_TOUCH_DEVICE = RootAutomatorEngine.class.getName() + ".touch_device";
    private static final String LOG_TAG = "RootAutomatorEngine";

    private static int sTouchDevice = -1;
    private static final String ROOT_AUTOMATOR_EXECUTABLE_ASSET = "binary/root_automator";

    private Context mContext;
    private String mDeviceNameOrPath;
    private Thread mThread;
    private String mExecutablePath;

    public RootAutomatorEngine(Context context, String deviceNameOrPath) {
        mContext = context;
        mDeviceNameOrPath = getDeviceNameOrPath(context, deviceNameOrPath);
    }


    public RootAutomatorEngine(Context context) {
        this(context, InputDevices.getTouchDeviceName());
    }

    public void execute(String autoFile) {
        mExecutablePath = getExecutablePath(mContext);
        Log.d(LOG_TAG, "exec: " + autoFile);
        AbstractShell.Result result = ProcessShell.execCommand(new String[]{
                "chmod 777 " + mExecutablePath,
                mExecutablePath + " \"" + autoFile + "\" -d " + mDeviceNameOrPath
        }, true);
        Log.d(LOG_TAG, "result = " + result);
    }


    public static String getDeviceNameOrPath(Context context, String deviceNameOrPath) {
        if (sTouchDevice < 0) {
            sTouchDevice = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TOUCH_DEVICE, -1);
        }
        if (sTouchDevice >= 0) {
            deviceNameOrPath = "/dev/input/event" + sTouchDevice;
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt(KEY_TOUCH_DEVICE, sTouchDevice)
                    .apply();
        }
        return deviceNameOrPath;
    }

    public static String getExecutablePath(Context context) {
        File tmp = new File(context.getCacheDir(), "root_automator");
        PFile.copyAsset(context, ROOT_AUTOMATOR_EXECUTABLE_ASSET, tmp.getAbsolutePath());
        return tmp.getAbsolutePath();
    }

    public static void setTouchDevice(int device) {
        sTouchDevice = device;
    }

    public static int getTouchDevice(Context context) {
        if (sTouchDevice >= 0)
            return sTouchDevice;
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TOUCH_DEVICE, -1);
    }

    @Override
    public void put(String name, Object value) {

    }

    @Override
    public Object execute(AutoFileSource source) {
        execute(source.getFile().getAbsolutePath());
        return null;
    }

    @Override
    public void forceStop() {
        mThread.interrupt();
        ProcessShell.exec("killall " + mExecutablePath, true);
    }

    @Override
    public void init() {
        mThread = Thread.currentThread();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
    }
}
