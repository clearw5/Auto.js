package com.stardust.autojs.engine;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.autojs.core.util.ProcessShell;
import com.stardust.autojs.core.inputevent.InputDevices;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.pio.PFiles;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/8/1.
 */

public class RootAutomatorEngine extends ScriptEngine.AbstractScriptEngine<AutoFileSource> {

    public static final int VERSION = 1;

    private static final String KEY_TOUCH_DEVICE = RootAutomatorEngine.class.getName() + ".touch_device";
    private static final String LOG_TAG = "RootAutomatorEngine";
    private static final Pattern PID_PATTERN = Pattern.compile("[0-9]{2,}");

    private static int sTouchDevice = -1;
    private static final String ROOT_AUTOMATOR_EXECUTABLE_ASSET = "binary/root_automator";

    private Context mContext;
    private final String mDeviceNameOrPath;
    private Thread mThread;
    private String mExecutablePath;
    private String mPid;
    private Process mProcess;

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
        final String[] commands = {
                "chmod 755 " + mExecutablePath,
                String.format("\"%s\" \"%s\" -d \"%s\" &", mExecutablePath, autoFile, mDeviceNameOrPath), // to run root_automator
                "echo $!",  // to print the root_automator pid
                "exit", // to exit su
                "exit"  // to exit shell
        };
        try {
            mProcess = Runtime.getRuntime().exec("su");
            executeCommands(mProcess, commands);
            mPid = readPid(mProcess);
            mProcess.waitFor();
        } catch (IOException e) {
            throw new ScriptException(e);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        } finally {
            mProcess.destroy();
            mProcess = null;
        }
    }

    private String readPid(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = PID_PATTERN.matcher(line);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private void executeCommands(Process process, String[] commands) throws IOException {
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        for (String command : commands) {
            if (command != null) {
                os.write(command.getBytes());
                os.writeBytes("\n");
            }
        }
        os.flush();
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
        PFiles.copyAsset(context, ROOT_AUTOMATOR_EXECUTABLE_ASSET, tmp.getAbsolutePath());
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
        if (mPid != null) {
            ProcessShell.exec("kill " + mPid, true);
        }
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
