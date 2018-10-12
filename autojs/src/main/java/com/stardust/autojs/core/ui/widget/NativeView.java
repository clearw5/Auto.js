package com.stardust.autojs.core.ui.widget;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.ui.JsEvent;
import com.stardust.autojs.core.ui.xview.ViewAttributes;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class NativeView extends NativeJavaObjectWithPrototype {

    public static class ScrollEvent {
        public int scrollX;
        public int scrollY;
        public int oldScrollX;
        public int oldScrollY;

        public ScrollEvent(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }


    private final ViewAttributes mViewAttributes;
    private final View mView;
    private final EventEmitter mEventEmitter;
    private final NativeJavaObject mNativeEventEmitter;

    public NativeView(Scriptable scope, View javaObject, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, javaObject, staticType);
        mViewAttributes = ViewAttributes.fromView(runtime.ui.getResourceParser(), javaObject);
        mEventEmitter = runtime.events.emitter();
        mView = javaObject;
        mNativeEventEmitter = new NativeJavaObject(scope, mEventEmitter, mEventEmitter.getClass());
        init();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        mView.setOnTouchListener((v, event) -> {
            JsEvent e = new JsEvent(getParentScope(), event, event.getClass());
            emit("touch", e, v);
            return e.isConsumed();
        });
        mView.setOnClickListener(v -> emit("click", v));
        mView.setOnLongClickListener(v -> {
            emit("long_click", v);
            return false;
        });
        mView.setOnKeyListener((v, keyCode, event) -> {
            JsEvent e = new JsEvent(getParentScope(), event, event.getClass());
            emit("key", e, keyCode, v);
            return e.isConsumed();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                    emit("scroll_change", new ScrollEvent(scrollX, scrollY, oldScrollX, oldScrollY), v)
            );
        }
    }


    @Override
    public boolean has(String name, Scriptable start) {
        if (mViewAttributes.contains(name)) {
            return true;
        }
        return super.has(name, start) || mNativeEventEmitter.has(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        ViewAttributes.Attribute attribute = mViewAttributes.get(name);
        if (attribute != null) {
            attribute.set(ScriptRuntime.toString(value));
            return;
        }
        if (mNativeEventEmitter.has(name, start)) {
            mNativeEventEmitter.put(name, start, value);
        } else {
            super.put(name, start, value);
        }
    }

    @Override
    public Object get(String name, Scriptable start) {
        ViewAttributes.Attribute attribute = mViewAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
        }
        Object o = mNativeEventEmitter.get(name, start);
        if (o != Scriptable.NOT_FOUND) {
            return o;
        }
        return super.get(name, start);
    }


    @Override
    public View unwrap() {
        return mView;
    }

    public EventEmitter once(String eventName, Object listener) {
        return mEventEmitter.once(eventName, listener);
    }

    public EventEmitter on(String eventName, Object listener) {
        return mEventEmitter.on(eventName, listener);
    }

    public EventEmitter addListener(String eventName, Object listener) {
        return mEventEmitter.addListener(eventName, listener);
    }

    public boolean emit(String eventName, Object... args) {
        return mEventEmitter.emit(eventName, args);
    }

    public String[] eventNames() {
        return mEventEmitter.eventNames();
    }

    public int listenerCount(String eventName) {
        return mEventEmitter.listenerCount(eventName);
    }

    public Object[] listeners(String eventName) {
        return mEventEmitter.listeners(eventName);
    }

    public EventEmitter prependListener(String eventName, Object listener) {
        return mEventEmitter.prependListener(eventName, listener);
    }

    public EventEmitter prependOnceListener(String eventName, Object listener) {
        return mEventEmitter.prependOnceListener(eventName, listener);
    }

    public EventEmitter removeAllListeners() {
        return mEventEmitter.removeAllListeners();
    }

    public EventEmitter removeAllListeners(String eventName) {
        return mEventEmitter.removeAllListeners(eventName);
    }

    public EventEmitter removeListener(String eventName, Object listener) {
        return mEventEmitter.removeListener(eventName, listener);
    }

    public EventEmitter setMaxListeners(int n) {
        return mEventEmitter.setMaxListeners(n);
    }

    public int getMaxListeners() {
        return mEventEmitter.getMaxListeners();
    }

    public static int defaultMaxListeners() {
        return EventEmitter.defaultMaxListeners();
    }
}
