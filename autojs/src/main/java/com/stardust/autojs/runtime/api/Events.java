package com.stardust.autojs.runtime.api;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.autojs.R;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.notification.Notification;
import com.stardust.notification.NotificationListenerService;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.inputevent.TouchObserver;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.NotificationListener;
import com.stardust.view.accessibility.OnKeyListener;

/**
 * Created by Stardust on 2017/7/18.
 */

public class Events extends EventEmitter implements OnKeyListener, TouchObserver.OnTouchEventListener, NotificationListener, AccessibilityNotificationObserver.ToastListener {

    private static final String PREFIX_KEY_DOWN = "__key_down__#";
    private static final String PREFIX_KEY_UP = "__key_up__#";

    private AccessibilityBridge mAccessibilityBridge;
    private Context mContext;
    private TouchObserver mTouchObserver;
    private long mLastTouchEventMillis;
    private long mTouchEventTimeout = 10;
    private boolean mListeningKey = false;
    private Loopers mLoopers;
    private Handler mHandler;
    private boolean mListeningNotification = false;
    private ScriptRuntime mScriptRuntime;

    public Events(Context context, AccessibilityBridge accessibilityBridge, ScriptRuntime runtime) {
        super(runtime.bridges);
        mAccessibilityBridge = accessibilityBridge;
        mContext = context;
        mLoopers = runtime.loopers;
        mScriptRuntime = runtime;
    }

    public EventEmitter emitter() {
        return new EventEmitter(mBridges);
    }

    public void observeKey() {
        if (mListeningKey)
            return;
        mScriptRuntime.ensureAccessibilityServiceEnabled();
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service == null)
            throw new ScriptException("AccessibilityService = null");
        if ((service.getServiceInfo().flags & AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS) == 0) {
            throw new ScriptException(mContext.getString(R.string.text_should_enable_key_observing));
        }
        ensureHandler();
        mLoopers.waitWhenIdle(true);
        mListeningKey = true;
        mAccessibilityBridge.ensureServiceEnabled();
        service.getOnKeyObserver().addListener(this);
    }

    private void ensureHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    public void observeTouch() {
        if (mTouchObserver != null)
            return;
        ensureHandler();
        mLoopers.waitWhenIdle(true);
        mTouchObserver = new TouchObserver(InputEventObserver.getGlobal());
        mTouchObserver.setOnTouchEventListener(this);
        mTouchObserver.observe();
    }

    public Events onKeyDown(String keyName, Object listener) {
        on(PREFIX_KEY_DOWN + keyName, listener);
        return this;
    }

    public Events onceKeyDown(String keyName, Object listener) {
        once(PREFIX_KEY_DOWN + keyName, listener);
        return this;
    }

    public Events removeAllKeyDownListeners(String keyName) {
        removeAllListeners(PREFIX_KEY_DOWN + keyName);
        return this;
    }

    public Events onKeyUp(String keyName, Object listener) {
        on(PREFIX_KEY_UP + keyName, listener);
        return this;
    }

    public Events onceKeyUp(String keyName, Object listener) {
        once(PREFIX_KEY_UP + keyName, listener);
        return this;
    }

    public Events removeAllKeyUpListeners(String keyName) {
        removeAllListeners(PREFIX_KEY_UP + keyName);
        return this;
    }

    public Events onTouch(Object listener) {
        on("touch", listener);
        return this;
    }

    public Events removeAllTouchListeners() {
        removeAllListeners("touch");
        return this;
    }

    public long getTouchEventTimeout() {
        return mTouchEventTimeout;
    }

    public void setTouchEventTimeout(long touchEventTimeout) {
        mTouchEventTimeout = touchEventTimeout;
    }


    public void observeNotification() {
        if (mListeningNotification)
            return;
        mAccessibilityBridge.ensureServiceEnabled();
        mListeningNotification = true;
        ensureHandler();
        mLoopers.waitWhenIdle(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && NotificationListenerService.getInstance() != null) {
            NotificationListenerService.getInstance().addListener(this);
        } else {
            mAccessibilityBridge.getNotificationObserver().addNotificationListener(this);
        }
        mAccessibilityBridge.getNotificationObserver().addToastListener(this);
    }

    public Events onNotification(Object listener) {
        on("notification", listener);
        return this;
    }

    public Events onToast(Object listener) {
        on("toast", listener);
        return this;
    }


    public void recycle() {
        if (mListeningKey) {
            AccessibilityService service = mAccessibilityBridge.getService();
            if (service != null) {
                service.getOnKeyObserver().removeListener(this);
                mListeningKey = false;
            }
        }
        if (mTouchObserver != null) {
            mTouchObserver.stop();
        }
        if (mListeningNotification) {
            mAccessibilityBridge.getNotificationObserver().removeNotificationListener(this);
            mAccessibilityBridge.getNotificationObserver().removeToastListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && NotificationListenerService.getInstance() != null) {
                NotificationListenerService.getInstance().removeListener(this);
            }
        }
    }

    @Override
    public void onKeyEvent(final int keyCode, final KeyEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String keyName = KeyEvent.keyCodeToString(keyCode).substring(8).toLowerCase();
                emit(keyName, event);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    emit(PREFIX_KEY_DOWN + keyName, event);
                    emit("key_down", keyCode, event);
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    emit(PREFIX_KEY_UP + keyName, event);
                    emit("key_up", keyCode, event);
                }
                emit("key", keyCode, event);
            }
        });
    }

    @Override
    public void onTouch(final int x, final int y) {
        if (System.currentTimeMillis() - mLastTouchEventMillis < mTouchEventTimeout) {
            return;
        }
        mLastTouchEventMillis = System.currentTimeMillis();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                emit("touch", new Point(x, y));
            }
        });
    }

    public void onNotification(final Notification notification) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                emit("notification", notification);
            }
        });

    }

    @Override
    public void onToast(final AccessibilityNotificationObserver.Toast toast) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                emit("toast", toast);
            }
        });
    }
}
