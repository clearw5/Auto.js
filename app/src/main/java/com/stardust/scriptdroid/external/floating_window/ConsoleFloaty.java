package com.stardust.scriptdroid.external.floating_window;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyService;

import com.stardust.scriptdroid.R;
/**
 * Created by Stardust on 2017/4/20.
 */

public class ConsoleFloaty extends ResizableFloaty {

    private ContextWrapper mContextWrapper;
    private View mResizer, mMoveCursor;

    @Override
    public View inflateCollapsedView(ResizableFloatyService service) {
        ensureContextWrapper(service);
        return View.inflate(mContextWrapper, R.layout.floating_window_collapse, null);
    }

    private void ensureContextWrapper(Context context) {
        if (mContextWrapper == null) {
            mContextWrapper = new ContextThemeWrapper(context, R.style.AppTheme);
        }
    }

    @Override
    public View inflateExpandedView(ResizableFloatyService service) {
        ensureContextWrapper(service);
        View view = View.inflate(mContextWrapper, R.layout.floating_console_expand, null);
        setListeners(view, service);
        return view;
    }

    private void setListeners(final View view, final ResizableFloatyService service) {
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.stopSelf();
                service.stopForeground(true);
            }
        });
        view.findViewById(R.id.move_or_resize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMoveCursor.getVisibility() == View.VISIBLE){
                    mMoveCursor.setVisibility(View.GONE);
                    mResizer.setVisibility(View.GONE);
                }else {
                    mMoveCursor.setVisibility(View.VISIBLE);
                    mResizer.setVisibility(View.VISIBLE);
                }
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
}
