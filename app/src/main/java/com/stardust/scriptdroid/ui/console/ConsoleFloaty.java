package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableExpandableFloaty;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.scriptdroid.R;
import com.stardust.util.ViewUtil;

/**
 * Created by Stardust on 2017/4/20.
 */

public class ConsoleFloaty extends ResizableExpandableFloaty.AbstractResizableExpandableFloaty {

    private ContextWrapper mContextWrapper;
    private View mResizer, mMoveCursor;
    private TextView mTitleView;
    private StardustConsole mConsole;
    private CharSequence mTitle;

    public ConsoleFloaty(StardustConsole console) {
        mConsole = console;
        setShouldRequestFocusWhenExpand(false);
        setInitialX(100);
        setInitialY(1000);
    }

    @Override
    public View inflateCollapsedView(FloatyService service, final ResizableExpandableFloatyWindow window) {
        ensureContextWrapper(service);
        View view = View.inflate(mContextWrapper, R.layout.floating_window_collapse, null);
        view.post(new Runnable() {
            @Override
            public void run() {
                window.expand();
            }
        });
        return view;
    }

    private void ensureContextWrapper(Context context) {
        if (mContextWrapper == null) {
            mContextWrapper = new ContextThemeWrapper(context, R.style.AppTheme);
        }
    }

    @Override
    public View inflateExpandedView(FloatyService service, ResizableExpandableFloatyWindow window) {
        ensureContextWrapper(service);
        View view = View.inflate(mContextWrapper, R.layout.floating_console_expand, null);
        setListeners(view, window);
        setUpConsole(view, window);
        setInitialMeasure(view);
        return view;
    }

    private void setInitialMeasure(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.setViewMeasure(view, 800, 800);
            }
        });
    }

    private void initConsoleTitle(View view) {
        mTitleView = (TextView) view.findViewById(R.id.title);
        if (mTitle != null) {
            mTitleView.setText(mTitle);
        }
    }

    private void setListeners(final View view, final ResizableExpandableFloatyWindow window) {
        setWindowOperationIconListeners(view, window);
    }

    private void setUpConsole(View view, ResizableExpandableFloatyWindow window) {
        ConsoleView consoleView = (ConsoleView) view.findViewById(R.id.console);
        consoleView.setConsole(mConsole);
        consoleView.setWindow(window);
        initConsoleTitle(view);
    }

    private void setWindowOperationIconListeners(View view, final ResizableExpandableFloatyWindow window) {
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.close();
            }
        });
        view.findViewById(R.id.move_or_resize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoveCursor.getVisibility() == View.VISIBLE) {
                    mMoveCursor.setVisibility(View.GONE);
                    mResizer.setVisibility(View.GONE);
                } else {
                    mMoveCursor.setVisibility(View.VISIBLE);
                    mResizer.setVisibility(View.VISIBLE);
                }
            }
        });
        view.findViewById(R.id.minimize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.collapse();
            }
        });
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
            mTitleView.post(new Runnable() {
                @Override
                public void run() {
                    mTitleView.setText(title);
                }
            });
        }
    }
}
