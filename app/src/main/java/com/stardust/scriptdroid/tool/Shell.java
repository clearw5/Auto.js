package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.scriptdroid.App;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/1/20.
 * <p>
 * 来自网络~~
 */

public class Shell {


    private static final String TAG = "Shell";

    private final static String COMMAND_SU = "su";
    private final static String COMMAND_SH = "sh";
    private final static String COMMAND_EXIT = "exit\n";
    private final static String COMMAND_LINE_END = "\n";

    private Process mProcess;
    private DataOutputStream mCommandOutputStream;
    private BufferedReader mSucceedReader;
    private BufferedReader mErrorReader;

    private StringBuilder mSucceedOutput = new StringBuilder();
    private StringBuilder mErrorOutput = new StringBuilder();

    public static String bytesToString(byte[] data, int base, int length) {
        StringBuilder buf = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            byte b = data[base + i];
            if(b >= 32 && b <= 126) {
                buf.append((char)b);
            } else {
                buf.append(String.format("\\x%02x", new Object[]{Byte.valueOf(b)}));
            }
        }

        return buf.toString();
    }


    public static void test(final Activity activity) {
        TermSettings settings = new TermSettings(App.getApp().getResources(), PreferenceManager.getDefaultSharedPreferences(App.getApp()));
        try {
            final ShellTermSession termSession = new ShellTermSession(settings, "");
            termSession.initializeEmulator(80, 40);
            termSession.write("su\r");
            termSession.write("getevent\r");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(termSession.getTermIn()));
                    try {
                        while (reader.ready()){
                            String line = reader.readLine();
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Shell() {
        this(false);
    }

    public Shell(boolean root) {
        try {
            mProcess = new ProcessBuilder(root ? COMMAND_SU : COMMAND_SH).redirectErrorStream(true).start();
            mCommandOutputStream = new DataOutputStream(mProcess.getOutputStream());
            mSucceedReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            mErrorReader = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Shell execute(String command) {

        try {
            mCommandOutputStream.writeBytes(command);
            if (!command.endsWith(COMMAND_LINE_END)) {
                mCommandOutputStream.writeBytes(COMMAND_LINE_END);
            }
            mCommandOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Shell exitAndWaitFor() {
        exit();
        try {
            mProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Shell readAll() {
        return readSucceedOutput().readErrorOutput();
    }

    public Shell readSucceedOutput() {
        try {
            while (mSucceedReader.ready()) {
                String line = mSucceedReader.readLine();
                mSucceedOutput.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Shell readErrorOutput() {
        String line;
        try {
            while ((line = mErrorReader.readLine()) != null) {
                mErrorOutput.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StringBuilder getSucceedOutput() {
        return mSucceedOutput;
    }

    public StringBuilder getErrorOutput() {
        return mErrorOutput;
    }

    public Shell exit() {
        execute(COMMAND_EXIT);
        return this;
    }

    public Shell destroy() {
        mProcess.destroy();
        return this;
    }

    public Process getProcess() {
        return mProcess;
    }

    public BufferedReader getSucceedReader() {
        return mSucceedReader;
    }

    public BufferedReader getErrorReader() {
        return mErrorReader;
    }

    public int waitFor() {
        try {
            return mProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Command执行结果
     *
     * @author Mountain
     */
    public static class CommandResult {
        public int code = -1;
        public String error;
        public String result;

        @Override
        public String toString() {
            return "ShellResult{" +
                    "code=" + code +
                    ", error='" + error + '\'' +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

    /**
     * 执行命令—单条
     *
     * @param command
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = command.split("\n");
        return execCommand(commands, isRoot);
    }

    /**
     * 执行命令-多条
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) return commandResult;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.code = process.waitFor();
            //获取错误信息
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) successMsg.append(s);
            while ((s = errorResult.readLine()) != null) errorMsg.append(s);
            commandResult.result = successMsg.toString();
            commandResult.error = errorMsg.toString();
            Log.i(TAG, commandResult.toString());
        } catch (Exception e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (os != null) os.close();
                if (successResult != null) successResult.close();
                if (errorResult != null) errorResult.close();
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                } else {
                    e.printStackTrace();
                }
            }
            if (process != null) process.destroy();
        }
        return commandResult;
    }

    public static Process exec(String[] commands, boolean isRoot) {
        try {
            Process process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            os.close();
            return process;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process exec(String command, boolean isRoot) {
        return exec(command.split("\n"), isRoot);
    }
}