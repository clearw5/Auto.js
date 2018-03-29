package com.stardust.autojs.core.ui.inflater.inflaters;

import android.animation.LayoutTransition;
import android.os.Build;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/4.
 */

public class ViewGroupInflater<V extends ViewGroup> extends BaseViewInflater<V> {

    private static final ValueMapper<Integer> PERSISTENT_DRAWING_CACHE = new ValueMapper<Integer>("persistentDrawingCache")
            .map("all", ViewGroup.PERSISTENT_ALL_CACHES)
            .map("animation", ViewGroup.PERSISTENT_ANIMATION_CACHE)
            .map("none", 0)
            .map("scrolling", ViewGroup.PERSISTENT_SCROLLING_CACHE);

    private static final ValueMapper<Integer> LAYOUT_MODES = new ValueMapper<Integer>("layoutMode")
            .map("clipBounds", 0) //ViewGroup.LAYOUT_MODE_CLIP_BOUNDS)
            .map("opticalBounds", 1); //ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);

    private static final ValueMapper<Integer> DESCENDANT_FOCUSABILITY = new ValueMapper<Integer>("descendantFocusability")
            .map("afterDescendants", ViewGroup.FOCUS_AFTER_DESCENDANTS)
            .map("beforeDescendants", ViewGroup.FOCUS_BEFORE_DESCENDANTS)
            .map("blocksDescendants", ViewGroup.FOCUS_BLOCK_DESCENDANTS);

    public ViewGroupInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }


    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "addStatesFromChildren":
                view.setAddStatesFromChildren(Boolean.valueOf(value));
                break;
            case "alwaysDrawnWithCache":
                view.setAlwaysDrawnWithCacheEnabled(Boolean.valueOf(value));
                break;
            case "animateLayoutChanges":
                view.setLayoutTransition(new LayoutTransition());
                break;
            case "animationCache":
                view.setAnimationCacheEnabled(Boolean.valueOf(value));
                break;
            case "clipChildren":
                view.setClipChildren(Boolean.valueOf(value));
                break;
            case "clipToPadding":
                view.setClipToPadding(Boolean.valueOf(value));
                break;
            case "descendantFocusability":
                view.setDescendantFocusability(DESCENDANT_FOCUSABILITY.get(value));
                break;
            case "layoutAnimation":
                Exceptions.unsupports(view, attr, value);
                break;
            case "layoutMode":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    view.setLayoutMode(LAYOUT_MODES.get(value));
                }
                break;
            case "persistentDrawingCache":
                view.setPersistentDrawingCache(PERSISTENT_DRAWING_CACHE.get(value));
                break;
            case "splitMotionEvents":
                view.setMotionEventSplittingEnabled(Boolean.valueOf(value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    public void applyPendingAttributesOfChildren(V view) {

    }
}
