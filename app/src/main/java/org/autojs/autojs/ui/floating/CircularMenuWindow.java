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
import com.stardust.floatingcircularactionmenu.CircularActionMenu;
import com.stardust.floatingcircularactionmenu.gesture.BounceDragGesture;
import com.stardust.util.ScreenMetrics;

public class CircularMenuWindow implements FloatyWindow {

    private static final String KEY_POSITION_X = CircularMenuWindow.class.getName() + ".position.x";
    private static final String KEY_POSITION_Y = CircularMenuWindow.class.getName() + ".position.y";

    protected CircularMenuFloaty mFloaty;
    protected WindowManager mWindowManager;
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
        this.mFloaty = floaty;
        mContext = context;
    }

    public void onCreate(FloatyService service, WindowManager manager) {
        this.mWindowManager = manager;
        this.mActionViewWindowLayoutParams = this.createWindowLayoutParams();
        this.mMenuWindowLayoutParams = this.createWindowLayoutParams();
        this.inflateWindowViews(service);
        this.initWindowBridge();
        this.initGestures();
        this.setListeners();
        this.setInitialState();
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
        this.mDragGesture = new BounceDragGesture(this.mActionViewWindowBridge, this.mCircularActionView);
        this.mDragGesture.setKeepToSideHiddenWidthRadio(this.mKeepToSideHiddenWidthRadio);
        this.mDragGesture.setPressedAlpha(this.mActiveAlpha);
        this.mDragGesture.setUnpressedAlpha(this.mInactiveAlpha);
    }

    private void initWindowBridge() {
        this.mActionViewWindowBridge = new OrientationAwareWindowBridge(this.mActionViewWindowLayoutParams, this.mWindowManager, this.mCircularActionView, mContext);
        this.mMenuWindowBridge = new WindowBridge.DefaultImpl(this.mMenuWindowLayoutParams, this.mWindowManager, this.mCircularActionMenu);
    }

    public void setKeepToSideHiddenWidthRadio(float keepToSideHiddenWidthRadio) {
        this.mKeepToSideHiddenWidthRadio = keepToSideHiddenWidthRadio;
        if (this.mDragGesture != null) {
            this.mDragGesture.setKeepToSideHiddenWidthRadio(this.mKeepToSideHiddenWidthRadio);
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
        this.setOnActionViewClickListener(v -> {
            if (isExpanded()) {
                collapse();
            } else {
                expand();
            }

        });
        if (this.mActionViewOnClickListener != null) {
            this.mDragGesture.setOnDraggedViewClickListener(this.mActionViewOnClickListener);
        }

        this.mCircularActionMenu.addOnStateChangeListener(new CircularActionMenu.OnStateChangeListenerAdapter() {
            public void onCollapsed(CircularActionMenu menu) {
                mCircularActionView.setAlpha(mInactiveAlpha);
            }

            public void onExpanded(CircularActionMenu menu) {
                mCircularActionView.setAlpha(mActiveAlpha);
            }
        });
    }

    public void setOnActionViewClickListener(View.OnClickListener listener) {
        if (this.mDragGesture == null) {
            this.mActionViewOnClickListener = listener;
        } else {
            this.mDragGesture.setOnDraggedViewClickListener(listener);
        }
    }

    public void expand() {
        this.mDragGesture.setEnabled(false);
        this.setMenuPositionAtActionView();
        if (this.mActionViewWindowBridge.getX() > this.mActionViewWindowBridge.getScreenWidth() / 2) {
            this.mCircularActionMenu.expand(3);
        } else {
            this.mCircularActionMenu.expand(5);
        }

    }

    public void setActiveAlpha(float activeAlpha) {
        this.mActiveAlpha = activeAlpha;
        if (this.mDragGesture != null) {
            this.mDragGesture.setPressedAlpha(activeAlpha);
        }

    }

    public void setInactiveAlpha(float inactiveAlpha) {
        this.mInactiveAlpha = inactiveAlpha;
        if (this.mDragGesture != null) {
            this.mDragGesture.setUnpressedAlpha(this.mInactiveAlpha);
        }

    }

    public void collapse() {
        this.mDragGesture.setEnabled(true);
        this.setMenuPositionAtActionView();
        this.mCircularActionMenu.collapse();
        this.mCircularActionView.setAlpha(this.mDragGesture.getUnpressedAlpha());
    }

    public boolean isExpanded() {
        return this.mCircularActionMenu.isExpanded();
    }

    private void setMenuPositionAtActionView() {
        int y = this.mActionViewWindowBridge.getY() - this.mCircularActionMenu.getMeasuredHeight() / 2 + this.mCircularActionView.getMeasuredHeight() / 2;
        int x;
        if (this.mActionViewWindowBridge.getX() > this.mActionViewWindowBridge.getScreenWidth() / 2) {
            x = this.mActionViewWindowBridge.getX() - this.mCircularActionMenu.getExpandedWidth() + this.mCircularActionView.getMeasuredWidth() / 2;
        } else {
            x = this.mActionViewWindowBridge.getX() - this.mCircularActionMenu.getExpandedWidth() + this.mCircularActionView.getMeasuredWidth();
        }

        this.mMenuWindowBridge.updatePosition(x, y);
    }

    private void inflateWindowViews(FloatyService service) {
        this.mCircularActionMenu = this.mFloaty.inflateMenuItems(service, this);
        this.mCircularActionView = this.mFloaty.inflateActionView(service, this);
        this.mCircularActionMenu.setVisibility(View.GONE);
        this.mWindowManager.addView(this.mCircularActionMenu, this.mActionViewWindowLayoutParams);
        this.mWindowManager.addView(this.mCircularActionView, this.mMenuWindowLayoutParams);
    }

    public void onServiceDestroy(FloatyService floatyService) {
        this.close();
    }

    public void close() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.edit()
                .putInt(KEY_POSITION_X, mActionViewWindowBridge.getX())
                .putInt(KEY_POSITION_Y, mActionViewWindowBridge.getY())
                .apply();
        mOrientationEventListener.disable();
        this.mWindowManager.removeView(this.mCircularActionMenu);
        this.mWindowManager.removeView(this.mCircularActionView);
        FloatyService.removeWindow(this);
    }

}
