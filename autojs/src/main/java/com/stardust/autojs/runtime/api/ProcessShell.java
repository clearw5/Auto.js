package com.stardust.autojs.runtime.api;


import android.util.Log;

import com.stardust.pio.UncheckedIOException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Stardust on 2017/1/20.
 * <p>
 * 来自网络~~
 */

public class ProcessShell extends AbstractShell {

    private static final String TAG = "ProcessShell";

    private Process mProcess;
    private DataOutputStream mCommandOutputStream;
    private BufferedReader mSucceedReader;
    private BufferedReader mErrorReader;

    private StringBuilder mSucceedOutput = new StringBuilder();
    private StringBuilder mErrorOutput = new StringBuilder();

    public ProcessShell() {

    }

    public ProcessShell(boolean root) {
        super(root);
    }

    @Override
    protected void init(String initialCommand) {
        try {
            mProcess = new ProcessBuilder(initialCommand).redirectErrorStream(true).start();
            mCommandOutputStream = new DataOutputStream(mProcess.getOutputStream());
            mSucceedReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            mErrorReader = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void exec(String command) {
        try {
            mCommandOutputStream.writeBytes(command);
            if (!command.endsWith(COMMAND_LINE_END)) {
                mCommandOutputStream.writeBytes(COMMAND_LINE_END);
            }
            mCommandOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exit() {
        if (mProcess != null) {
            mProcess.destroy();
            mProcess = null;
        }
        if (mSucceedReader != null) {
            try {
                mSucceedReader.close();
            } catch (IOException ignored) {

            }
            mSucceedReader = null;
        }
        if (mErrorReader != null) {
            try {
                mErrorReader.close();
            } catch (IOException ignored) {

            }
            mErrorReader = null;
        }

    }

    @Override
    public void exitAndWaitFor() {
        exec(COMMAND_EXIT);
        waitFor();
        exit();
    }

    public int waitFor() {
        try {
            return mProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ProcessShell readAll() {
        return readSucceedOutput().readErrorOutput();
    }

    public ProcessShell readSucceedOutput() {
        read(mSucceedReader, mSucceedOutput);
        return this;
    }

    private void read(BufferedReader reader, StringBuilder sb) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ProcessShell readErrorOutput() {
        read(mErrorReader, mErrorOutput);
        return this;
    }

    public StringBuilder getSucceedOutput() {
        return mSucceedOutput;
    }

    public StringBuilder getErrorOutput() {
        return mErrorOutput;
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

    public static Result exec(String command, boolean isRoot) {
        String[] commands = command.split("\n");
        return exec(commands, isRoot);
    }

    public static Result exec(String[] commands, boolean isRoot) {
        ProcessShell shell = null;
        try {
            shell = new ProcessShell(isRoot);
            for (String command : commands) {
                shell.exec(command);
            }
            shell.exec(COMMAND_EXIT);
            Result result = new Result();
            result.code = shell.waitFor();
            shell.readAll();
            result.error = shell.getErrorOutput().toString();
            result.result = shell.getSucceedOutput().toString();
            shell.exit();
            return result;
        } finally {
            if (shell != null) {
                shell.exit();
            }
        }
    }

    public static Result execCommand(String[] commands, boolean isRoot) {
        Result commandResult = new Result();
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
                String errMsg = e.getMessage();
                if (errMsg != null) {
                    Log.e(TAG, errMsg);
                } else {
                    e.printStackTrace();
                }
            }
            if (process != null) process.destroy();
        }
        return commandResult;
    }

    public static Result execCommand(String command, boolean isRoot) {
        String[] commands = command.split("\n");
        return execCommand(commands, isRoot);
    }


}