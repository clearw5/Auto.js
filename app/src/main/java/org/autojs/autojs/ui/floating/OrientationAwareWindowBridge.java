package org.autojs.autojs.ui.floating;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.WindowBridge;

public class OrientationAwareWindowBridge extends WindowBridge.DefaultImpl {


    private Context mContext;
    private int mOrientation;

    public OrientationAwareWindowBridge(WindowManager.LayoutParams windowLayoutParams, WindowManager windowManager, View windowView, Context context) {
        super(windowLayoutParams, windowManager, windowView);
        mContext = context;
        mOrientation = mContext.getResources().getConfiguration().orientation;
    }

    public boolean isOrientationChanged(int newOrientation) {
        if (mOrientation != newOrientation) {
            mOrientation = newOrientation;
            return true;
        }
        return false;
    }

    @Override
    public int getScreenHeight() {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return super.getScreenWidth();
        } else {
            return super.getScreenHeight();
        }
    }

    @Override
    public int getScreenWidth() {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return super.getScreenHeight();
        } else {
            return super.getScreenWidth();
        }
    }
}
