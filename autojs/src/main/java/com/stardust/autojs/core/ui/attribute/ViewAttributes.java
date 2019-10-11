package com.stardust.autojs.core.ui.attribute;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.CallSuper;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.stardust.autojs.core.internal.Functions;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.Ids;
import com.stardust.autojs.core.ui.inflater.util.Strings;
import com.stardust.util.BiMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.DRAWABLE_CACHE_QUALITIES;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.IMPORTANT_FOR_ACCESSIBILITY;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.LAYOUT_DIRECTIONS;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.SCROLLBARS_STYLES;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.SCROLL_INDICATORS;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.TEXT_ALIGNMENTS;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.TEXT_DIRECTIONS;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.TINT_MODES;
import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.VISIBILITY;

public class ViewAttributes {

    public interface Getter<T> {
        T get();
    }

    public interface Setter<T> {
        void set(T value);
    }

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

    protected interface AttributeGetter extends Getter<String> {
    }


    protected interface AttributeSetter extends Setter<String> {
    }

    protected interface ValueConverter<T> {
        T convert(String value);
    }

    protected static class MappingAttributeSetter<T> implements AttributeSetter {

        private final ValueConverter<T> mValueConverter;
        private final Setter<T> mSetter;

        public MappingAttributeSetter(ValueConverter<T> valueConverter, Setter<T> Setter) {
            mValueConverter = valueConverter;
            mSetter = Setter;
        }

        @Override
        public void set(String value) {
            mSetter.set(mValueConverter.convert(value));
        }
    }


    private Map<String, Attribute> mAttributes = new HashMap<>();
    private final Drawables mDrawables;
    private final View mView;
    private ViewAttributeDelegate mViewAttributeDelegate;

    public ViewAttributes(ResourceParser resourceParser, View view) {
        mDrawables = resourceParser.getDrawables();
        mView = view;
        init();
    }

    public void setViewAttributeDelegate(ViewAttributeDelegate viewAttributeDelegate) {
        mViewAttributeDelegate = viewAttributeDelegate;
    }

    public Drawables getDrawables() {
        return mDrawables;
    }

    public View getView() {
        return mView;
    }

    public boolean contains(String name) {
        return mAttributes.containsKey(name) ||
                (mViewAttributeDelegate != null && mViewAttributeDelegate.has(name));
    }

    public Attribute get(String name) {
        if (mViewAttributeDelegate != null && mViewAttributeDelegate.has(name)) {
            return new Attribute() {
                @Override
                public String get() {
                    return mViewAttributeDelegate.get(getView(), name, ViewAttributes.this::getAttrValue);
                }

                @Override
                public void set(String value) {
                    mViewAttributeDelegate.set(getView(), name, value, ViewAttributes.this::setAttrValue);
                }
            };
        }
        return mAttributes.get(name);
    }

