package com.stardust.autojs.runtime.api;


import com.stardust.pio.UncheckedIOException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Stardust on 2017/1/20.
 * <p>
 * 来自网络~~
 */

public class ProcessShell extends AbstractShell implements AutoCloseable {

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
    public void close() {
        exit();
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
        try {
            while (mSucceedReader.ready()) {
                String line = mSucceedReader.readLine();
                mSucceedOutput.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public ProcessShell readErrorOutput() {
        String line;
        try {
            while ((line = mErrorReader.readLine()) != null) {
                mErrorOutput.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        try (ProcessShell shell = new ProcessShell(isRoot)) {
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
        }
    }


}