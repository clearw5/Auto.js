package com.stardust.autojs.core.ui.inflater.inflaters;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.stardust.autojs.core.ui.ViewExtras;
import com.stardust.autojs.core.ui.attribute.ViewAttributes;
import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewInflater;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.Ids;
import com.stardust.autojs.core.ui.inflater.util.Strings;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;

import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class BaseViewInflater<V extends View> implements ViewInflater<V> {

    private static final String LOG_TAG = "BaseViewInflater";

    public static final ValueMapper<PorterDuff.Mode> TINT_MODES = new ValueMapper<PorterDuff.Mode>("tintMode")
            .map("add", PorterDuff.Mode.ADD)
            .map("multiply", PorterDuff.Mode.MULTIPLY)
            .map("screen", PorterDuff.Mode.SCREEN)
            .map("src_atop", PorterDuff.Mode.SRC_ATOP)
            .map("src_in", PorterDuff.Mode.SRC_IN)
            .map("src_over", PorterDuff.Mode.SRC_OVER);

    public static final ValueMapper<Integer> DRAWABLE_CACHE_QUALITIES = new ValueMapper<Integer>("drawingCacheQuality")
            .map("auto", View.DRAWING_CACHE_QUALITY_AUTO)
            .map("high", View.DRAWING_CACHE_QUALITY_HIGH)
            .map("low", View.DRAWING_CACHE_QUALITY_LOW);

    public static final ValueMapper<Integer> IMPORTANT_FOR_ACCESSIBILITY = new ValueMapper<Integer>("importantForAccessibility")
            .map("auto", 0) //View.IMPORTANT_FOR_ACCESSIBILITY_AUTO)
            .map("no", 2) //View.IMPORTANT_FOR_ACCESSIBILITY_NO)
            .map("noHideDescendants", 4) //View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
            .map("yes", 1); //View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    public static final ValueMapper<Integer> LAYOUT_DIRECTIONS = new ValueMapper<Integer>("layoutDirection")
            .map("inherit", 2)
            .map("locale", 3)
            .map("ltr", 0)
            .map("rtl", 1);
    public static final ValueMapper<Integer> SCROLLBARS_STYLES = new ValueMapper<Integer>("scrollbarStyle")
            .map("insideInset", View.SCROLLBARS_INSIDE_INSET)
            .map("insideOverlay", View.SCROLLBARS_INSIDE_OVERLAY)
            .map("outsideInset", View.SCROLLBARS_OUTSIDE_INSET)
            .map("outsideOverlay", View.SCROLLBARS_OUTSIDE_OVERLAY);
    public static final ValueMapper<Integer> SCROLL_INDICATORS = new ValueMapper<Integer>("scrollIndicators")
            .map("bottom", 2) //View.SCROLL_INDICATOR_BOTTOM)
            .map("end", 20) //View.SCROLL_INDICATOR_END)
            .map("left", 4) //View.SCROLL_INDICATOR_LEFT)
            .map("none", 0)
            .map("right", 8) //View.SCROLL_INDICATOR_RIGHT)
            .map("start", 10) //View.SCROLL_INDICATOR_START)
            .map("top", 1); //View.SCROLL_INDICATOR_TOP)
    public static final ValueMapper<Integer> VISIBILITY = new ValueMapper<Integer>("visibility")
            .map("visible", View.VISIBLE)
            .map("invisible", View.INVISIBLE)
            .map("gone", View.GONE);
    public static final ValueMapper<Integer> TEXT_DIRECTIONS = new ValueMapper<Integer>("textDirection")
            .map("anyRtl", 2)
            .map("firstStrong", 1)
            .map("firstStrongLtr", 6)
            .map("firstStrongRtl", 7)
            .map("inherit", 0)
            .map("locale", 5)
            .map("ltr", 3)
            .map("rtl", 4);
    public static final ValueMapper<Integer> TEXT_ALIGNMENTS = new ValueMapper<Integer>("textAlignment")
            .map("center", 4)
            .map("gravity", 1)
            .map("inherit", 0)
            .map("textEnd", 3)
            .map("textStart", 2)
            .map("viewEnd", 6)
            .map("viewStart", 5);

    private final ResourceParser mResourceParser;

    public BaseViewInflater(ResourceParser resourceParser) {
        mResourceParser = resourceParser;
    }

    public Drawables getDrawables() {
        return mResourceParser.getDrawables();
    }

    public ResourceParser getResourceParser() {
        return mResourceParser;
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        ViewAttributes viewAttributes = ViewExtras.getViewAttributes(view, getResourceParser());
        ViewAttributes.Attribute attribute = viewAttributes.get(attr);
        if (attribute != null) {
            Log.d(LOG_TAG, "setAttr use ViewAttributes: attr = " + attr);
            attribute.set(value);
            return true;
        }
        Log.d(LOG_TAG, "setAttr cannot use ViewAttributes: attr = " + attr);
        Integer layoutRule = null;
        boolean layoutTarget = false;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        switch (attr) {
            case "id":
                view.setId(Ids.parse(value));
                break;
            case "gravity":
                return setGravity(view, value);
            case "width":
            case "layout_width":
                switch (value) {
                    case "wrap_content":
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case "fill_parent":
                    case "match_parent":
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        break;
                    default:
                        layoutParams.width = Dimensions.parseToPixel(value, view, parent, true);
                        break;
                }
                break;
            case "height":
            case "layout_height":
                switch (value) {
                    case "wrap_content":
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case "fill_parent":
                    case "match_parent":
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        break;
                    default:
                        layoutParams.height = Dimensions.parseToPixel(value, view, parent, false);
                        break;
                }
                break;
            case "layout_gravity":
                if (parent instanceof LinearLayout) {
                    ((LinearLayout.LayoutParams) layoutParams).gravity = Gravities.parse(value);
                } else if (parent instanceof FrameLayout) {
                    ((FrameLayout.LayoutParams) layoutParams).gravity = Gravities.parse(value);
                } else {
                    return setLayoutGravity(parent, view, Gravities.parse(value));
                }
                break;
            case "layout_weight":
                if (parent instanceof LinearLayout) {
                    ((LinearLayout.LayoutParams) layoutParams).weight = Float.parseFloat(value);
                }
                break;
            case "layout_below":
                layoutRule = RelativeLayout.BELOW;
                layoutTarget = true;
                break;
            case "layout_above":
                layoutRule = RelativeLayout.ABOVE;
                layoutTarget = true;
                break;
            case "layout_toLeftOf":
                layoutRule = RelativeLayout.LEFT_OF;
                layoutTarget = true;
                break;
            case "layout_toRightOf":
                layoutRule = RelativeLayout.RIGHT_OF;
                layoutTarget = true;
                break;
            case "layout_alignBottom":
                layoutRule = RelativeLayout.ALIGN_BOTTOM;
                layoutTarget = true;
                break;
            case "layout_alignTop":
                layoutRule = RelativeLayout.ALIGN_TOP;
                layoutTarget = true;
                break;
            case "layout_alignLeft":
            case "layout_alignStart":
                layoutRule = RelativeLayout.ALIGN_LEFT;
                layoutTarget = true;
                break;
            case "layout_alignRight":
            case "layout_alignEnd":
                layoutRule = RelativeLayout.ALIGN_RIGHT;
                layoutTarget = true;
                break;
            case "layout_alignParentBottom":
                layoutRule = RelativeLayout.ALIGN_PARENT_BOTTOM;
                break;
            case "layout_alignParentTop":
                layoutRule = RelativeLayout.ALIGN_PARENT_TOP;
                break;
            case "layout_alignParentLeft":
            case "layout_alignParentStart":
                layoutRule = RelativeLayout.ALIGN_PARENT_LEFT;
                break;
            case "layout_alignParentRight":
            case "layout_alignParentEnd":
                layoutRule = RelativeLayout.ALIGN_PARENT_RIGHT;
                break;
            case "layout_centerHorizontal":
                layoutRule = RelativeLayout.CENTER_HORIZONTAL;
                break;
            case "layout_centerVertical":
                layoutRule = RelativeLayout.CENTER_VERTICAL;
                break;
            case "layout_centerInParent":
                layoutRule = RelativeLayout.CENTER_IN_PARENT;
                break;
            case "layout_margin":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    int margin = Dimensions.parseToIntPixel(value, view);
                    params.bottomMargin = params.leftMargin = params.topMargin = params.rightMargin = margin;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(margin);
                        params.setMarginEnd(margin);
                    }
                }
                break;
            case "layout_marginLeft":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.leftMargin = Dimensions.parseToIntPixel(value, view);
                }
                break;
            case "layout_marginTop":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.topMargin = Dimensions.parseToIntPixel(value, view);
                }
                break;
            case "layout_marginRight":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.rightMargin = Dimensions.parseToIntPixel(value, view);
                }
                break;
            case "layout_marginBottom":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.bottomMargin = Dimensions.parseToIntPixel(value, view);
                }
                break;
            case "padding":
                int p = Dimensions.parseToIntPixel(value, view);
                view.setPadding(p, p, p, p);
                break;
            case "paddingLeft":
                view.setPadding(Dimensions.parseToIntPixel(value, view), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                break;
            case "paddingTop":
                view.setPadding(view.getPaddingLeft(), Dimensions.parseToIntPixel(value, view), view.getPaddingRight(), view.getPaddingBottom());
                break;
            case "paddingRight":
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), Dimensions.parseToIntPixel(value, view), view.getPaddingBottom());
                break;
            case "paddingBottom":
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), Dimensions.parseToIntPixel(value, view));
                break;
            case "bg":
            case "background":
                getDrawables().setupWithViewBackground(view, value);
                break;
            case "accessibilityLiveRegion":
            case "accessibilityTraversalAfter":
            case "accessibilityTraversalBefore":
                Exceptions.unsupports(view, attr, value);
            case "alpha":
                view.setAlpha(Float.valueOf(value));
                break;
            case "autofillHints":
            case "autofilledHighlight":
                Exceptions.unsupports(view, attr, value);
                break;
            case "backgroundTint":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setBackgroundTintList(ColorStateList.valueOf(Colors.parse(view, value)));
                }
                break;
            case "backgroundTintMode":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setBackgroundTintMode(TINT_MODES.get(value));
                }
                break;
            case "checked":
                if (view instanceof CompoundButton) {
                    ((CompoundButton) view).setChecked(Boolean.parseBoolean(value));
                }
                break;
            case "clickable":
                view.setClickable(Boolean.valueOf(value));
                break;
            case "contentDescription":
                view.setContentDescription(Strings.parse(view, value));
                break;
            case "contextClickable":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setContextClickable(Boolean.valueOf(value));
                }
                break;
            case "defaultFocusHighlightEnabled":
                Exceptions.unsupports(view, attr, value);
                break;
            case "drawingCacheQuality":
                view.setDrawingCacheQuality(DRAWABLE_CACHE_QUALITIES.get(value));
                break;
            case "duplicateParentState":
                view.setDuplicateParentStateEnabled(Boolean.valueOf(value));
                break;
            case "elevation":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(Dimensions.parseToIntPixel(value, view));
                }
                break;
            case "fadeScrollbars":
                view.setScrollbarFadingEnabled(Boolean.valueOf(value));
                break;
            case "fadingEdgeLength":
                view.setFadingEdgeLength(Dimensions.parseToIntPixel(value, view));
                break;
            case "filterTouchesWhenObscured":
                view.setFilterTouchesWhenObscured(Boolean.valueOf(value));
                break;
            case "fitsSystemWindows":
                view.setFitsSystemWindows(Boolean.valueOf(value));
                break;
            case "focusable":
                view.setFocusable(Boolean.valueOf(value));
                break;
            case "focusableInTouchMode":
                view.setFocusableInTouchMode(Boolean.valueOf(value));
                break;
            case "focusedByDefault":
                Exceptions.unsupports(view, attr, value);
                break;
            case "forceHasOverlappingRendering":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.forceHasOverlappingRendering(Boolean.valueOf(value));
                }
                break;
            case "foreground":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setForeground(getDrawables().parse(view, value));
                }
                break;
            case "foregroundGravity":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setForegroundGravity(Gravities.parse(value));
                }
                break;
            case "foregroundTint":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setForegroundTintList(ColorStateList.valueOf(Colors.parse(view, value)));
                }
                break;
            case "foregroundTintMode":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setForegroundTintMode(TINT_MODES.get(value));
                }
                break;
            case "hapticFeedbackEnabled":
                view.setHapticFeedbackEnabled(Boolean.valueOf(value));
                break;
            case "importantForAccessibility":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY.get(value));
                }
                break;
            case "importantForAutofill":
                Exceptions.unsupports(view, attr, value);
                break;
            case "isScrollContainer":
                view.setScrollContainer(Boolean.valueOf(value));
                break;
            case "keepScreenOn":
                view.setKeepScreenOn(Boolean.valueOf(value));
                break;
            case "keyboardNavigationCluster":
                Exceptions.unsupports(view, attr, value);
                break;
            case "layerType":
                Exceptions.unsupports(view, attr, value);
                break;
            case "layoutDirection":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setLayoutDirection(LAYOUT_DIRECTIONS.get(value));
                }
                break;
            case "longClickable":
                view.setLongClickable(Boolean.valueOf(value));
                break;
            case "minHeight":
                view.setMinimumHeight(Dimensions.parseToIntPixel(value, view));
                break;
            case "minWidth":
                view.setMinimumWidth(Dimensions.parseToIntPixel(value, view));
                break;
            case "nextClusterForward":
                Exceptions.unsupports(view, attr, value);
                break;
            case "nextFocusDown":
                Exceptions.unsupports(view, attr, value);
                break;
            case "nextFocusForward":
                Exceptions.unsupports(view, attr, value);
                break;
            case "nextFocusLeft":
                Exceptions.unsupports(view, attr, value);
                break;
            case "nextFocusRight":
                Exceptions.unsupports(view, attr, value);
                break;
            case "nextFocusUp":
                Exceptions.unsupports(view, attr, value);
                break;
            case "onClick":
                Exceptions.unsupports(view, attr, value);
                break;
            case "paddingEnd":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setPaddingRelative(view.getPaddingStart(), view.getPaddingTop(),
                            Dimensions.parseToIntPixel(value, view), view.getPaddingBottom());
                } else {
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                            Dimensions.parseToIntPixel(value, view), view.getPaddingBottom());
                }
                break;
            case "paddingHorizontal":
            case "paddingVertical":
                Exceptions.unsupports(view, attr, value);
                break;
            case "paddingStart":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setPaddingRelative(Dimensions.parseToIntPixel(value, view), view.getPaddingTop(),
                            view.getPaddingEnd(), view.getPaddingBottom());
                } else {
                    view.setPadding(Dimensions.parseToIntPixel(value, view), view.getPaddingTop(),
                            view.getPaddingRight(), view.getPaddingBottom());
                }
                break;
            case "requiresFadingEdge":
                for (String str : value.split("\\|")) {
                    if (str.equals("horizontal")) {
                        view.setHorizontalFadingEdgeEnabled(true);
                    } else if (str.equals("vertical")) {
                        view.setVerticalFadingEdgeEnabled(true);
                    }
                }
                break;
            case "rotation":
                view.setRotation(Float.parseFloat(value));
                break;
            case "rotationX":
                view.setRotationX(Float.parseFloat(value));
                break;
            case "rotationY":
                view.setRotationY(Float.parseFloat(value));
                break;
            case "saveEnabled":
                view.setSaveEnabled(Boolean.valueOf(value));
                break;
            case "scaleX":
                view.setScaleX(Float.parseFloat(value));
                break;
            case "scaleY":
                view.setScaleY(Float.parseFloat(value));
                break;
            case "scrollIndicators":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setScrollIndicators(SCROLL_INDICATORS.get(value));
                }
                break;
            case "scrollX":
                view.setScrollX(Dimensions.parseToIntPixel(value, view));
                break;
            case "scrollY":
                view.setScrollY(Dimensions.parseToIntPixel(value, view));
                break;
            case "scrollbarAlwaysDrawHorizontalTrack":
            case "scrollbarAlwaysDrawVerticalTrack":
                Exceptions.unsupports(view, attr, value);
                break;
            case "scrollbarDefaultDelayBeforeFade":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setScrollBarDefaultDelayBeforeFade(Integer.valueOf(value));
                }
                break;
            case "scrollbarFadeDuration":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setScrollBarFadeDuration(Integer.valueOf(value));
                }
                break;
            case "scrollbarSize":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setScrollBarSize(Dimensions.parseToIntPixel(value, view));
                }
                break;
            case "scrollbarStyle":
                view.setScrollBarStyle(SCROLLBARS_STYLES.get(value));
                break;
            case "scrollbarThumbHorizontal":
            case "scrollbarThumbVertical":
            case "scrollbarTrackHorizontal":
            case "scrollbarTrackVertical":
                Exceptions.unsupports(view, attr, value);
            case "scrollbars":
                for (String str : value.split("|")) {
                    if (str.equals("horizontal")) {
                        view.setHorizontalScrollBarEnabled(true);
                    } else if (str.equals("vertical")) {
                        view.setVerticalScrollBarEnabled(true);
                    }
                }
                break;
            case "soundEffectsEnabled":
                view.setSoundEffectsEnabled(Boolean.valueOf(value));
                break;
            case "stateListAnimator":
                Exceptions.unsupports(view, attr, value);
            case "tag":
                view.setTag(Strings.parse(view, value));
                break;
            case "textAlignment":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setTextAlignment(TEXT_ALIGNMENTS.get(value));
                }
                break;
            case "textDirection":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setTextDirection(TEXT_DIRECTIONS.get(value));
                }
                break;
            case "theme":
                //Exceptions.unsupports(view, attr, value);

                break;
            case "tooltipText":
                Exceptions.unsupports(view, attr, value);
                break;
            case "transformPivotX":
                view.setPivotX(Dimensions.parseToPixel(value, view));
                break;
            case "transformPivotY":
                view.setPivotY(Dimensions.parseToPixel(value, view));
                break;
            case "transitionName":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTransitionName(Strings.parse(view, value));
                }
                break;
            case "translationX":
                view.setTranslationX(Dimensions.parseToPixel(value, view));
                break;
            case "translationY":
                view.setTranslationY(Dimensions.parseToPixel(value, view));
                break;
            case "translationZ":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTranslationZ(Dimensions.parseToPixel(value, view));
                }
                break;
            case "visibility":
                view.setVisibility(VISIBILITY.get(value));
                break;
            default:
                return false;

        }
        if (layoutRule != null && parent instanceof RelativeLayout) {
            if (layoutTarget) {
                int anchor = Ids.parse(value);
                ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule, anchor);
            } else if (value.equals("true")) {
                ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule);
            }
        }
        return true;
    }

    public boolean setLayoutGravity(ViewGroup parent, V view, int gravity) {
        try {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            Field field = layoutParams.getClass().getField("gravity");
            field.set(layoutParams, gravity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setAttr(V view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        if (ns == null || ns.equals("android")) {
            return setAttr(view, attrName, value, parent, attrs);
        }
        return false;
    }

    private boolean setGravity(V view, String g) {
        try {
            Method setGravity = view.getClass().getMethod("setGravity", int.class);
            setGravity.invoke(view, Gravities.parse(g));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void applyPendingAttributes(V view, ViewGroup parent) {

    }

    @Override
    public boolean inflateChildren(DynamicLayoutInflater inflater, Node node, V parent) {
        return false;
    }

    @Nullable
    @Override
    public ViewCreator<? super V> getCreator() {
        return null;
    }
}
