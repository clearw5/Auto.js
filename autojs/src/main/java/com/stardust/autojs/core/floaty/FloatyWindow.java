package com.stardust.autojs.core.floaty;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.stardust.autojs.R;
import com.stardust.autojs.runtime.api.Floaty;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;

/**
 * Created by Stardust on 2017/12/5.
 */

public class FloatyWindow extends ResizableFloatyWindow {

    private static final Object LOCK = new Object();
    private View mView;
    private boolean mCreated = false;
    private View mMoveCursor;
    private View mResizer;
    private View mCloseButton;
    private MyFloaty mFloaty;


    public FloatyWindow(View view) {
        this(new MyFloaty(view));
        mView = view;
    }

    private FloatyWindow(MyFloaty floaty) {
        super(floaty);
        mFloaty = floaty;
    }

    public void waitFor() {
        synchronized (LOCK) {
            if (mCreated) {
                return;
            }
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onCreate(FloatyService service, WindowManager manager) {
        super.onCreate(service, manager);
        synchronized (LOCK) {
            mCreated = true;
            LOCK.notify();
        }
        View root = (View) mView.getParent().getParent();
        mMoveCursor = root.findViewById(R.id.move_cursor);
        mResizer = root.findViewById(R.id.resizer);
        mCloseButton = root.findViewById(R.id.close);
    }

    public void setOnCloseButtonClickListener(View.OnClickListener listener){
        mCloseButton.setOnClickListener(listener);
    }

    public void setAdjustEnabled(boolean enabled) {
        if (!enabled) {
            mMoveCursor.setVisibility(View.GONE);
            mResizer.setVisibility(View.GONE);
            mCloseButton.setVisibility(View.GONE);
        } else {
            mMoveCursor.setVisibility(View.VISIBLE);
            mResizer.setVisibility(View.VISIBLE);
            mCloseButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean isAdjustEnabled() {
        return mMoveCursor.getVisibility() == View.VISIBLE;
    }

    public View getRootView() {
        return mFloaty.mRootView;
    }

    private static class MyFloaty implements ResizableFloaty {


        private View mContentView;
        private View mRootView;


        public MyFloaty(View view) {
            mContentView = view;
        }

        @Override
        public View inflateView(FloatyService floatyService, ResizableFloatyWindow resizableFloatyWindow) {
            mRootView = View.inflate(mContentView.getContext(), R.layout.floaty_window, null);
            ((FrameLayout) mRootView.findViewById(R.id.container)).addView(mContentView);
            return mRootView;
        }

        @Nullable
        @Override
        public View getResizerView(View view) {
            return view.findViewById(R.id.resizer);
        }

        @Nullable
        @Override
        public View getMoveCursorView(View view) {
            return view.findViewById(R.id.move_cursor);
        }
    }
}
