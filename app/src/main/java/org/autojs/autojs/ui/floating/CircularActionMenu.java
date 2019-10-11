package org.autojs.autojs.ui.floating;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import org.autojs.autojs.R;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/9/25.
 */

public class CircularActionMenu extends FrameLayout {

    public interface OnStateChangeListener {
        void onExpanding(CircularActionMenu menu);

        void onExpanded(CircularActionMenu menu);

        void onCollapsing(CircularActionMenu menu);

        void onCollapsed(CircularActionMenu menu);

        void onMeasured(CircularActionMenu menu);
    }

    public static class OnStateChangeListenerAdapter implements OnStateChangeListener {

        @Override
        public void onExpanding(CircularActionMenu menu) {

        }

        @Override
        public void onExpanded(CircularActionMenu menu) {

        }

        @Override
        public void onCollapsing(CircularActionMenu menu) {

        }

        @Override
        public void onCollapsed(CircularActionMenu menu) {

        }

        @Override
        public void onMeasured(CircularActionMenu menu) {

        }
    }

    private PointF[] mItemExpandedPositionOffsets;
    private CopyOnWriteArrayList<OnStateChangeListener> mOnStateChangeListeners = new CopyOnWriteArrayList<>();
    private boolean mExpanded;
    private boolean mExpanding = false;
    private boolean mCollapsing = false;
    private float mRadius = 200;
    private float mAngle = (float) Math.toRadians(90);
    private long mDuration = 200;
    private int mExpandedHeight = -1;
    private int mExpandedWidth = -1;
    private final Interpolator mInterpolator = new FastOutSlowInInterpolator();


    public CircularActionMenu(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CircularActionMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircularActionMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularActionMenu);
        mRadius = a.getDimensionPixelSize(R.styleable.CircularActionMenu_cam_radius, (int) mRadius);
        int angleInDegree = a.getInt(R.styleable.CircularActionMenu_cam_angle, 0);
        if (angleInDegree != 0) {
            mAngle = (float) Math.toRadians(angleInDegree);
        }
        for (int i = 0; i < getItemCount(); i++) {
            View v = getItemAt(i);
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            params.gravity = Gravity.START | Gravity.LEFT | Gravity.CENTER_VERTICAL;
            // FIXME: 2017/10/17 Not working
            updateViewLayout(v, params);
        }
        requestLayout();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void expand(int direction) {
        setVisibility(VISIBLE);
        mExpanding = true;
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExpanding = false;
                mExpanded = true;
                for (OnStateChangeListener l : mOnStateChangeListeners) {
                    l.onExpanded(CircularActionMenu.this);
                }
            }
        };
        ScaleAnimation scaleAnimation = createScaleAnimation(0, 1);
        direction = (direction == Gravity.RIGHT ? 1 : -1);
        for (int i = 0; i < getItemCount(); i++) {
            View item = getItemAt(i);
            item.animate()
                    .translationXBy(direction * mItemExpandedPositionOffsets[i].x)
                    .translationYBy(mItemExpandedPositionOffsets[i].y)
                    .setListener(listener)
                    .setDuration(mDuration)
                    .start();
            item.startAnimation(scaleAnimation);
        }
        for (OnStateChangeListener l : mOnStateChangeListeners) {
            l.onExpanding(CircularActionMenu.this);
        }
    }

    private ScaleAnimation createScaleAnimation(float fromScale, float toScale) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromScale, toScale, fromScale, toScale, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(mDuration);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(mInterpolator);
        return scaleAnimation;
    }

    public View getItemAt(int i) {
        return getChildAt(i);
    }

    public void collapse() {
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCollapsing = false;
                mExpanded = false;
                setVisibility(GONE);
                for (OnStateChangeListener l : mOnStateChangeListeners) {
                    l.onCollapsed(CircularActionMenu.this);
                }
            }
        };
        mCollapsing = true;
        ScaleAnimation scaleAnimation = createScaleAnimation(1, 0);
        for (int i = 0; i < getItemCount(); i++) {
            View item = getItemAt(i);
            item.animate()
                    .translationX(0)
                    .translationY(0)
                    .setListener(listener)
                    .setDuration(mDuration)
                    .setInterpolator(mInterpolator)
                    .start();
            item.startAnimation(scaleAnimation);
        }
        for (OnStateChangeListener l : mOnStateChangeListeners) {
            l.onCollapsing(CircularActionMenu.this);
        }
    }

    public void addOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListeners.add(onStateChangeListener);
    }

    public boolean removeOnStateChangeListener(OnStateChangeListener listener) {
        return mOnStateChangeListeners.remove(listener);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public boolean isExpanding() {
        return mExpanding;
    }

    public boolean isCollapsing() {
        return mCollapsing;
    }


    public int getItemCount() {
        return getChildCount();
    }

    private void calcExpandedPositions() {
        mItemExpandedPositionOffsets = new PointF[getItemCount()];
        double averageAngle = mAngle / (getItemCount() - 1);
        for (int i = 0; i < getItemCount(); i++) {
            double angle = -mAngle / 2 + i * averageAngle;
            mItemExpandedPositionOffsets[i] = new PointF((float) (mRadius * Math.cos(angle)),
                    (float) (mRadius * Math.sin(angle)));
        }
    }

    private void calcExpandedSize() {
        int maxX = 0;
        int maxY = 0;
        int minY = Integer.MAX_VALUE;
        int maxWidth = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View item = getItemAt(i);
            maxWidth = Math.max(item.getMeasuredWidth(), maxWidth);
            maxX = Math.max((int) (mItemExpandedPositionOffsets[i].x + item.getMeasuredWidth()), maxX);
            // FIXME: 2017/9/26 这样算出来的高度略大
            maxY = Math.max((int) (mItemExpandedPositionOffsets[i].y + item.getMeasuredHeight()), maxY);
            minY = Math.min((int) (mItemExpandedPositionOffsets[i].y - item.getMeasuredHeight()), minY);
        }
        mExpandedWidth = maxX;
        mExpandedHeight = maxY - minY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        calcExpandedPositions();
        if (mExpandedHeight == -1 || mExpandedWidth == -1) {
            calcExpandedSize();
        }
        setMeasuredDimension(2 * mExpandedWidth, mExpandedHeight);
        for (OnStateChangeListener listener : mOnStateChangeListeners) {
            listener.onMeasured(this);
        }
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    public int getExpandedWidth() {
        return mExpandedWidth;
    }

}
