package com.stardust.autojs.core.ui.dialog;

import android.content.Context;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2018/4/17.
 */

public class JsDialog extends MaterialDialog {

    private final EventEmitter mEmitter;
    private final UiHandler mUiHandler;

    public JsDialog(Builder builder, EventEmitter emitter, UiHandler uiHandler) {
        super(builder);
        mEmitter = emitter;
        mUiHandler = uiHandler;
    }

    public EventEmitter once(String eventName, Object listener) {
        return mEmitter.once(eventName, listener);
    }

    public EventEmitter on(String eventName, Object listener) {
        return mEmitter.on(eventName, listener);
    }

    public EventEmitter addListener(String eventName, Object listener) {
        return mEmitter.addListener(eventName, listener);
    }

    public boolean emit(String eventName, Object... args) {
        return mEmitter.emit(eventName, args);
    }

    public String[] eventNames() {
        return mEmitter.eventNames();
    }

    public int listenerCount(String eventName) {
        return mEmitter.listenerCount(eventName);
    }

    public Object[] listeners(String eventName) {
        return mEmitter.listeners(eventName);
    }

    public EventEmitter prependListener(String eventName, Object listener) {
        return mEmitter.prependListener(eventName, listener);
    }

    public EventEmitter prependOnceListener(String eventName, Object listener) {
        return mEmitter.prependOnceListener(eventName, listener);
    }

    public EventEmitter removeAllListeners() {
        return mEmitter.removeAllListeners();
    }

    public EventEmitter removeAllListeners(String eventName) {
        return mEmitter.removeAllListeners(eventName);
    }

    public EventEmitter removeListener(String eventName, Object listener) {
        return mEmitter.removeListener(eventName, listener);
    }

    public EventEmitter setMaxListeners(int n) {
        return mEmitter.setMaxListeners(n);
    }

    public int getMaxListeners() {
        return mEmitter.getMaxListeners();
    }

    public static int defaultMaxListeners() {
        return EventEmitter.defaultMaxListeners();
    }

    @Override
    public void show() {
        checkWindowType();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.show();
        } else {
            mUiHandler.post(super::show);
        }
    }

    private void checkWindowType() {
        Context context = getContext();
        if (!DialogUtils.isActivityContext(context)) {
            Window window = getWindow();
            if (window != null)
                window.setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
    }
}
