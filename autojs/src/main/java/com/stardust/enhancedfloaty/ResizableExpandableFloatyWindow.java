package com.stardust.enhancedfloaty;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.stardust.autojs.R;
import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.enhancedfloaty.util.WindowTypeCompat;

import com.stardust.widget.ViewSwitcher;

/**
 * Created by Stardust on 2017/4/18.
 */

public class ResizableExpandableFloatyWindow extends FloatyWindow {

    private static final int INITIAL_WINDOW_PARAM_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    private static final String TAG = "ExpandableFloatyService";


    private ResizableExpandableFloaty mFloaty;
    private ViewSwitcher mCollapseExpandViewSwitcher;
    private View mCollapsedView;
    private View mExpandedView;
    private View mResizer;
    private View mMoveCursor;
    private DragGesture mDragGesture;
    private int mCollapsedViewX, mCollapsedViewY;
    private int mExpandedViewX, mExpandedViewY;

    private ViewStack mViewStack = new ViewStack(new ViewStack.CurrentViewSetter() {
        @Override
        public void setCurrentView(View v) {
            mCollapseExpandViewSwitcher.setSecondView(v);
        }
    });


    public ResizableExpandableFloatyWindow(ResizableExpandableFloaty floaty) {
        if (floaty == null) {
            throw new NullPointerException("floaty == null");
        }
        mFloaty = floaty;
    }


    @Override
    protected View onCreateView(FloatyService service) {
        inflateWindowViews(service);
        View windowView = View.inflate(service, R.layout.ef_expandable_floaty_container, null);
        windowView.setFocusableInTouchMode(true);
        mCollapseExpandViewSwitcher = windowView.findViewById(R.id.container);
        mCollapseExpandViewSwitcher.setMeasureAllChildren(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCollapseExpandViewSwitcher.addView(mCollapsedView, params);
        mCollapseExpandViewSwitcher.addView(mExpandedView, params);
        mViewStack.setRootView(mExpandedView);
        return windowView;
    }

    @Override
    protected void onAttachToWindow(View view, WindowManager manager) {
        super.onAttachToWindow(view, manager);
        initGesture();
        setKeyListener();
        setInitialState();
    }

    public View getCollapsedView() {
        return mCollapsedView;
    }

    public View getExpandedView() {
        return mExpandedView;
    }

    public View getResizer() {
        return mResizer;
    }

    public View getMoveCursor() {
        return mMoveCursor;
    }

    protected ViewStack getViewStack() {
        return mViewStack;
    }

    protected int getCollapsedViewX() {
        return mCollapsedViewX;
    }

    protected void setCollapsedViewX(int collapsedViewX) {
        mCollapsedViewX = collapsedViewX;
    }

    protected int getCollapsedViewY() {
        return mCollapsedViewY;
    }

    protected void setCollapsedViewY(int collapsedViewY) {
        mCollapsedViewY = collapsedViewY;
    }

    protected int getExpandedViewX() {
        return mExpandedViewX;
    }

    protected void setExpandedViewX(int expandedViewX) {
        mExpandedViewX = expandedViewX;
    }

    protected int getExpandedViewY() {
        return mExpandedViewY;
    }

    protected void setExpandedViewY(int expandedViewY) {
        mExpandedViewY = expandedViewY;
    }

    protected void setInitialState() {
        ResizableExpandableFloaty floaty = getFloaty();
        boolean expand = floaty.isInitialExpanded();
        if (expand) {
            setExpandedViewX(floaty.getInitialX());
            setExpandedViewY(floaty.getInitialY());
            expand();
        } else {
            setCollapsedViewX(floaty.getInitialX());
            setCollapsedViewX(floaty.getInitialY());
            getWindowBridge().updatePosition(getCollapsedViewX(), getCollapsedViewY());
        }
    }

    @Override
    protected WindowBridge onCreateWindowBridge(WindowManager.LayoutParams params) {
        return new WindowBridge.DefaultImpl(params, getWindowManager(), getWindowView()) {
            @Override
            public void updatePosition(int x, int y) {
                super.updatePosition(x, y);
                if (getViewSwitcher().getCurrentView() == getExpandedView()) {
                    setExpandedViewX(x);
                    setExpandedViewY(y);
                } else {
                    setCollapsedViewX(x);
                    setCollapsedViewY(y);
                }
            }

        };
    }

    protected void inflateWindowViews(FloatyService service) {
        ResizableExpandableFloaty floaty = getFloaty();
        mExpandedView = floaty.inflateExpandedView(service, this);
        mCollapsedView = floaty.inflateCollapsedView(service, this);
        mResizer = floaty.getResizerView(getExpandedView());
        mMoveCursor = floaty.getMoveCursorView(getExpandedView());
    }

    protected WindowManager.LayoutParams onCreateWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowTypeCompat.getPhoneWindowType(),
                INITIAL_WINDOW_PARAM_FLAG,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        return layoutParams;
    }

