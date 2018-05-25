package org.autojs.autojs.ui.floating;

import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

/**
 * Created by Stardust on 2017/10/18.
 */

public abstract class FullScreenFloatyWindow implements FloatyWindow {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;

    @Override
    public void onCreate(FloatyService floatyService, WindowManager windowManager) {
        mWindowManager = windowManager;
        mView = inflateView(floatyService);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mView, mLayoutParams);
    }

    protected abstract View inflateView(FloatyService service);

    @Override
    public void onServiceDestroy(FloatyService floatyService) {
        close();
    }

    @Override
    public void close() {
        mWindowManager.removeView(mView);
        FloatyService.removeWindow(this);
    }
}
