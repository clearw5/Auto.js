package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.autojs.runtime.record.inputevent.TouchObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.OnKeyListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/7/18.
 */

public class Events extends EventEmitter implements OnKeyListener, TouchObserver.OnTouchEventListener {

    private static final String PREFIX_KEY_DOWN = "__key_down__#";
    private static final String PREFIX_KEY_UP = "__key_up__#";

    private AccessibilityBridge mAccessibilityBridge;
    private Context mContext;
    private TouchObserver mTouchObserver;
    private Timers mTimers;
    private long mLastTouchEventMillis;
    private long mTouchEventTimeout = 10;
    private boolean mListeningKey = false;

    public Events(Context context, AccessibilityBridge accessibilityBridge, ScriptBridges bridges, Timers timers) {
        super(bridges);
        mAccessibilityBridge = accessibilityBridge;
        mContext = context;
        mTimers = timers;
    }

    public EventEmitter emitter(){
        return new EventEmitter(mBridges);
    }

    public void observeKey() {
        if (mListeningKey)
            return;
        mListeningKey = true;
        mTimers.prepareLoopIfNeeded();
        mAccessibilityBridge.ensureServiceEnabled();
        AccessibilityService service = mAccessibilityBridge.getService();
        if (service == null)
            throw new ScriptStopException();
        service.getOnKeyObserver().addListener(this);
    }

    public void observeTouch() {
        mTimers.prepareLoopIfNeeded();
        if (mTouchObserver != null)
            return;
        mTouchObserver = new TouchObserver(mContext);
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
    }

    @Override
    public void onKeyEvent(final int keyCode, final KeyEvent event) {
        mTimers.post(new Runnable() {
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
        mTimers.post(new Runnable() {
            @Override
            public void run() {
                emit("touch", new Point(x, y));
            }
        });
    }


}
