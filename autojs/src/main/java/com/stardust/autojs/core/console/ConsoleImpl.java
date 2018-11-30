package com.stardust.autojs.core.console;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.WindowManager;

import com.stardust.autojs.R;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.AbstractConsole;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.util.FloatingPermission;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.util.UiHandler;
import com.stardust.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/5/2.
 */

public class ConsoleImpl extends AbstractConsole {

    public static class LogEntry implements Comparable<LogEntry> {

        public int id;
        public int level;
        public CharSequence content;
        public boolean newLine = false;

        public LogEntry(int id, int level, CharSequence content) {
            this.id = id;
            this.level = level;
            this.content = content;
        }

        public LogEntry(int id, int level, CharSequence content, boolean newLine) {
            this.id = id;
            this.level = level;
            this.content = content;
            this.newLine = newLine;
        }

        @Override
        public int compareTo(@NonNull LogEntry o) {
            return 0;
        }
    }

    public interface LogListener {
        void onNewLog(LogEntry logEntry);

        void onLogClear();
    }

    private final Object WINDOW_SHOW_LOCK = new Object();
    private final Console mGlobalConsole;
    private final ArrayList<LogEntry> mLogEntries = new ArrayList<>();
    private AtomicInteger mIdCounter = new AtomicInteger(0);
    private ResizableExpandableFloatyWindow mFloatyWindow;
    private ConsoleFloaty mConsoleFloaty;
    private WeakReference<LogListener> mLogListener;
    private UiHandler mUiHandler;
    private BlockingQueue<String> mInput = new ArrayBlockingQueue<>(1);
    private WeakReference<ConsoleView> mConsoleView;
    private volatile boolean mShown = false;
    private int mX, mY;

    public ConsoleImpl(UiHandler uiHandler) {
        this(uiHandler, null);
    }

    public ConsoleImpl(UiHandler uiHandler, Console globalConsole) {
        mUiHandler = uiHandler;
        mConsoleFloaty = new ConsoleFloaty(this);
        mGlobalConsole = globalConsole;
        mFloatyWindow = new ResizableExpandableFloatyWindow(mConsoleFloaty) {
            @Override
            public void onCreate(FloatyService service, WindowManager manager) {
                super.onCreate(service, manager);
                expand();
                mFloatyWindow.getWindowBridge().updatePosition(mX, mY);
                synchronized (WINDOW_SHOW_LOCK) {
                    mShown = true;
                    WINDOW_SHOW_LOCK.notifyAll();
                }
            }
        };
    }

    public void setConsoleView(ConsoleView consoleView) {
        mConsoleView = new WeakReference<>(consoleView);
        setLogListener(consoleView);
        synchronized (this) {
            this.notify();
        }
    }


    public void setLogListener(LogListener logListener) {
        mLogListener = new WeakReference<>(logListener);
    }

    public ArrayList<LogEntry> getAllLogs() {
        return mLogEntries;
    }

    public void printAllStackTrace(Throwable t) {
        println(android.util.Log.ERROR, ScriptRuntime.getStackTrace(t, true));
    }

    public String getStackTrace(Throwable t) {
        return ScriptRuntime.getStackTrace(t, false);
    }

    @Override
    public String println(int level, CharSequence charSequence) {
        LogEntry logEntry = new LogEntry(mIdCounter.getAndIncrement(), level, charSequence, true);
        synchronized (mLogEntries) {
            mLogEntries.add(logEntry);
        }
        if (mGlobalConsole != null) {
            mGlobalConsole.println(level, charSequence);
        }
        if (mLogListener != null && mLogListener.get() != null) {
            mLogListener.get().onNewLog(logEntry);
        }
        return null;
    }


    @Override
    public void write(int level, CharSequence charSequence) {
        println(level, charSequence);
    }


    @Override
    public void clear() {
        synchronized (mLogEntries) {
            mLogEntries.clear();
        }
        if (mLogListener != null && mLogListener.get() != null) {
            mLogListener.get().onLogClear();
        }
    }

    @Override
    public void show() {
        if (mShown) {
            return;
        }
        if (!FloatingPermission.canDrawOverlays(mUiHandler.getContext())) {
            FloatingPermission.manageDrawOverlays(mUiHandler.getContext());
            mUiHandler.toast(R.string.text_no_floating_window_permission);
            return;
        }
        startFloatyService();
        mUiHandler.post(() -> {
            try {
                FloatyService.addWindow(mFloatyWindow);
                // SecurityException: https://github.com/hyb1996-guest/AutoJsIssueReport/issues/4781
            } catch (WindowManager.BadTokenException | SecurityException e) {
                e.printStackTrace();
                mUiHandler.toast(R.string.text_no_floating_window_permission);
            }
        });
        synchronized (WINDOW_SHOW_LOCK) {
            if (mShown) {
                return;
            }
            try {
                WINDOW_SHOW_LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startFloatyService() {
        Context context = mUiHandler.getContext();
        context.startService(new Intent(context, FloatyService.class));
    }

    @Override
    public void hide() {
        mUiHandler.post(() -> {
            synchronized (WINDOW_SHOW_LOCK) {
                if (!mShown)
                    return;
                try {
                    mFloatyWindow.close();
                } catch (IllegalArgumentException ignored) {

                }
                mShown = false;
            }
        });
    }


    public void setSize(int w, int h) {
        if (mShown) {
            mUiHandler.post(() -> {
                if (mShown) {
                    ViewUtil.setViewMeasure(mConsoleFloaty.getExpandedView(), w, h);
                }
            });
        }
    }

    public void setPosition(int x, int y) {
        mX = x;
        mY = y;
        if (mShown) {
            mUiHandler.post(() -> {
                if (mShown)
                    mFloatyWindow.getWindowBridge().updatePosition(x, y);
            });
        }
    }

    @ScriptInterface
    public String rawInput() {
        if (mConsoleView == null || mConsoleView.get() == null) {
            if (!mShown) {
                show();
            }
            waitForConsoleView();
        }
        mConsoleView.get().showEditText();
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

    @Override
    public void error(@Nullable Object data, Object... options) {
        if (data instanceof Throwable) {
            data = getStackTrace((Throwable) data);
        }
        if (options != null && options.length > 0) {
            StringBuilder sb = new StringBuilder(data == null ? "" : data.toString());
            ArrayList<Object> newOptions = new ArrayList<>();
            for (Object option : options) {
                if (option instanceof Throwable) {
                    sb.append(getStackTrace((Throwable) option)).append(" ");
                } else {
                    newOptions.add(option);
                }
            }
            data = sb.toString();
            if (newOptions.isEmpty()) {
                super.error(data, newOptions.toArray());
            } else {
                super.error(data);
            }
        } else {
            super.error(data, options);
        }
    }
}
