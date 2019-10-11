package org.autojs.autojs.ui.floating;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;
import com.stardust.enhancedfloaty.WindowBridge;
import com.stardust.util.ScreenMetrics;

import org.autojs.autojs.ui.floating.gesture.BounceDragGesture;

public class CircularMenuWindow extends FloatyWindow {

    private static final String KEY_POSITION_X = CircularMenuWindow.class.getName() + ".position.x";
    private static final String KEY_POSITION_Y = CircularMenuWindow.class.getName() + ".position.y";

    protected CircularMenuFloaty mFloaty;
    protected CircularActionMenu mCircularActionMenu;
    protected View mCircularActionView;
    protected BounceDragGesture mDragGesture;
    protected OrientationAwareWindowBridge mActionViewWindowBridge;
    protected WindowBridge mMenuWindowBridge;
    protected WindowManager.LayoutParams mActionViewWindowLayoutParams;
    protected WindowManager.LayoutParams mMenuWindowLayoutParams;
    protected View.OnClickListener mActionViewOnClickListener;
    protected float mKeepToSideHiddenWidthRadio;
    protected float mActiveAlpha = 1.0F;
    protected float mInactiveAlpha = 0.4F;
    private Context mContext;
    private OrientationEventListener mOrientationEventListener;

    public CircularMenuWindow(Context context, CircularMenuFloaty floaty) {
        mFloaty = floaty;
        mContext = context;
    }

    @Override
    protected void onCreateWindow(FloatyService service, WindowManager manager) {
        mActionViewWindowLayoutParams = createWindowLayoutParams();
        mMenuWindowLayoutParams = createWindowLayoutParams();
        inflateWindowViews(service);
        initWindowBridge();
        initGestures();
        setListeners();
        setInitialState();
        mOrientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (mActionViewWindowBridge.isOrientationChanged(mContext.getResources().getConfiguration().orientation)) {
                    keepToSide();
                }
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    @Override
    protected View onCreateView(FloatyService floatyService) {
        return null;
    }

    @Override
    protected WindowManager.LayoutParams onCreateWindowLayoutParams() {
        return null;
    }

    private void keepToSide() {
        mDragGesture.keepToEdge();
    }

    private void setInitialState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int y = preferences.getInt(KEY_POSITION_Y, ScreenMetrics.getDeviceScreenHeight() / 2);
        mActionViewWindowBridge.updatePosition(mActionViewWindowBridge.getX(), y);
        keepToSide();
    }

    private void initGestures() {
        mDragGesture = new BounceDragGesture(mActionViewWindowBridge, mCircularActionView);
        mDragGesture.setKeepToSideHiddenWidthRadio(mKeepToSideHiddenWidthRadio);
        mDragGesture.setPressedAlpha(mActiveAlpha);
        mDragGesture.setUnpressedAlpha(mInactiveAlpha);
    }

    private void initWindowBridge() {
        mActionViewWindowBridge = new OrientationAwareWindowBridge(mActionViewWindowLayoutParams, getWindowManager(), mCircularActionView, mContext);
        mMenuWindowBridge = new WindowBridge.DefaultImpl(mMenuWindowLayoutParams, getWindowManager(), mCircularActionMenu);
    }

    public void setKeepToSideHiddenWidthRadio(float keepToSideHiddenWidthRadio) {
        mKeepToSideHiddenWidthRadio = keepToSideHiddenWidthRadio;
        if (mDragGesture != null) {
            mDragGesture.setKeepToSideHiddenWidthRadio(mKeepToSideHiddenWidthRadio);
        }
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                FloatyWindowManger.getWindowType(), 520, -3);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        return layoutParams;
    }

    private void setListeners() {
        setOnActionViewClickListener(v -> {
            if (isExpanded()) {
                collapse();
            } else {
                expand();
            }

        });
        if (mActionViewOnClickListener != null) {
            mDragGesture.setOnDraggedViewClickListener(mActionViewOnClickListener);
        }

        mCircularActionMenu.addOnStateChangeListener(new CircularActionMenu.OnStateChangeListenerAdapter() {
            public void onCollapsed(CircularActionMenu menu) {
                mCircularActionView.setAlpha(mInactiveAlpha);
            }

            public void onExpanded(CircularActionMenu menu) {
                mCircularActionView.setAlpha(mActiveAlpha);
            }
        });
    }

    public void setOnActionViewClickListener(View.OnClickListener listener) {
        if (mDragGesture == null) {
            mActionViewOnClickListener = listener;
        } else {
            mDragGesture.setOnDraggedViewClickListener(listener);
        }
    }

    public void expand() {
        mDragGesture.setEnabled(false);
        setMenuPositionAtActionView();
        if (mActionViewWindowBridge.getX() > mActionViewWindowBridge.getScreenWidth() / 2) {
            mCircularActionMenu.expand(3);
        } else {
            mCircularActionMenu.expand(5);
        }

    }

    public void setActiveAlpha(float activeAlpha) {
        mActiveAlpha = activeAlpha;
        if (mDragGesture != null) {
            mDragGesture.setPressedAlpha(activeAlpha);
        }

    }

    public void setInactiveAlpha(float inactiveAlpha) {
        mInactiveAlpha = inactiveAlpha;
        if (mDragGesture != null) {
            mDragGesture.setUnpressedAlpha(mInactiveAlpha);
        }

    }

    public void collapse() {
        mDragGesture.setEnabled(true);
        setMenuPositionAtActionView();
        mCircularActionMenu.collapse();
        mCircularActionView.setAlpha(mDragGesture.getUnpressedAlpha());
    }

    public boolean isExpanded() {
        return mCircularActionMenu.isExpanded();
    }

    private void setMenuPositionAtActionView() {
        int y = mActionViewWindowBridge.getY() - mCircularActionMenu.getMeasuredHeight() / 2 + mCircularActionView.getMeasuredHeight() / 2;
        int x;
        if (mActionViewWindowBridge.getX() > mActionViewWindowBridge.getScreenWidth() / 2) {
            x = mActionViewWindowBridge.getX() - mCircularActionMenu.getExpandedWidth() + mCircularActionView.getMeasuredWidth() / 2;
        } else {
            x = mActionViewWindowBridge.getX() - mCircularActionMenu.getExpandedWidth() + mCircularActionView.getMeasuredWidth();
        }

        mMenuWindowBridge.updatePosition(x, y);
    }

    private void inflateWindowViews(FloatyService service) {
        mCircularActionMenu = mFloaty.inflateMenuItems(service, this);
        mCircularActionView = mFloaty.inflateActionView(service, this);
        mCircularActionMenu.setVisibility(View.GONE);
        getWindowManager().addView(mCircularActionMenu, mActionViewWindowLayoutParams);
        getWindowManager().addView(mCircularActionView, mMenuWindowLayoutParams);
    }

    public void onServiceDestroy(FloatyService floatyService) {
        close();
    }

    public void close() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.edit()
                .putInt(KEY_POSITION_X, mActionViewWindowBridge.getX())
                .putInt(KEY_POSITION_Y, mActionViewWindowBridge.getY())
                .apply();
        try {
            mOrientationEventListener.disable();
            getWindowManager().removeView(mCircularActionMenu);
            getWindowManager().removeView(mCircularActionView);
            FloatyService.removeWindow(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
