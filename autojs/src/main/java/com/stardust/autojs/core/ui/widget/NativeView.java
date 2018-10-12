package com.stardust.autojs.core.ui.widget;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.ui.JsEvent;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.Ids;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeJavaMethod;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NativeView extends NativeJavaObjectWithPrototype {

    protected interface Attribute {
        String get();

        void set(String value);
    }


    protected class BaseAttribute implements Attribute {

        private String mValue;
        private final AttributeSetter mAttributeSetter;

        public BaseAttribute(AttributeSetter attributeSetter) {
            mAttributeSetter = attributeSetter;
        }

        @Override
        public String get() {
            return mValue;
        }

        @Override
        public void set(String value) {
            mValue = value;
            mAttributeSetter.set(value);
        }
    }

    protected interface AttributeGetter {
        String get();
    }


    protected interface AttributeSetter {
        void set(String value);
    }

    protected interface ValueConverter<T> {
        T convert(String value);
    }

    protected interface ValueApplier<T> {
        void apply(T value);
    }

    protected static class MappingAttributeSetter<T> implements AttributeSetter {

        private final ValueConverter<T> mValueConverter;
        private final ValueApplier<T> mValueApplier;

        public MappingAttributeSetter(ValueConverter<T> valueConverter, ValueApplier<T> valueApplier) {
            mValueConverter = valueConverter;
            mValueApplier = valueApplier;
        }

        @Override
        public void set(String value) {
            mValueApplier.apply(mValueConverter.convert(value));
        }
    }

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


    private Map<String, Attribute> mAttributes = new HashMap<>();
    private final com.stardust.autojs.runtime.ScriptRuntime mRuntime;
    private final Drawables mDrawables;
    private final View mView;
    private final EventEmitter mEventEmitter;
    private final NativeJavaObject mNativeEventEmitter;

    public NativeView(Scriptable scope, View javaObject, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, javaObject, staticType);
        mRuntime = runtime;
        mDrawables = mRuntime.ui.getResourceParser().getDrawables();
        mEventEmitter = runtime.events.emitter();
        mView = javaObject;
        mNativeEventEmitter = new NativeJavaObject(scope, mEventEmitter, mEventEmitter.getClass());
        init();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        onRegisterAttrs();
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

    @CallSuper
    protected void onRegisterAttrs() {
        registerAttr("id", Ids::parse, mView::setId);
        registerAttr("gravity", Gravities::parse, this::setGravity);
        registerAttrs(new String[]{"width", "layout_width", "w"}, this::parseDimension, this::setWidth);
        registerAttrs(new String[]{"height", "layout_height", "h"}, this::parseDimension, this::setHeight);
        registerDrawableAttrs(new String[]{"bg", "background"}, mView::setBackground);
        registerAttr("layout_gravity", Gravities::parse, this::setLayoutGravity);
        registerAttr("layout_weight", Float::parseFloat, this::setLayoutWeight);
    }


    private int parseDimension(String dim) {
        switch (dim) {
            case "wrap_content":
                return ViewGroup.LayoutParams.WRAP_CONTENT;
            case "fill_parent":
            case "match_parent":
                return ViewGroup.LayoutParams.MATCH_PARENT;
            default:
                return Dimensions.parseToPixel(dim, mView.getResources().getDisplayMetrics(), (ViewGroup) mView.getParent(), true);
        }
    }


    @Override
    public boolean has(String name, Scriptable start) {
        if (mAttributes.containsKey(name)) {
            return true;
        }
        return super.has(name, start) || mNativeEventEmitter.has(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        Attribute attribute = mAttributes.get(name);
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
        Attribute attribute = mAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
        }
        Object o = mNativeEventEmitter.get(name, start);
        if(o != Scriptable.NOT_FOUND){
            return o;
        }
        return super.get(name, start);
    }

    protected void registerAttr(String name, Attribute attribute) {
        mAttributes.put(name, attribute);
    }

    protected void registerAttr(String name, AttributeGetter getter, AttributeSetter setter) {
        mAttributes.put(name, new Attribute() {
            @Override
            public String get() {
                return getter.get();
            }

            @Override
            public void set(String value) {
                setter.set(value);
            }
        });
    }

    protected void registerAttr(String name, AttributeSetter setter) {
        mAttributes.put(name, new BaseAttribute(setter));
    }

    protected <T> void registerAttr(String name, ValueConverter<T> converter, ValueApplier<T> applier) {
        mAttributes.put(name, new BaseAttribute(new MappingAttributeSetter<>(converter, applier)));
    }

    protected <T> void registerAttrs(String[] names, ValueConverter<T> converter, ValueApplier<T> applier) {
        registerAttrs(names, new MappingAttributeSetter<>(converter, applier));
    }

    protected <T> void registerAttrs(String[] names, AttributeSetter setter) {
        registerAttrs(names, new BaseAttribute(setter));
    }

    protected <T> void registerAttrs(String[] names, Attribute attribute) {
        for (String name : names) {
            mAttributes.put(name, attribute);
        }
    }

    protected void registerDrawableAttr(String name, ValueApplier<Drawable> applier) {
        mAttributes.put(name, new BaseAttribute(new MappingAttributeSetter<>(
                this::parseDrawable, applier)));
    }


    private void registerDrawableAttrs(String[] names, ValueApplier<Drawable> applier) {
        registerAttrs(names, new BaseAttribute(new MappingAttributeSetter<>(
                this::parseDrawable, applier)));
    }


    protected Drawable parseDrawable(String value) {
        return mDrawables.parse(mView, value);
    }

    private boolean setGravity(int g) {
        try {
            Method setGravity = mView.getClass().getMethod("setGravity", int.class);
            setGravity.invoke(mView, g);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setLayoutGravity(int gravity) {
        ViewParent parent = mView.getParent();
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        if (parent instanceof LinearLayout) {
            ((LinearLayout.LayoutParams) layoutParams).gravity = gravity;
            mView.setLayoutParams(layoutParams);
        } else if (parent instanceof FrameLayout) {
            ((FrameLayout.LayoutParams) layoutParams).gravity = gravity;
            mView.setLayoutParams(layoutParams);
        } else {
            try {
                Field field = layoutParams.getClass().getField("gravity");
                field.set(layoutParams, gravity);
                mView.setLayoutParams(layoutParams);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO throw or ?
            }
        }
    }

    private void setLayoutWeight(float weight) {
        ViewParent parent = mView.getParent();
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        if (parent instanceof LinearLayout) {
            ((LinearLayout.LayoutParams) layoutParams).weight = weight;
            mView.setLayoutParams(layoutParams);
        }
    }


    private void setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.width = width;
        mView.setLayoutParams(layoutParams);
    }


    private void setHeight(int height) {
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = height;
        mView.setLayoutParams(layoutParams);
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
