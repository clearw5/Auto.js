package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.autojs.R;
import com.stardust.autojs.core.floaty.FloatyWindow;
import com.stardust.autojs.core.ui.JsLayoutInflater;
import com.stardust.autojs.core.ui.JsViewHelper;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.util.FloatingPermission;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.util.UiHandler;
import com.stardust.util.ViewUtil;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/12/5.
 */

public class Floaty {

    private JsLayoutInflater mJsLayoutInflater;
    private Context mContext;
    private UiHandler mUiHandler;
    private CopyOnWriteArraySet<JsFloatyWindow> mWindows = new CopyOnWriteArraySet<>();
    private ScriptRuntime mRuntime;

    public Floaty(UiHandler uiHandler, UI ui, ScriptRuntime runtime) {
        mUiHandler = uiHandler;
        mRuntime = runtime;
        mContext = new ContextThemeWrapper(mUiHandler.getContext(), R.style.AppTheme);
        mJsLayoutInflater = ui.getJsLayoutInflater();
    }

    public JsFloatyWindow window(String xml) {
        return window(inflate(xml));
    }

    public JsFloatyWindow window(View view) {
        try {
            FloatingPermission.waitForPermissionGranted(view.getContext());
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
        JsFloatyWindow window = new JsFloatyWindow(view);
        addWindow(window);
        return window;
    }

    private synchronized void addWindow(JsFloatyWindow window) {
        mWindows.add(window);
    }

    private synchronized boolean removeWindow(JsFloatyWindow window) {
        return mWindows.remove(window);
    }

    private View inflate(String xml) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return mJsLayoutInflater.inflate(mContext, xml);
        } else {
            VolatileDispose<View> dispose = new VolatileDispose<>();
            mUiHandler.post(() -> dispose.setAndNotify(mJsLayoutInflater.inflate(mContext, xml)));
            return dispose.blockedGetOrThrow(ScriptInterruptedException.class);
        }
    }

    public synchronized void closeAll() {
        for (JsFloatyWindow window : mWindows) {
            window.close(false);
        }
        mWindows.clear();
    }

    public class JsFloatyWindow {

        private View mView;
        private volatile FloatyWindow mWindow;
        private boolean mExitOnClose = false;

        public JsFloatyWindow(View view) {
            mWindow = new FloatyWindow(view);
            mUiHandler.post(() -> {
                mUiHandler.getContext().startService(new Intent(mUiHandler.getContext(), FloatyService.class));
                FloatyService.addWindow(mWindow);
            });
            mWindow.waitFor();
            mWindow.setOnCloseButtonClickListener(v -> close());
            //setSize(mWindow.getWindowBridge().getScreenWidth() / 2, mWindow.getWindowBridge().getScreenHeight() / 2);
            mView = view;
        }

        public View getView(String id) {
            return JsViewHelper.findViewByStringId(mView, id);
        }

        public int getX() {
            return mWindow.getWindowBridge().getX();
        }

        public int getY() {
            return mWindow.getWindowBridge().getY();
        }

        public int getWidth() {
            return mWindow.getRootView().getWidth();
        }

        public int getHeight() {
            return mWindow.getRootView().getHeight();
        }

        public void setSize(int w, int h) {
            runWithWindow(() -> ViewUtil.setViewMeasure(mWindow.getRootView(), w, h));
        }

        private void runWithWindow(Runnable r) {
            if (mWindow == null)
                return;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                r.run();
                return;
            }
            mUiHandler.post(() -> {
                if (mWindow == null)
                    return;
                r.run();
            });
        }

        public void setPosition(int x, int y) {
            runWithWindow(() -> mWindow.getWindowBridge().updatePosition(x, y));
        }

        public void setAdjustEnabled(boolean enabled) {
            runWithWindow(() -> mWindow.setAdjustEnabled(enabled));
        }

        public boolean isAdjustEnabled() {
            return mWindow.isAdjustEnabled();
        }

        public void exitOnClose() {
            mExitOnClose = true;
        }

        public void close() {
            close(true);
        }

        void close(boolean removeFromWindows) {
            if (removeFromWindows && !removeWindow(this)) {
                return;
            }
            runWithWindow(() -> {
                mWindow.close();
                mWindow = null;
                if (mExitOnClose) {
                    mRuntime.exit();
                }
            });
        }
    }


}
