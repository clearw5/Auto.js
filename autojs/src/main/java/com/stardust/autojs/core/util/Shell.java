package com.stardust.autojs.core.util;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.pio.UncheckedIOException;

import java.io.IOException;
import java.util.ArrayList;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/4/24.
 */

public class Shell extends AbstractShell {

    public interface Callback {

        void onOutput(String str);

        void onNewLine(String line);

        void onInitialized();

        void onInterrupted(InterruptedException e);
    }

    public static class SimpleCallback implements Callback {

        @Override
        public void onOutput(String str) {

        }

        @Override
        public void onNewLine(String str) {

        }

        @Override
        public void onInitialized() {

        }

        @Override
        public void onInterrupted(InterruptedException e) {

        }
    }

    private static final boolean DEBUG = true;
    private static final String TAG = "Shell";

    private volatile TermSession mTermSession;
    private final Object mInitLock = new Object();
    private final Object mExitLock = new Object();
    private final Object mCommandOutputLock = new Object();
    private volatile RuntimeException mInitException;
    private volatile boolean mInitialized = false;
    private volatile boolean mWaitingExit = false;
    private volatile String mCommandOutput = null;
    private final boolean mShouldReadOutput;
    private Callback mCallback;

    public Shell(Context context) {
        this(context, false);
    }

    public Shell(Context context, boolean root) {
        this(context, root, true);
    }

    public Shell(Context context, boolean root, boolean shouldReadOutput) {
        super(context, root);
        mShouldReadOutput = shouldReadOutput;
    }

    public Shell() {
        this(false);
    }

    public Shell(boolean root) {
        this(ScriptRuntime.getApplicationContext(), root);
    }

    @Override
    protected void init(final String initialCommand) {
        Handler uiHandler = new Handler(mContext.getMainLooper());
        uiHandler.post(() -> {
            TermSettings settings = new TermSettings(mContext.getResources(), PreferenceManager.getDefaultSharedPreferences(mContext));
            try {
                mTermSession = new MyShellTermSession(settings, initialCommand);
                mTermSession.initializeEmulator(1024, 40);
            } catch (IOException e) {
                mInitException = new UncheckedIOException(e);
            }
        });
    }

    public void exec(String command) {
        ensureInitialized();
        mTermSession.write(command + "\n");
    }

    public String execAndWaitFor(String command) {
        exec(command);
        synchronized (mCommandOutputLock) {
            try {
                mCommandOutputLock.wait();
                return mCommandOutput;
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    private void ensureInitialized() {
        if (mTermSession == null) {
            logDebug("ensureInitialized: not init");
            checkInitException();
            waitInitialization();
            if (mTermSession == null) {
                checkInitException();
                throw new IllegalStateException();
            }
        } else {
            logDebug("ensureInitialized: init");
        }
    }

    private void logDebug(String log) {
        if (DEBUG) {
            Log.d(TAG, log);
        }
    }

    private void checkInitException() {
        if (mInitException != null) {
            throw mInitException;
        }
    }

    private void waitInitialization() {
        synchronized (mInitLock) {
            if (mInitialized) {
                return;
            }
            logDebug("waitInitialization: enter");
            try {
                mInitLock.wait();
                logDebug("waitInitialization: exit");
            } catch (InterruptedException e) {
                onInterrupted(e);
            }
        }
    }

    private void onInterrupted(InterruptedException e) {
        if (mCallback == null) {
            exit();
            throw new ScriptInterruptedException();
        } else {
            mCallback.onInterrupted(e);
        }
    }

    @Override
    public void exit() {
        mTermSession.finish();
    }

    @Override
    public void exitAndWaitFor() {
        execExitAndWait();
        if (!isRoot()) {
            return;
        }
        execExitAndWait();
    }

    private void execExitAndWait() {
        synchronized (mExitLock) {
            mWaitingExit = true;
            exec("exit");
            try {
                mExitLock.wait();
            } catch (InterruptedException e) {
                onInterrupted(e);
            }
        }
    }

    public TermSession getTermSession() {
        return mTermSession;
    }

    private class MyShellTermSession extends ShellTermSession {

        private StringBuilder mStringBuffer = new StringBuilder();
        private ArrayList<String> mCommandOutputs = new ArrayList<>();

        public MyShellTermSession(TermSettings settings, String initialCommand) throws IOException {
            super(settings, initialCommand);
        }

        private void onNewLine(String line) {
            logDebug("onNewLine: " + line);
            if (!mInitialized) {
                if (!isRoot() && line.endsWith(" $ sh")) {
                    notifyInitialized();
                }
            } else {
                mCommandOutputs.add(line);
            }
            if (mCallback != null) {
                mCallback.onNewLine(line);
            }
            if (mWaitingExit && line.endsWith(" exit")) {
                notifyExit();
            }
        }

        private void onCommandOutput(ArrayList<String> output) {
            StringBuilder result = new StringBuilder();
            for (int i = 1; i < output.size(); i++) {
                result.append(output.get(i));
                if (i < output.size() - 1) {
                    result.append("\n");
                }
            }
            logDebug("onCommandOutput: lines = " + output + ", output = " + result);
            synchronized (mCommandOutputLock) {
                mCommandOutput = result.toString();
                mCommandOutputLock.notifyAll();
            }
        }

        private void onOutput(String str) {
            logDebug("onOutput: " + str);
            if (!mInitialized) {
                if (isRoot() && str.endsWith(":/ # ")) {
                    notifyInitialized();
                }
            }
            int start = 0;
            int i;
            while (true) {
                i = str.indexOf("\n", start);
                if (i > 0) {
                    onNewLine((mStringBuffer.toString() + str.substring(0, i - 1)).trim());
                    mStringBuffer.delete(0, mStringBuffer.length());
                } else {
                    if (start <= str.length() - 1) {
                        mStringBuffer.append(str.substring(start));
                    }
                    break;
                }
                start = i + 1;
            }
            if (str.endsWith(" # ") || str.endsWith(" $ ")) {
                onCommandOutput(mCommandOutputs);
                mCommandOutputs.clear();
            }
            if (mCallback != null) {
                mCallback.onOutput(str.replace("\r", ""));
            }
        }


        @Override
        protected void processInput(byte[] data, int offset, int count) {
            onOutput(new String(data, offset, count));
        }

        private void notifyExit() {
            synchronized (mExitLock) {
                mWaitingExit = false;
                mExitLock.notify();
            }
        }

        private void notifyInitialized() {
            logDebug("notifyInitialized");
            mInitialized = true;
            synchronized (mInitLock) {
                mInitLock.notifyAll();
            }
            if (mCallback != null) {
                mCallback.onInitialized();
            }
        }

        @Override
        protected void onProcessExit() {
            super.onProcessExit();
            synchronized (mExitLock) {
                mWaitingExit = false;
                mExitLock.notify();
            }
        }

    }

}
