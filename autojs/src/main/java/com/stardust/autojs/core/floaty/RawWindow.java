package com.stardust.autojs.core.floaty;

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.inflaters.Exceptions;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileBox;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;
import com.stardust.enhancedfloaty.WindowBridge;

public class RawWindow implements FloatyWindow {


    public interface RawFloaty {

        View inflateWindowView(FloatyService service, ViewGroup parent);
    }

    private WindowBridge mWindowBridge;
    private VolatileDispose<RuntimeException> mInflateException = new VolatileDispose<>();
    private WindowManager mWindowManager;
    private ViewGroup mWindowView;
    private View mWindowContent;
    private RawFloaty mRawFloaty;
    private WindowManager.LayoutParams mWindowLayoutParams;

    public RawWindow(RawFloaty rawFloaty) {
        mRawFloaty = rawFloaty;
    }

    @Override
    public void onCreate(FloatyService floatyService, WindowManager windowManager) {
        mWindowManager = windowManager;
        mWindowView = (ViewGroup) View.inflate(floatyService, R.layout.raw_window, null);
        mWindowLayoutParams = createWindowLayoutParams();
        try {
            mWindowContent = mRawFloaty.inflateWindowView(floatyService, mWindowView);
            mWindowManager.addView(mWindowView, mWindowLayoutParams);
        } catch (RuntimeException e) {
            mInflateException.setAndNotify(e);
            return;
        }
        mWindowBridge = new WindowBridge.DefaultImpl(mWindowLayoutParams, windowManager, mWindowView);
        mInflateException.setAndNotify(Exceptions.NO_EXCEPTION);
    }

    public RuntimeException waitForCreation() {
        return mInflateException.blockedGetOrThrow(ScriptInterruptedException.class);
    }


    protected WindowManager.LayoutParams createWindowLayoutParams() {
        int flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                flags,
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

    public void setTouchable(boolean touchable) {
        if (touchable) {
            mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        } else {
            mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    public WindowBridge getWindowBridge() {
        return mWindowBridge;
    }

    public ViewGroup getWindowView() {
        return mWindowView;
    }

    public View getWindowContent() {
        return mWindowContent;
    }

    @Override
    public void onServiceDestroy(FloatyService floatyService) {
        close();
    }

    @Override
    public void close() {
        if (mWindowView != null)
            mWindowManager.removeView(mWindowView);
        FloatyService.removeWindow(this);
    }
}
