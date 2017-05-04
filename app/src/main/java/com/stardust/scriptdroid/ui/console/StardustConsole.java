package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.content.Intent;

import com.stardust.autojs.runtime.api.AbstractConsole;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.util.UiHandler;

import java.util.ArrayList;
import java.util.List;

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

    private final Console GLOBAL_CONSOLE = AutoJs.getInstance().getScriptEngineService().getGlobalConsole();
    private List<Log> mLogs = new ArrayList<>();
    private ResizableExpandableFloatyWindow mFloatyWindow;
    private ConsoleFloaty mConsoleFloaty;
    private LogListener mLogListener;
    private UiHandler mUiHandler;

    public StardustConsole(UiHandler uiHandler) {
        mUiHandler = uiHandler;
        mConsoleFloaty = new ConsoleFloaty(this);
        mFloatyWindow = new ResizableExpandableFloatyWindow(mConsoleFloaty);
    }

    public void setLogListener(LogListener logListener) {
        mLogListener = logListener;
    }

    public List<Log> getLogs() {
        return mLogs;
    }

    @Override
    public void println(int level, CharSequence charSequence) {
        Log log = new Log(level, charSequence);
        mLogs.add(log);
        GLOBAL_CONSOLE.println(level, charSequence);
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
        startFloatyService();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                FloatyService.addWindow(mFloatyWindow);
            }
        });
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
    }

    @Override
    public void setTitle(CharSequence title) {
        mConsoleFloaty.setTitle(title);
    }
}
