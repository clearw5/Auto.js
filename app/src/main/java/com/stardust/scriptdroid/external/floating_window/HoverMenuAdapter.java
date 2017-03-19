package com.stardust.scriptdroid.external.floating_window;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floating_window.content.MainMenuNavigatorContent;
import com.stardust.scriptdroid.external.floating_window.content.RecordNavigatorContent;
import com.stardust.scriptdroid.external.floating_window.content.ScritpListNavigatorContent;
import com.stardust.theme.ThemeColorManagerCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/11.
 */

public class HoverMenuAdapter implements io.mattcarroll.hover.HoverMenuAdapter {


    public static final String ID_MAIN = "main";
    public static final String ID_RECORD = "record";
    public static final String ID_SCRIPT_LIST = "script_list";

    private final Context mContext;
    private final List<String> mTabIds;
    private final Map<String, NavigatorContent> mData = new LinkedHashMap<>();
    private final Set<ContentChangeListener> mContentChangeListeners = new HashSet<>();
    private View[] mViews;

    public HoverMenuAdapter(@NonNull Context context) {
        mContext = context;

        mData.put(HoverMenuAdapter.ID_MAIN, new MainMenuNavigatorContent(context));
        mData.put(HoverMenuAdapter.ID_RECORD, new RecordNavigatorContent(context));
        mData.put(HoverMenuAdapter.ID_SCRIPT_LIST, new ScritpListNavigatorContent(context));

        mTabIds = new ArrayList<>();
        for (String tabId : mData.keySet()) {
            mTabIds.add(tabId);
        }
        mViews = new View[mTabIds.size()];
    }

    @Override
    public int getTabCount() {
        return mTabIds.size();
    }

    @Override
    public View getTabView(int index) {
        mViews[index] = getTabViewInner(index);
        return mViews[index];
    }

    private View getTabViewInner(int index) {
        String menuItemId = mTabIds.get(index);
        if (ID_MAIN.equals(menuItemId)) {
            return createTabView(R.drawable.ic_android_eat_js_100);
        } else if (ID_RECORD.equals(menuItemId)) {
            return createTabView(R.drawable.ic_video_record);
        } else if (ID_SCRIPT_LIST.equals(menuItemId)) {
            return createTabView(R.drawable.ic_menu);
        } else {
            throw new RuntimeException("Unknown tab selected: " + index);
        }
    }

    @Override
    public long getTabId(int position) {
        return position;
    }

    @Override
    public NavigatorContent getNavigatorContent(int index) {
        String tabId = mTabIds.get(index);
        return mData.get(tabId);
    }

    @Override
    public void addContentChangeListener(@NonNull ContentChangeListener listener) {
        mContentChangeListeners.add(listener);
    }

    @Override
    public void removeContentChangeListener(@NonNull ContentChangeListener listener) {
        mContentChangeListeners.remove(listener);
    }

    public void selectTab(String id) {
        int i = mTabIds.indexOf(id);
        mViews[i].performClick();
    }

    protected void notifyDataSetChanged() {
        for (ContentChangeListener listener : mContentChangeListeners) {
            listener.onContentChange(this);
        }
    }

    private View createTabView(@DrawableRes int tabBitmapRes) {
        return createTabView(tabBitmapRes, ThemeColorManagerCompat.getColorPrimary(), Color.WHITE);
    }

    private View createTabView(@DrawableRes int tabBitmapRes, @ColorInt int backgroundColor, @ColorInt Integer iconColor) {
        Resources resources = mContext.getResources();
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, resources.getDisplayMetrics());

        DemoTabView view = new DemoTabView(mContext, resources.getDrawable(R.drawable.tab_background), resources.getDrawable(tabBitmapRes));
        view.setTabBackgroundColor(backgroundColor);
        view.setTabForegroundColor(iconColor);
        view.setPadding(padding, padding, padding, padding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(padding);
        }
        return view;
    }

    public class DemoTabView extends View {

        private int mBackgroundColor;
        private Integer mForegroundColor;

        private Drawable mCircleDrawable;
        private Drawable mIconDrawable;
        private int mIconInsetLeft, mIconInsetTop, mIconInsetRight, mIconInsetBottom;

        public DemoTabView(Context context, Drawable backgroundDrawable, Drawable iconDrawable) {
            super(context);
            mCircleDrawable = backgroundDrawable;
            mIconDrawable = iconDrawable;
            init();
        }

        private void init() {
            int insetsDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics());
            mIconInsetLeft = mIconInsetTop = mIconInsetRight = mIconInsetBottom = insetsDp;
        }

        public void setTabBackgroundColor(@ColorInt int backgroundColor) {
            mBackgroundColor = backgroundColor;
            mCircleDrawable.setColorFilter(mBackgroundColor, PorterDuff.Mode.SRC_ATOP);
        }

        public void setTabForegroundColor(@ColorInt Integer foregroundColor) {
            mForegroundColor = foregroundColor;
            if (null != mForegroundColor) {
                mIconDrawable.setColorFilter(mForegroundColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                mIconDrawable.setColorFilter(null);
            }
        }

        public void setIcon(@Nullable Drawable icon) {
            mIconDrawable = icon;
            if (null != mForegroundColor && null != mIconDrawable) {
                mIconDrawable.setColorFilter(mForegroundColor, PorterDuff.Mode.SRC_ATOP);
            }
            updateIconBounds();

            invalidate();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            // Make circle as large as View minus padding.
            mCircleDrawable.setBounds(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());

            // Re-size the icon as necessary.
            updateIconBounds();

            invalidate();
        }

        private void updateIconBounds() {
            if (null != mIconDrawable) {
                Rect bounds = new Rect(mCircleDrawable.getBounds());
                bounds.set(bounds.left + mIconInsetLeft, bounds.top + mIconInsetTop, bounds.right - mIconInsetRight, bounds.bottom - mIconInsetBottom);
                mIconDrawable.setBounds(bounds);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mCircleDrawable.draw(canvas);
            if (null != mIconDrawable) {
                mIconDrawable.draw(canvas);
            }
        }
    }
}