    public String getAttrValue(String name) {
        Attribute attribute = mAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
        }
        return null;
    }

    public void setAttrValue(String name, String value) {
        Attribute attribute = mAttributes.get(name);
        if (attribute != null) {
            attribute.set(value);
        }
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
        registerAttr("layout_margin", this::setMargin);
        registerAttr("layout_marginLeft", this::parseDimension, this::setMarginLeft);
        registerAttr("layout_marginRight", this::parseDimension, this::setMarginRight);
        registerAttr("layout_marginTop", this::parseDimension, this::setMarginTop);
        registerAttr("layout_marginBottom", this::parseDimension, this::setMarginBottom);
        registerAttr("layout_marginStart", this::parseDimension, this::setMarginStart);
        registerAttr("layout_marginEnd", this::parseDimension, this::setMarginEnd);
        registerAttr("padding",this::setPadding);
        registerAttr("paddingLeft", this::parseDimension, this::setPaddingLeft);
        registerAttr("paddingRight", this::parseDimension, this::setPaddingRight);
        registerAttr("paddingTop", this::parseDimension, this::setPaddingTop);
        registerAttr("paddingBottom", this::parseDimension, this::setPaddingBottom);
        registerAttr("paddingStart", this::parseDimension, this::setPaddingStart);
        registerAttr("paddingEnd", this::parseDimension, this::setPaddingEnd);
        registerAttr("alpha", Float::parseFloat, mView::setAlpha);
        registerAttr("backgroundTint", Color::parseColor, this::setBackgroundTint);
        registerAttr("backgroundTintMode", TINT_MODES::get,
                bind(ViewCompat::setBackgroundTintMode, mView));
        registerAttr("clickable", Boolean::parseBoolean, mView::setClickable);
        registerAttr("checked", Boolean::parseBoolean, this::setChecked);
        registerAttr("contentDescription", this::parseString, mView::setContentDescription);
        registerAttr("contextClickable", Boolean::parseBoolean, this::setContextClickable);
        registerAttr("scaleX", Float::parseFloat, mView::setScaleX);
        registerAttr("scaleY", Float::parseFloat, mView::setScaleY);
        registerAttr("rotation", Float::parseFloat, mView::setRotation);
        registerAttr("rotationX", Float::parseFloat, mView::setRotationX);
        registerAttr("rotationY", Float::parseFloat, mView::setRotationY);
        registerAttr("saveEnabled", Boolean::parseBoolean, mView::setSaveEnabled);
        registerAttr("transformPivotX", this::parseDimensionToPixel, mView::setPivotX);
        registerAttr("transformPivotY", this::parseDimensionToPixel, mView::setPivotY);
        registerAttr("translationX", this::parseDimensionToPixel, mView::setTranslationX);
        registerAttr("translationY", this::parseDimensionToPixel, mView::setTranslationY);
        registerAttr("visibility", VISIBILITY::get, mView::setVisibility);
        registerAttr("tag", this::parseString, mView::setTag);
        registerAttr("soundEffectsEnabled", Boolean::parseBoolean, mView::setSoundEffectsEnabled);
        registerAttr("scrollbarStyle", SCROLLBARS_STYLES::get, mView::setScrollBarStyle);
        registerAttr("scrollX", this::parseDimensionToIntPixel, mView::setScrollX);
        registerAttr("scrollY", this::parseDimensionToIntPixel, mView::setScrollY);
        registerAttr("scrollIndicators", SCROLL_INDICATORS::get, bind(ViewCompat::setScrollIndicators, mView));
        registerAttr("scrollbarDefaultDelayBeforeFade", Integer::valueOf, mView::setScrollBarDefaultDelayBeforeFade);
        registerAttr("scrollbarFadeDuration", Integer::valueOf, mView::setScrollBarFadeDuration);
        registerAttr("scrollbarSize", this::parseDimensionToIntPixel, mView::setScrollBarSize);
        registerAttr("textAlignment", TEXT_ALIGNMENTS::get, mView::setTextAlignment);
        registerAttr("textDirection", TEXT_DIRECTIONS::get, mView::setTextDirection);
        registerAttr("transitionName", this::parseString, bind(ViewCompat::setTransitionName, mView));
        registerAttr("translationZ", this::parseDimensionToPixel, bind(ViewCompat::setTranslationZ, mView));
        registerAttr("scrollbars", this::setScrollbars);
        registerAttr("drawingCacheQuality", DRAWABLE_CACHE_QUALITIES::get, mView::setDrawingCacheQuality);
        registerAttr("duplicateParentState", Boolean::parseBoolean, mView::setDuplicateParentStateEnabled);
        registerAttr("fadeScrollbars", Boolean::valueOf, mView::setScrollbarFadingEnabled);
        registerAttr("fadingEdgeLength", this::parseDimensionToIntPixel, mView::setFadingEdgeLength);
        registerAttr("filterTouchesWhenObscured", Boolean::valueOf, mView::setFilterTouchesWhenObscured);
        registerAttr("fitsSystemWindows", Boolean::valueOf, mView::setFitsSystemWindows);
        registerAttr("focusable", Boolean::valueOf, mView::setFocusable);
        registerAttr("focusableInTouchMode", Boolean::valueOf, mView::setFocusableInTouchMode);
        registerAttr("hapticFeedbackEnabled", Boolean::valueOf, mView::setHapticFeedbackEnabled);
        registerAttr("isScrollContainer", Boolean::valueOf, mView::setScrollContainer);
        registerAttr("keepScreenOn", Boolean::valueOf, mView::setKeepScreenOn);
        registerAttr("longClickable", Boolean::valueOf, mView::setLongClickable);
        registerAttr("minHeight", this::parseDimensionToIntPixel, mView::setMinimumHeight);
        registerAttr("minWidth", this::parseDimensionToIntPixel, mView::setMinimumWidth);
        registerAttr("elevation", this::parseDimensionToIntPixel, this::setElevation);
        registerAttr("forceHasOverlappingRendering", Boolean::valueOf, this::forceHasOverlappingRendering);
        registerDrawableAttr("foreground", this::setForeground);
        registerAttr("foregroundGravity", Gravities::parse, this::setForegroundGravity);
        registerAttr("foregroundTintMode", TINT_MODES::get, this::setForegroundTintMode);
        registerAttr("importantForAccessibility", IMPORTANT_FOR_ACCESSIBILITY::get, mView::setImportantForAccessibility);
        registerAttr("layoutDirection", LAYOUT_DIRECTIONS::get, mView::setLayoutDirection);
    }

    protected void setForegroundTintMode(PorterDuff.Mode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.setForegroundTintMode(mode);
        }
    }

    protected void setForegroundGravity(int g) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.setForegroundGravity(g);
        }
    }

    protected void setForeground(Drawable foreground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.setForeground(foreground);
        }
    }

    protected void forceHasOverlappingRendering(boolean b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mView.forceHasOverlappingRendering(b);
        }
    }

    protected void setElevation(int e) {
        ViewCompat.setElevation(mView, e);
    }

    protected void setScrollbars(String scrollbars) {
        for (String str : scrollbars.split("\\|")) {
            if (str.equals("horizontal")) {
                mView.setHorizontalScrollBarEnabled(true);
            } else if (str.equals("vertical")) {
                mView.setVerticalScrollBarEnabled(true);
            }
        }
    }


    protected float parseDimensionToPixel(String value) {
        return Dimensions.parseToPixel(mView, value);
    }

    protected int parseDimensionToIntPixel(String value) {
        return Dimensions.parseToIntPixel(value, mView);
    }


    protected int parseDimension(String dim) {
        switch (dim) {
            case "wrap_content":
                return ViewGroup.LayoutParams.WRAP_CONTENT;
            case "fill_parent":
            case "match_parent":
                return ViewGroup.LayoutParams.MATCH_PARENT;
            default:
                return Dimensions.parseToPixel(dim, mView, (ViewGroup) mView.getParent(), true);
        }
    }

    protected void registerAttr(String name, Attribute attribute) {
        mAttributes.put(name, attribute);
    }

    protected <V> void registerAttr(String name, Getter<V> getter, Setter<V> setter, BiMap<String, V> biMap) {
        mAttributes.put(name, new Attribute() {
            @Override
            public String get() {
                return biMap.getKey(getter.get());
            }

            @Override
            public void set(String value) {
                V v = biMap.get(value);
                setter.set(v);
            }
        });
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

    protected <T> void registerAttr(String name, ValueConverter<T> converter, Setter<T> applier) {
        mAttributes.put(name, new BaseAttribute(new MappingAttributeSetter<>(converter, applier)));
    }

    protected <T> void registerAttrs(String[] names, ValueConverter<T> converter, Setter<T> applier) {
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

    protected void registerDrawableAttr(String name, Setter<Drawable> applier) {
        mAttributes.put(name, new BaseAttribute(new MappingAttributeSetter<>(
                this::parseDrawable, applier)));
    }


    protected void registerDrawableAttrs(String[] names, Setter<Drawable> applier) {
        registerAttrs(names, new BaseAttribute(new MappingAttributeSetter<>(
                this::parseDrawable, applier)));
    }

    protected void registerPixelAttr(String name, Setter<Float> applier) {
        registerAttr(name, this::parseDimensionToPixel, applier);
    }

    protected void registerIntPixelAttr(String name, Setter<Integer> applier) {
        registerAttr(name, this::parseDimensionToIntPixel, applier);
    }

    protected void registerIdAttr(String name, Setter<Integer> applier) {
        registerAttr(name, Ids::parse, applier);
    }

    protected void registerBooleanAttr(String name, Setter<Boolean> applier) {
        registerAttr(name, Boolean::parseBoolean, applier);
    }


    protected Drawable parseDrawable(String value) {
        return mDrawables.parse(mView, value);
    }

    protected boolean setGravity(int g) {
        try {
            Method setGravity = mView.getClass().getMethod("setGravity", int.class);
            setGravity.invoke(mView, g);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void setMargin(String  margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            int[] pixels = Dimensions.parseToIntPixelArray(getView(), margin);
            params.setMargins(pixels[0], pixels[1], pixels[2], pixels[3]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(pixels[0]);
                params.setMarginEnd(pixels[2]);
            }
            getView().setLayoutParams(params);
        }
    }

    protected void setMarginLeft(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.leftMargin = margin;
        }
    }

    protected void setMarginRight(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.rightMargin = margin;
        }
    }

    protected void setMarginTop(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.topMargin = margin;
        }
    }

    protected void setMarginBottom(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.bottomMargin = margin;


        }
    }

    protected void setMarginStart(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.setMarginStart(margin);
        }
    }

    protected void setMarginEnd(int margin) {
        if (mView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
            params.setMarginEnd(margin);
        }
    }

    protected void setPadding(String padding) {
        int[] pixels = Dimensions.parseToIntPixelArray(getView(), padding);
        mView.setPadding(pixels[0], pixels[1], pixels[2], pixels[3]);
    }

    protected void setPaddingLeft(int padding) {
        mView.setPadding(padding, mView.getPaddingTop(), mView.getPaddingRight(), mView.getPaddingBottom());
    }


    protected void setPaddingRight(int padding) {
        mView.setPadding(mView.getPaddingLeft(), mView.getPaddingTop(), padding, mView.getPaddingBottom());
    }


    protected void setPaddingTop(int padding) {
        mView.setPadding(mView.getPaddingLeft(), padding, mView.getPaddingRight(), mView.getPaddingBottom());
    }


    protected void setPaddingStart(int padding) {
        mView.setPaddingRelative(padding, mView.getPaddingTop(), mView.getPaddingEnd(), mView.getPaddingBottom());
    }


    protected void setPaddingEnd(int padding) {
        mView.setPaddingRelative(mView.getPaddingStart(), mView.getPaddingTop(), padding, mView.getPaddingBottom());
    }


    protected void setPaddingBottom(int padding) {
        mView.setPadding(mView.getPaddingLeft(), mView.getPaddingTop(), mView.getPaddingRight(), padding);
    }

    protected void setBackgroundTint(int color) {
        ViewCompat.setBackgroundTintList(mView, ColorStateList.valueOf(color));
    }

    protected void setContextClickable(boolean clickable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.setContextClickable(clickable);
        }

    }

    protected void setChecked(boolean checked) {
        if (mView instanceof CompoundButton) {
            ((CompoundButton) mView).setChecked(checked);
        }
    }

    protected void setLayoutGravity(int gravity) {
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

    protected void setLayoutWeight(float weight) {
        ViewParent parent = mView.getParent();
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        if (parent instanceof LinearLayout) {
            ((LinearLayout.LayoutParams) layoutParams).weight = weight;
            mView.setLayoutParams(layoutParams);
        }
    }


    protected void setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.width = width;
        mView.setLayoutParams(layoutParams);
    }


    protected void setHeight(int height) {
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = height;
        mView.setLayoutParams(layoutParams);
    }

    protected String parseString(String value) {
        return Strings.parse(mView, value);
    }

    protected static <T1, T2> Setter<T2> bind(Functions.VoidFunc2<T1, T2> func2, T1 t1) {
        return value -> func2.call(t1, value);
    }

}
