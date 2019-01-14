package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.autojs.R;
import com.stardust.autojs.core.floaty.BaseResizableFloatyWindow;
import com.stardust.autojs.core.floaty.RawWindow;
import com.stardust.autojs.core.ui.JsViewHelper;
import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.Exceptions;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.util.FloatingPermission;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.util.UiHandler;
import com.stardust.util.ViewUtil;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2017/12/5.
 */

public class Floaty {

    private DynamicLayoutInflater mLayoutInflater;
    private Context mContext;
    private UiHandler mUiHandler;
    private CopyOnWriteArraySet<JsWindow> mWindows = new CopyOnWriteArraySet<>();
    private ScriptRuntime mRuntime;

    public Floaty(UiHandler uiHandler, UI ui, ScriptRuntime runtime) {
        mUiHandler = uiHandler;
        mRuntime = runtime;
        mContext = new ContextThemeWrapper(mUiHandler.getContext(), R.style.ScriptTheme);
        mLayoutInflater = ui.getLayoutInflater();
    }

    public JsResizableWindow window(BaseResizableFloatyWindow.ViewSupplier supplier) {
        try {
            FloatingPermission.waitForPermissionGranted(mContext);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
        JsResizableWindow window = new JsResizableWindow(supplier);
        addWindow(window);
        return window;
    }

    public JsResizableWindow window(View view) {
        try {
            FloatingPermission.waitForPermissionGranted(view.getContext());
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }

        JsResizableWindow window = new JsResizableWindow((context, parent) -> view);
        addWindow(window);
        return window;
    }

    public JsRawWindow rawWindow(RawWindow.RawFloaty floaty) {
        try {
            FloatingPermission.waitForPermissionGranted(mContext);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
        JsRawWindow window = new JsRawWindow(floaty);
        addWindow(window);
        return window;
    }

    public JsRawWindow rawWindow(View view) {
        try {
            FloatingPermission.waitForPermissionGranted(mContext);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
        JsRawWindow window = new JsRawWindow((context, parent) -> view);
        addWindow(window);
        return window;
    }

    private synchronized void addWindow(JsWindow window) {
        mWindows.add(window);
    }

    private synchronized boolean removeWindow(JsWindow window) {
        return mWindows.remove(window);
    }

    public synchronized void closeAll() {
        for (JsWindow window : mWindows) {
            window.close(false);
        }
        mWindows.clear();
    }

    public interface JsWindow {
        void close(boolean removeFromWindows);
    }

    public class JsRawWindow implements JsWindow {

        private RawWindow mWindow;
        private boolean mExitOnClose;

        public JsRawWindow(RawWindow.RawFloaty floaty) {
            mWindow = new RawWindow(floaty);
            mUiHandler.post(() -> {
                mUiHandler.getContext().startService(new Intent(mUiHandler.getContext(), FloatyService.class));
                FloatyService.addWindow(mWindow);
            });
            RuntimeException exception = mWindow.waitForCreation();
            if (exception != Exceptions.NO_EXCEPTION && exception != null) {
                throw exception;
            }
        }

        public View findView(String id) {
            return JsViewHelper.findViewByStringId(mWindow.getContentView(), id);
        }

        public int getX() {
            return mWindow.getWindowBridge().getX();
        }

        public int getY() {
            return mWindow.getWindowBridge().getY();
        }

        public int getWidth() {
            return mWindow.getWindowView().getWidth();
        }

        public int getHeight() {
            return mWindow.getWindowView().getHeight();
        }

        public void setSize(int w, int h) {
            runWithWindow(() -> {
                        mWindow.getWindowBridge().updateMeasure(w, h);
                        ViewUtil.setViewMeasure(mWindow.getWindowView(), w, h);
                    }
            );
        }

        public void setTouchable(boolean touchable) {
            runWithWindow(() -> mWindow.setTouchable(touchable));
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

        public void exitOnClose() {
            mExitOnClose = true;
        }

        public void requestFocus() {
            mWindow.requestWindowFocus();
        }

        public void disableFocus() {
            mWindow.disableWindowFocus();
        }

        public void close() {
            close(true);
        }

        public void close(boolean removeFromWindows) {
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

    public class JsResizableWindow implements JsWindow {

        private View mView;
        private volatile BaseResizableFloatyWindow mWindow;
        private boolean mExitOnClose = false;

        public JsResizableWindow(BaseResizableFloatyWindow.ViewSupplier supplier) {
            mWindow = new BaseResizableFloatyWindow(mContext, (context, parent) -> {
                mView = supplier.inflate(context, parent);
                return mView;
            });
            mUiHandler.post(() -> {
                mUiHandler.getContext().startService(new Intent(mUiHandler.getContext(), FloatyService.class));
                FloatyService.addWindow(mWindow);
            });
            RuntimeException exception = mWindow.waitForCreation();
            if (exception != Exceptions.NO_EXCEPTION && exception != null) {
                throw exception;
            }
            mWindow.setOnCloseButtonClickListener(v -> close());
            //setSize(mWindow.getWindowBridge().getScreenWidth() / 2, mWindow.getWindowBridge().getScreenHeight() / 2);
        }

        public View findView(String id) {
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
            runWithWindow(() -> {
                        mWindow.getWindowBridge().updateMeasure(w, h);
                        ViewUtil.setViewMeasure(mWindow.getRootView(), w, h);
                    }
            );
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

        public void requestFocus() {
            mWindow.requestWindowFocus();
        }

        public void disableFocus() {
            mWindow.disableWindowFocus();
        }

        public void close() {
            close(true);
        }

        public void close(boolean removeFromWindows) {
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
