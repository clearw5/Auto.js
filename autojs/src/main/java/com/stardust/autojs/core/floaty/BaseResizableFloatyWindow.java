package com.stardust.autojs.core.floaty;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.inflaters.Exceptions;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileBox;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.WindowBridge;
import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;

/**
 * Created by Stardust on 2017/12/5.
 */

public class BaseResizableFloatyWindow extends ResizableFloatyWindow {

    public interface ViewSupplier {

        View inflate(Context context, ViewGroup parent);

    }

    private VolatileDispose<RuntimeException> mInflateException = new VolatileDispose<>();
    private View mCloseButton;
    private static final String TAG = "ResizableFloatyWindow";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private ViewGroup mWindowView;
    private View mRootView;
    private View mResizer;
    private View mMoveCursor;
    private WindowBridge mWindowBridge;
    private MyFloaty mFloaty;


    public BaseResizableFloatyWindow(Context context, ViewSupplier viewSupplier) {
        this(new MyFloaty(context, viewSupplier));
    }

    private BaseResizableFloatyWindow(MyFloaty floaty) {
        super(floaty);
        mFloaty = floaty;
    }

    public RuntimeException waitForCreation() {
        return mInflateException.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @Override
    public void onCreate(FloatyService service, WindowManager manager) {
        this.mWindowManager = manager;
        this.mWindowLayoutParams = this.createWindowLayoutParams();
        if (this.mFloaty == null) {
            throw new IllegalStateException("Must start this service by static method ResizableExpandableFloatyWindow.startService");
        } else {
            try {
                this.initWindowView(service);
            } catch (RuntimeException e) {
                mInflateException.setAndNotify(e);
                return;
            }
            this.mWindowBridge = new WindowBridge.DefaultImpl(this.mWindowLayoutParams, this.mWindowManager, this.mWindowView);
            this.initGesture();
        }
        mInflateException.setAndNotify(Exceptions.NO_EXCEPTION);
    }

    public void setOnCloseButtonClickListener(View.OnClickListener listener) {
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
        return mRootView;
    }

    private void initWindowView(FloatyService service) {
        this.mWindowView = (ViewGroup) View.inflate(service, com.stardust.lib.R.layout.ef_floaty_container, (ViewGroup) null);
        this.mRootView = this.mFloaty.inflateView(service, this);
        this.mResizer = this.mFloaty.getResizerView(this.mRootView);
        this.mMoveCursor = this.mFloaty.getMoveCursorView(this.mRootView);
        this.mCloseButton = mRootView.findViewById(R.id.close);
        android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(-2, -2);
        this.mWindowView.addView(this.mRootView, params);
        this.mWindowView.setFocusableInTouchMode(true);
        this.mWindowManager.addView(this.mWindowView, this.mWindowLayoutParams);
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        return layoutParams;
    }

    public void disableWindowFocus() {
        mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    public void requestWindowFocus() {
        mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        mWindowView.requestFocus();
    }


    private void initGesture() {
        if (this.mResizer != null) {
            ResizeGesture.enableResize(this.mResizer, this.mRootView, this.mWindowBridge);
        }

        if (this.mMoveCursor != null) {
            DragGesture gesture = new DragGesture(this.mWindowBridge, this.mMoveCursor);
            gesture.setPressedAlpha(1.0F);
        }
    }

    public WindowBridge getWindowBridge() {
        return this.mWindowBridge;
    }

    public void onServiceDestroy(FloatyService service) {
        this.close();
    }

    public void close() {
        if (mWindowView != null)
            this.mWindowManager.removeView(this.mWindowView);
        FloatyService.removeWindow(this);
    }

    private static class MyFloaty implements ResizableFloaty {


        private ViewSupplier mContentViewSupplier;
        private View mRootView;
        private Context mContext;

        public MyFloaty(Context context, ViewSupplier supplier) {
            mContentViewSupplier = supplier;
            mContext = context;
        }

        @Override
        public View inflateView(FloatyService floatyService, ResizableFloatyWindow resizableFloatyWindow) {
            mRootView = View.inflate(mContext, R.layout.floaty_window, null);
            FrameLayout container = (FrameLayout) mRootView.findViewById(R.id.container);
            View contentView = mContentViewSupplier.inflate(mContext, container);
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
