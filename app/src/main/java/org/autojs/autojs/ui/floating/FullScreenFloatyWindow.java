package org.autojs.autojs.ui.floating;

import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

/**
 * Created by Stardust on 2017/10/18.
 */

public abstract class FullScreenFloatyWindow extends FloatyWindow {

    @Override
    protected WindowManager.LayoutParams onCreateWindowLayoutParams() {
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                FloatyWindowManger.getWindowType(),
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
    }

}
