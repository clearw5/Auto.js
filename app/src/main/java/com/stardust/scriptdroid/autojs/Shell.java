package com.stardust.scriptdroid.autojs;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.App;

import java.io.IOException;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/4/24.
 */

public class Shell extends AbstractShell implements AutoCloseable {

    public interface OutputListener {
        void onNewOutput(String str);
    }

    private static final String TAG = "Shell";

    private TermSession mTermSession;
    private RuntimeException mInitException;
    private final Object mInitLock = new Object();
    private final Object mExitLock = new Object();
    private boolean mInitialized = false;
    private boolean mWaitingExit = false;
    private OutputListener mOutputListener;

    public Shell() {
        super();
    }

    public Shell(boolean root) {
        super(root);
    }

    @Override
    protected void init(String initialCommand) {
        init(initialCommand, App.getApp(), AutoJs.getInstance().getUiHandler());
    }

    private void init(final String initialCommand, final Context context, Handler uiHandler) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                TermSettings settings = new TermSettings(context.getResources(), PreferenceManager.getDefaultSharedPreferences(context));
                try {
                    mTermSession = new MyShellTermSession(settings, initialCommand);
                    mTermSession.initializeEmulator(40, 40);
                } catch (IOException e) {
                    mInitException = new UncheckedIOException(e);
                }
            }
        });
    }

    public void exec(String command) {
        ensureInitialized();
        mTermSession.write(command + "\n");
    }

    public void setOutputListener(OutputListener outputListener) {
        mOutputListener = outputListener;
    }

    private void ensureInitialized() {
        if (mTermSession == null) {
            checkInitException();
            waitInitialization();
            if (mTermSession == null) {
                checkInitException();
                throw new IllegalStateException();
            }
        }
    }

    private void checkInitException() {
        if (mInitException != null) {
            throw mInitException;
        }
    }

    private void waitInitialization() {
        if (mInitialized)
            throw new IllegalStateException("already initialized");
        synchronized (mInitLock) {
            try {
                mInitLock.wait();
            } catch (InterruptedException e) {
                exit();
                throw new ScriptInterruptedException();
            }
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
                exit();
                throw new ScriptInterruptedException();
            }
        }
    }

    @Override
    public void close() {
        exit();
    }

    private class MyShellTermSession extends ShellTermSession {

        public MyShellTermSession(TermSettings settings, String initialCommand) throws IOException {
            super(settings, initialCommand);
        }

        @Override
        protected void processInput(byte[] data, int offset, int count) {
            String output = new String(data, offset, count);
            Log.d(TAG, output);
            if(mOutputListener != null){
                mOutputListener.onNewOutput(output);
            }
            if (mInitialized && !mWaitingExit) {
                return;
            }
            String[] lines = new String(data, offset, count).split("\n");
            for (String line : lines) {
                if (!mInitialized && line.endsWith(" # ")) {
                    notifyInitialized();
                    return;
                }
                if (mWaitingExit && line.endsWith(" $ ")) {
                    notifyExit();
                    return;
                }
            }
        }

        private void notifyExit() {
            synchronized (mExitLock) {
                mWaitingExit = false;
                mExitLock.notify();
            }
        }

        private void notifyInitialized() {
            mInitialized = true;
            synchronized (mInitLock) {
                mInitLock.notifyAll();
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
