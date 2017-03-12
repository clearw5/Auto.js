package com.stardust.hover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.WindowManager;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.HoverMenuAdapter;
import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.defaulthovermenu.view.ViewHoverMenu;

/**
 * Created by Stardust on 2017/3/11.
 */

public class HoverMenuBuilder {

    public static final int DISPLAY_MODE_WINDOW = 1; // Display directly in a window.
    public static final int DISPLAY_MODE_VIEW = 2; // Display within View hierarchy.

    private Context mContext;
    private int mDisplayMode = DISPLAY_MODE_WINDOW;
    private WindowManager mWindowManager;
    private Navigator mNavigator;
    private HoverMenuAdapter mAdapter;
    private String mSavedVisualState = null;

    public HoverMenuBuilder(@NonNull Context context) {
        mContext = context;
    }

    public HoverMenuBuilder displayWithinWindow() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMode = DISPLAY_MODE_WINDOW;
        return this;
    }

    public HoverMenuBuilder displayWithinView(@NonNull ViewGroup container) {
        mDisplayMode = DISPLAY_MODE_VIEW;
        return this;
    }

    public HoverMenuBuilder useNavigator(@Nullable Navigator navigator) {
        mNavigator = navigator;
        return this;
    }

    public HoverMenuBuilder useAdapter(@Nullable HoverMenuAdapter adapter) {
        mAdapter = adapter;
        return this;
    }

    public HoverMenuBuilder restoreVisualState(@NonNull String visualState) {
        mSavedVisualState = visualState;
        return this;
    }

    public HoverMenu build() {
        if (DISPLAY_MODE_WINDOW == mDisplayMode) {
            WindowHoverMenu windowHoverMenu = new WindowHoverMenu(mContext, mWindowManager, mNavigator, mSavedVisualState);
            windowHoverMenu.setAdapter(mAdapter);
            return windowHoverMenu;
        } else {
            ViewHoverMenu viewHoverMenu = new ViewHoverMenu(mContext);
            viewHoverMenu.setAdapter(mAdapter);
            return viewHoverMenu;
        }
    }
}
