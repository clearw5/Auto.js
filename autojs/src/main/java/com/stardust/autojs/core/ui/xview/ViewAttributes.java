package com.stardust.autojs.core.ui.xview;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.Ids;
import com.stardust.autojs.core.ui.widget.NativeView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ViewAttributes {

    public interface Attribute {
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
    private final Drawables mDrawables;
    private final View mView;

    public ViewAttributes(Drawables drawables, View view) {
        mDrawables = drawables;
        mView = view;
        init();
    }

    public static ViewAttributes fromView(ResourceParser parser, View view) {
        ViewAttributes attributes;
        Object tag = view.getTag(R.id.view_tag_view_attrs);
        if (!(tag instanceof ViewAttributes)) {
            attributes = new ViewAttributes(parser.getDrawables(), view);
            view.setTag(R.id.view_tag_view_attrs, attributes);
        } else {
            attributes = (ViewAttributes) tag;
        }
        return attributes;
    }


    public boolean contains(String name) {
        return mAttributes.containsKey(name);
    }

    public Attribute get(String name) {
        return mAttributes.get(name);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        onRegisterAttrs();
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

}
