package com.stardust.autojs.runtime.console;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.runtime.api.AbstractConsole;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.util.UiHandler;
import com.stardust.autojs.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2017/5/2.
 */

public class StardustConsole extends AbstractConsole {

    public static class Log {

        public int level;
        public CharSequence content;

        public Log(int level, CharSequence content) {
            this.level = level;
            this.content = content;
        }
    }

    public interface LogListener {
        void onNewLog(Log log);

        void onLogClear();
    }

    private final Console mGlobalConsole;
    private List<Log> mLogs = new ArrayList<>();
    private ResizableExpandableFloatyWindow mFloatyWindow;
    private ConsoleFloaty mConsoleFloaty;
    private LogListener mLogListener;
    private UiHandler mUiHandler;
    private BlockingQueue<String> mInput = new ArrayBlockingQueue<>(1);
    private ConsoleView mConsoleView;
    private volatile boolean mShown = false;

    public StardustConsole(UiHandler uiHandler) {
        this(uiHandler, null);
    }

    public StardustConsole(UiHandler uiHandler, Console globalConsole) {
        mUiHandler = uiHandler;
        mConsoleFloaty = new ConsoleFloaty(this);
        mGlobalConsole = globalConsole;
        mFloatyWindow = new ResizableExpandableFloatyWindow(mConsoleFloaty) {
            @Override
            public void onCreate(FloatyService service, WindowManager manager) {
                super.onCreate(service, manager);
                expand();
            }
        };
    }

    public void setConsoleView(ConsoleView consoleView) {
        mConsoleView = consoleView;
        setLogListener(consoleView);
        synchronized (this) {
            this.notify();
        }
    }

    public void setLogListener(LogListener logListener) {
        mLogListener = logListener;
    }

    public List<Log> getLogs() {
        return mLogs;
    }

    @Override
    public void println(int level, CharSequence charSequence) {
        Log log = new Log(level, charSequence + "\n");
        mLogs.add(log);
        if (mGlobalConsole != null) {
            mGlobalConsole.println(level, charSequence);
        }
        if (mLogListener != null) {
            mLogListener.onNewLog(log);
        }
    }


    @Override
    public void write(int level, CharSequence charSequence) {
        Log log = new Log(level, charSequence);
        mLogs.add(log);
        if (mGlobalConsole != null) {
            mGlobalConsole.print(level, charSequence);
        }
        if (mLogListener != null) {
            mLogListener.onNewLog(log);
        }
    }


    @Override
    public void clear() {
        mLogs.clear();
        if (mLogListener != null) {
            mLogListener.onLogClear();
        }
    }

    @Override
    public void show() {
        if (!SettingsCompat.canDrawOverlays(mUiHandler.getContext())) {
            SettingsCompat.manageDrawOverlays(mUiHandler.getContext());
            mUiHandler.toast(R.string.text_no_floating_window_permission);
            return;
        }
        startFloatyService();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    FloatyService.addWindow(mFloatyWindow);
                    // SecurityException: https://github.com/hyb1996-guest/AutoJsIssueReport/issues/4781
                } catch (WindowManager.BadTokenException | SecurityException e) {
                    e.printStackTrace();
                    mUiHandler.toast(R.string.text_no_floating_window_permission);
                }
            }
        });
        mShown = true;
    }

    private void startFloatyService() {
        Context context = mUiHandler.getContext();
        context.startService(new Intent(context, FloatyService.class));
    }

    @Override
    public void hide() {
        try {
            mFloatyWindow.close();
        } catch (IllegalArgumentException ignored) {

        }
        mShown = false;
    }

    @ScriptInterface
    public String rawInput() {
        if (mConsoleView == null) {
            if (!mShown) {
                show();
            }
            waitForConsoleView();
        }
        mConsoleView.showEditText();
        try {
            return mInput.take();
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
    }

    private void waitForConsoleView() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
    }

    @ScriptInterface
    public String rawInput(Object data, Object... param) {
        log(data, param);
        return rawInput();
    }

    boolean submitInput(@NonNull CharSequence input) {
        return mInput.offer(input.toString());
    }

    @Override
    public void setTitle(CharSequence title) {
        mConsoleFloaty.setTitle(title);
    }
}
