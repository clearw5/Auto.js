package com.stardust.autojs.core.ui.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.view.View;

import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Ids;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

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


    private Map<String, Attribute> mAttributes = new HashMap<>();
    private final com.stardust.autojs.runtime.ScriptRuntime mRuntime;
    private final Drawables mDrawables;
    private final View mView;

    public NativeView(Scriptable scope, View javaObject, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, javaObject, staticType);
        mRuntime = runtime;
        mDrawables = mRuntime.ui.getResourceParser().getDrawables();
        mView = javaObject;
        init();
    }


    public NativeView(Scriptable scope, View javaObject, Class<?> staticType, boolean isAdapter, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, javaObject, staticType, isAdapter);
        mRuntime = runtime;
        mDrawables = mRuntime.ui.getResourceParser().getDrawables();
        mView = javaObject;
        init();
    }

    private void init() {
        onRegisterAttrs();
    }

    @CallSuper
    protected void onRegisterAttrs() {
        registerAttr("id", Ids::parse, mView::setId);
        registerDrawableAttr("bg", mView::setBackground);


    }

    @Override
    public boolean has(String name, Scriptable start) {
        if (mAttributes.containsKey(name)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        Attribute attribute = mAttributes.get(name);
        if (attribute != null) {
            attribute.set(ScriptRuntime.toString(value));
            return;
        }
        super.put(name, start, value);
    }

    @Override
    public Object get(String name, Scriptable start) {
        Attribute attribute = mAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
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

    protected void registerDrawableAttr(String name, ValueApplier<Drawable> applier) {
        mAttributes.put(name, new BaseAttribute(new MappingAttributeSetter<>(
                this::parseDrawable, applier)));
    }


    protected Drawable parseDrawable(String value) {
        return mDrawables.parse(mView, value);
    }

    @Override
    public View unwrap() {
        return mView;
    }
}
