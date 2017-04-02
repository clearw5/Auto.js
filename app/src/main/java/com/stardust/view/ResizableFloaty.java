package com.stardust.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.stardust.scriptdroid.R;
import com.stardust.util.ViewUtil;
import com.stardust.widget.ViewSwitcher;

/**
 * Created by Stardust on 2017/3/11.
 */

public class ResizableFloaty {

    private static final String TAG = "ResizableFloaty";

    private View mCollapsedView, mExpandedView;
    private static ResizableFloaty floaty;

    public ResizableFloaty(View collapsedView, View expandedView) {
        mExpandedView = expandedView;
        mCollapsedView = collapsedView;
    }

    public static void startService(Context context, View collapsedView, View expandedView) {
        floaty = new ResizableFloaty(collapsedView, expandedView);
        context.startService(new Intent(context, FloatingWindowService.class));
    }

    interface WindowBridge {
        int getX();

        int getY();

        void updatePosition(int x, int y);

        int getWidth();

        int getHeight();

        void updateMeasure(int width, int height);

        int getScreenWidth();

        int getScreenHeight();
    }

    public static class FloatingWindowService extends Service {

        private WindowManager mWindowManager;
        private WindowManager.LayoutParams mWindowLayoutParams;
        private RelativeLayout mWindowView;
        private ViewSwitcher mCollapseExpandViewSwitcher;
        private View mResizer;
        private ResizableFloaty mFloaty = floaty;
        private ResizeGesture mResizeGesture;
        private DragGesture mDragGesture;
        private ViewStack mViewStack = new ViewStack(new ViewStack.CurrentViewSetter() {
            @Override
            public void setCurrentView(View v) {
                mCollapseExpandViewSwitcher.setSecondView(v);
            }
        });

        private WindowBridge mWindowBridge = new WindowBridge() {
            DisplayMetrics mDisplayMetrics;

            @Override
            public int getX() {
                return mWindowLayoutParams.x;
            }

            @Override
            public int getY() {
                return mWindowLayoutParams.y;
            }

            @Override
            public void updatePosition(int x, int y) {
                mWindowLayoutParams.x = x;
                mWindowLayoutParams.y = y;
                mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
            }

            @Override
            public int getWidth() {
                return mWindowView.getWidth();
            }

            @Override
            public int getHeight() {
                return mWindowView.getHeight();
            }

            @Override
            public void updateMeasure(int width, int height) {
                mWindowLayoutParams.width = width;
                mWindowLayoutParams.height = height;
                mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
            }

            @Override
            public int getScreenWidth() {
                ensureDisplayMetrics();
                return mDisplayMetrics.widthPixels;
            }

            @Override
            public int getScreenHeight() {
                ensureDisplayMetrics();
                return mDisplayMetrics.heightPixels;
            }

            private void ensureDisplayMetrics() {
                if (mDisplayMetrics == null) {
                    mDisplayMetrics = new DisplayMetrics();
                    mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
                }
            }
        };

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowLayoutParams = createWindowLayoutParams();
            initWindowView();
            initGesture();
            setUpListeners();
        }