    protected void initGesture() {
        enableResize();
        enableMove();
    }

    protected void enableResize() {
        if (getResizer() != null) {
            ResizeGesture.enableResize(getResizer(), getExpandedView(), getWindowBridge());
        }
    }

    public ResizableExpandableFloaty getFloaty() {
        return mFloaty;
    }

    protected void enableMove() {
        if (getMoveCursor() != null) {
            DragGesture gesture = new DragGesture(getWindowBridge(), getMoveCursor());
            gesture.setPressedAlpha(1.0f);
        }
        DragGesture dragGesture = new DragGesture(getWindowBridge(), getCollapsedView());
        dragGesture.setUnpressedAlpha(getFloaty().getCollapsedViewUnpressedAlpha());
        dragGesture.setPressedAlpha(getFloaty().getCollapsedViewPressedAlpha());
        dragGesture.setKeepToSide(true);
        dragGesture.setKeepToSideHiddenWidthRadio(getFloaty().getCollapsedHiddenWidthRadio());
        dragGesture.setOnDraggedViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand();
            }
        });
        setDragGesture(dragGesture);
    }

    protected void setDragGesture(DragGesture dragGesture) {
        mDragGesture = dragGesture;
    }

    protected DragGesture getDragGesture() {
        return mDragGesture;
    }

    public void expand() {
        getViewSwitcher().showSecond();
        //enableWindowLimit();
        if (getFloaty().shouldRequestFocusWhenExpand()) {
            requestWindowFocus();
        }
        getDragGesture().setKeepToSide(false);
        getWindowBridge().updatePosition(getExpandedViewX(), getExpandedViewY());
    }

    protected ViewSwitcher getViewSwitcher() {
        return mCollapseExpandViewSwitcher;
    }

    public void collapse() {
        getViewSwitcher().showFirst();
        disableWindowFocus();
        setWindowLayoutNoLimit();
        getDragGesture().setKeepToSide(true);
        getWindowBridge().updatePosition(getCollapsedViewX(), getCollapsedViewY());
    }

    protected void setKeyListener() {
        getWindowView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    onBackPressed();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    onHomePressed();
                    return true;
                }
                return false;
            }


        });
    }

    private void onBackPressed() {
        ViewStack viewStack = getViewStack();
        if (viewStack.canGoBack()) {
            viewStack.goBack();
        } else {
            collapse();
        }
    }

    private void onHomePressed() {
        getViewStack().goBackToFirst();
        collapse();
    }


    public void disableWindowFocus() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        updateWindowLayoutParams(windowLayoutParams);
    }

    public void setWindowLayoutInScreen() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        updateWindowLayoutParams(windowLayoutParams);
    }

    public void requestWindowFocus() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        updateWindowLayoutParams(windowLayoutParams);
        getWindowView().requestFocus();
    }

    public void setWindowLayoutNoLimit() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        updateWindowLayoutParams(windowLayoutParams);
    }

}

