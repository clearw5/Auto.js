package com.stardust.autojs.core.console;

import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableExpandableFloaty;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.ViewUtil;
import com.stardust.autojs.R;

/**
 * Created by Stardust on 2017/4/20.
 */

public class ConsoleFloaty extends ResizableExpandableFloaty.AbstractResizableExpandableFloaty {

    private ContextWrapper mContextWrapper;
    private View mResizer, mMoveCursor;
    private TextView mTitleView;
    private ConsoleImpl mConsole;
    private CharSequence mTitle;
    private View mExpandedView;

    public ConsoleFloaty(ConsoleImpl console) {
        mConsole = console;
        setShouldRequestFocusWhenExpand(false);
        setInitialX(100);
        setInitialY(1000);
        setCollapsedViewUnpressedAlpha(1.0f);
    }

    @Override
    public int getInitialWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT; //ScreenMetrics.getDeviceScreenWidth() * 2 / 3;
    }

    @Override
    public int getInitialHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;//ScreenMetrics.getDeviceScreenHeight() / 3;
    }

    @Override
    public View inflateCollapsedView(FloatyService service, final ResizableExpandableFloatyWindow window) {
        ensureContextWrapper(service);
        return View.inflate(mContextWrapper, R.layout.floating_window_collapse, null);
    }

    private void ensureContextWrapper(Context context) {
        if (mContextWrapper == null) {
            mContextWrapper = new ContextThemeWrapper(context, R.style.ConsoleTheme);
        }
    }

    @Override
    public View inflateExpandedView(FloatyService service, ResizableExpandableFloatyWindow window) {
        ensureContextWrapper(service);
        View view = View.inflate(mContextWrapper, R.layout.floating_console_expand, null);
        setListeners(view, window);
        setUpConsole(view, window);
        setInitialMeasure(view);
        mExpandedView = view;
        return view;
    }

    public View getExpandedView() {
        return mExpandedView;
    }

    private void setInitialMeasure(final View view) {
        view.post(() -> ViewUtil.setViewMeasure(view, ScreenMetrics.getDeviceScreenWidth() * 2 / 3,
                ScreenMetrics.getDeviceScreenHeight() / 3));
    }

    private void initConsoleTitle(View view) {
        mTitleView = view.findViewById(R.id.title);
        if (mTitle != null) {
            mTitleView.setText(mTitle);
        }
    }

    private void setListeners(final View view, final ResizableExpandableFloatyWindow window) {
        setWindowOperationIconListeners(view, window);
    }

    private void setUpConsole(View view, ResizableExpandableFloatyWindow window) {
        ConsoleView consoleView = view.findViewById(R.id.console);
        consoleView.setConsole(mConsole);
        consoleView.setWindow(window);
        initConsoleTitle(view);
    }

    private void setWindowOperationIconListeners(View view, final ResizableExpandableFloatyWindow window) {
        view.findViewById(R.id.close).setOnClickListener(v -> window.close());
        view.findViewById(R.id.move_or_resize).setOnClickListener(v -> {
            if (mMoveCursor.getVisibility() == View.VISIBLE) {
                mMoveCursor.setVisibility(View.GONE);
                mResizer.setVisibility(View.GONE);
            } else {
                mMoveCursor.setVisibility(View.VISIBLE);
                mResizer.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.minimize).setOnClickListener(v -> window.collapse());
    }

    @Nullable
    @Override
    public View getResizerView(View expandedView) {
        mResizer = expandedView.findViewById(R.id.resizer);
        return mResizer;
    }

    @Nullable
    @Override
    public View getMoveCursorView(View expandedView) {
        mMoveCursor = expandedView.findViewById(R.id.move_cursor);
        return mMoveCursor;
    }

    public void setTitle(final CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.post(() -> mTitleView.setText(title));
        }
    }
}
