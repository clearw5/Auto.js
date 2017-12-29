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
import com.stardust.autojs.rhino.ProxyObject;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.util.UiHandler;
import com.stardust.util.ViewUtil;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2017/12/5.
 */

public class Floaty {

    private JsLayoutInflater mJsLayoutInflater;
    private Context mContext;
    private UiHandler mUiHandler;
    private Set<JsFloatyWindow> mWindows = new HashSet<>();

    public Floaty(UiHandler uiHandler, UI ui) {
        mUiHandler = uiHandler;
        mContext = new ContextThemeWrapper(mUiHandler.getContext(), R.style.AppTheme);
        mJsLayoutInflater = ui.getJsLayoutInflater();
    }

    public JsFloatyWindow window(String xml) {
        return window(inflate(xml));
    }

    public JsFloatyWindow window(View view) {
        JsFloatyWindow window = new JsFloatyWindow(view);
        mWindows.add(window);
        return window;
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

    public void closeAll() {
        Iterator<JsFloatyWindow> iterator = mWindows.iterator();
        while (iterator.hasNext()) {
            JsFloatyWindow window = iterator.next();
            if (Looper.myLooper() == Looper.getMainLooper()) {
                window.mWindow.close();
            } else {
                mUiHandler.post(() -> window.mWindow.close());
            }
            iterator.remove();
        }
    }

    public class JsFloatyWindow {

        private Map<String, View> mViewCache = new HashMap<>();
        private View mView;
        private FloatyWindow mWindow;

        public JsFloatyWindow(View view) {
            mWindow = new FloatyWindow(view);
            mUiHandler.post(() -> {
                mUiHandler.getContext().startService(new Intent(mUiHandler.getContext(), FloatyService.class));
                FloatyService.addWindow(mWindow);
            });
            mWindow.waitFor();
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
            return mWindow.getWindowBridge().getWidth();
        }

        public int getHeight() {
            return mWindow.getWindowBridge().getHeight();
        }

        public void setSize(int w, int h) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                ViewUtil.setViewMeasure(mWindow.getRootView(), w, h);
               // mWindow.getWindowBridge().updateMeasure(w, h);
            } else {
                mUiHandler.post(() -> {
                    ViewUtil.setViewMeasure(mWindow.getRootView(), w, h);
                    //  mWindow.getWindowBridge().updateMeasure(w, h);
                });
            }
        }

        public void setPosition(int x, int y) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mWindow.getWindowBridge().updatePosition(x, y);
            } else {
                mUiHandler.post(() -> mWindow.getWindowBridge().updatePosition(x, y));
            }
        }

        public void setAdjustEnabled(boolean enabled) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mWindow.setAdjustEnabled(enabled);
            } else {
                mUiHandler.post(() -> mWindow.setAdjustEnabled(enabled));
            }
        }

        public boolean isAdjustEnabled() {
            return mWindow.isAdjustEnabled();
        }

        public void close() {
            if (!mWindows.remove(this)) {
                return;
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mWindow.close();
            } else {
                mUiHandler.post(() -> mWindow.close());
            }
        }
    }


}