        private void setUpListeners() {
            mDragGesture.setOnDraggedViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableWindowFocus();
                    expand();
                }
            });
            mWindowView.setOnKeyListener(new View.OnKeyListener() {
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

        private void expand() {
            mCollapseExpandViewSwitcher.showSecond();
            mResizeGesture.setResizeEnabled(true);
            mDragGesture.setKeepToSide(false);
        }

        private void onBackPressed() {
            if (mViewStack.canGoBack()) {
                mViewStack.goBack();
            } else {
                collapse();
            }
        }

        private void onHomePressed() {
            mViewStack.goBackToFirst();
            collapse();
        }

        private void collapse() {
            mCollapseExpandViewSwitcher.showFirst();
            disableWindowFocus();
            mResizeGesture.setResizeEnabled(false);
            mDragGesture.setKeepToSide(true);
        }

        private void disableWindowFocus() {
            mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }

        private void initGesture() {
            mResizeGesture = ResizeGesture.enableResize(mResizer, mWindowBridge);
            mDragGesture = DragGesture.enableDrag(mWindowView, mWindowBridge);
            mResizeGesture.setResizeEnabled(false);
            mDragGesture.setKeepToSide(true);
        }

        private void enableWindowFocus() {
            mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
            mWindowView.requestFocus();
        }

        private void initWindowView() {
            mWindowView = (RelativeLayout) View.inflate(getApplicationContext(), R.layout.resizable_floaty_container, null);
            mWindowView.setFocusableInTouchMode(true);
            mCollapseExpandViewSwitcher = (ViewSwitcher) mWindowView.findViewById(R.id.container);
            mResizer = mWindowView.findViewById(R.id.resizer);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mCollapseExpandViewSwitcher.addView(floaty.mCollapsedView, params);
            mCollapseExpandViewSwitcher.addView(floaty.mExpandedView, params);
            mViewStack.setRootView(floaty.mExpandedView);
            mWindowManager.addView(mWindowView, mWindowLayoutParams);
        }

        private WindowManager.LayoutParams createWindowLayoutParams() {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            return layoutParams;
        }
    }

    public static class DragGesture extends GestureDetector.SimpleOnGestureListener {

        public static DragGesture enableDrag(final View view, WindowBridge bridge) {
            final DragGesture gestureListener = new DragGesture(bridge, view) {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    view.setAlpha(0.8f);
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

            };
            final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(view.getContext(), gestureListener);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        view.setAlpha(1.0f);
                        if (!gestureListener.mFlung && gestureListener.isKeepToSide()) {
                            gestureListener.keepToSide();
                        }
                    }
                    return true;
                }
            });
            return gestureListener;
        }

        private WindowBridge mWindowBridge;
        private boolean mKeepToSide;
        private View.OnClickListener mOnClickListener;
        private View mView;

        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        private boolean mFlung = false;

        public DragGesture(WindowBridge windowBridge, View view) {
            mWindowBridge = windowBridge;
            mView = view;
        }

        public void setKeepToSide(boolean keepToSide) {
            mKeepToSide = keepToSide;
        }

        public boolean isKeepToSide() {
            return mKeepToSide;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            initialX = mWindowBridge.getX();
            initialY = mWindowBridge.getY();
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            mFlung = false;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mWindowBridge.updatePosition(initialX + (int) ((e2.getRawX() - initialTouchX)),
                    initialY + (int) ((e2.getRawY() - initialTouchY)));
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mFlung = true;
            if (mKeepToSide)
                keepToSide();
            return false;
        }

        public void keepToSide() {
            int newX = mWindowBridge.getX();
            if (newX > mWindowBridge.getScreenWidth() / 2)
                mWindowBridge.updatePosition(mWindowBridge.getScreenWidth(), mWindowBridge.getY());
            else
                mWindowBridge.updatePosition(0, mWindowBridge.getY());
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mOnClickListener != null)
                mOnClickListener.onClick(mView);
            return super.onSingleTapConfirmed(e);
        }

        public void setOnDraggedViewClickListener(View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }
    }

    public static class ResizeGesture extends GestureDetector.SimpleOnGestureListener {

        public static ResizeGesture enableResize(View resizer, WindowBridge windowBridge) {
            ResizeGesture resizeGesture = new ResizeGesture(windowBridge, resizer);
            final GestureDetector detector = new GestureDetector(resizer.getContext(), resizeGesture);
            resizer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    detector.onTouchEvent(event);
                    return true;
                }
            });
            return resizeGesture;
        }

        private WindowBridge mWindowBridge;
        private float initialTouchX;
        private float initialTouchY;
        private int mInitialWidth, mInitialHeight;
        private View mResizerView;
        private int mMinHeight = 200, mMinWidth = 200;
        private final int mStatusBarHeight;


        public ResizeGesture(WindowBridge windowBridge, View resizerView) {
            mWindowBridge = windowBridge;
            mResizerView = resizerView;
            mStatusBarHeight = ViewUtil.getStatusBarHeight(resizerView.getContext());
        }

        public void setMinHeight(int minHeight) {
            mMinHeight = minHeight;
        }

        public void setMinWidth(int minWidth) {
            mMinWidth = minWidth;
        }

        public void setResizeEnabled(boolean enabled) {
            mResizerView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            mInitialWidth = mWindowBridge.getWidth();
            mInitialHeight = mWindowBridge.getHeight();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, final MotionEvent e2, float distanceX, float distanceY) {
            int newWidth = mInitialWidth + (int) ((e2.getRawX() - initialTouchX));
            int newHeight = mInitialHeight + (int) ((e2.getRawY() - initialTouchY));
            newWidth = Math.max(mMinWidth, newWidth);
            newHeight = Math.max(mMinHeight, newHeight);
            newWidth = Math.min(mWindowBridge.getScreenWidth() - mWindowBridge.getX() - mResizerView.getWidth(), newWidth);
            newHeight = Math.min(mWindowBridge.getScreenHeight() - mWindowBridge.getY() - mResizerView.getHeight() - mStatusBarHeight, newHeight);
            mWindowBridge.updateMeasure(newWidth, newHeight);
            return true;
        }
    }


}
