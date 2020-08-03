package org.autojs.autojs.theme.widget;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorMutable;

/**
 * Created by Stardust on 2018/1/23.
 */

public class ThemeColorSwipeRefreshLayout extends SwipeRefreshLayout implements ThemeColorMutable {
    public ThemeColorSwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        ThemeColorManager.add(this);
    }

    public ThemeColorSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void setThemeColor(ThemeColor themeColor) {
        setColorSchemeColors(themeColor.colorPrimary);
    }
}
