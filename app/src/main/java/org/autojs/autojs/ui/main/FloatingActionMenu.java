package org.autojs.autojs.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.autojs.autojs.R;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/9/24.
 */

public class FloatingActionMenu extends FrameLayout implements View.OnClickListener {

    public interface OnFloatingActionButtonClickListener {
        void onClick(FloatingActionButton button, int pos);
    }

    private static final int[] ICONS = {
            R.drawable.ic_floating_action_menu_dir,
            R.drawable.ic_floating_action_menu_file,
            R.drawable.ic_floating_action_menu_open,
            R.drawable.ic_project};
    private static final int[] LABELS = {R.string.text_directory, R.string.text_file, R.string.text_import, R.string.text_project};
    private TextView[] mLabels;
    private FloatingActionButton[] mFabs;
    private View[] mFabContainers;
    private boolean mExpanded = false;
    private int mInterval = 30;
    private int mDuration = 250;
    private final Interpolator mInterpolator = new FastOutSlowInInterpolator();
    private final PublishSubject<Boolean> mState = PublishSubject.create();
    private OnFloatingActionButtonClickListener mOnFloatingActionButtonClickListener;

    public FloatingActionMenu(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatingActionMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingActionMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        buildFabs(ICONS, LABELS);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void expand() {
        setVisibility(VISIBLE);
        int h = mFabs[0].getHeight();
        for (int i = 0; i < mFabContainers.length; i++) {
            animateY(mFabContainers[i], -(h + mInterval) * (i + 1), null);
            rotate(mFabs[i]);
        }
        mExpanded = true;
        mState.onNext(true);
    }

    private void rotate(FloatingActionButton fab) {
        fab.setRotation(0);
        fab.animate()
                .rotation(360)
                .setDuration(mDuration)
                .setInterpolator(mInterpolator)
                .start();
    }


    private void animateY(View view, float y, Animator.AnimatorListener l) {
        view.animate()
                .translationY(y)
                .setDuration(mDuration)
                .setInterpolator(mInterpolator)
                .setListener(l)
                .start();
    }

    public void collapse() {
        animateY(mFabContainers[0], 0, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
            }
        });
        for (int i = 1; i < mFabContainers.length; i++) {
            animateY(mFabContainers[i], 0, null);
            rotate(mFabs[i]);
        }
        mExpanded = false;
        mState.onNext(false);
    }

    private void buildFabs(int[] icons, int[] labels) {
        if (icons.length != labels.length)
            throw new IllegalArgumentException("icons.length = " + icons.length + " is not equal to labels.length = " + labels.length);
        mFabs = new FloatingActionButton[icons.length];
        mLabels = new TextView[icons.length];
        mFabContainers = new View[icons.length];
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < icons.length; i++) {
            mFabContainers[i] = inflater.inflate(R.layout.item_floating_action_menu, this, false);
            mFabs[i] = (FloatingActionButton) mFabContainers[i].findViewById(R.id.floating_action_button);
            mFabs[i].setImageResource(icons[i]);
            mFabs[i].setOnClickListener(this);
            mFabs[i].setTag(i);
            mLabels[i] = (TextView) mFabContainers[i].findViewById(R.id.label);
            mLabels[i].setText(labels[i]);
            addView(mFabContainers[i]);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // Call super.onMeasure to measure children and width
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            return;
        }
        int h = mFabContainers[0].getMeasuredHeight();
        setMeasuredDimension(getMeasuredWidth(), (h + mInterval) * mFabs.length + h);
    }

    public PublishSubject<Boolean> getState() {
        return mState;
    }

    @Override
    public void onClick(View v) {
        collapse();
        if (mOnFloatingActionButtonClickListener != null) {
            mOnFloatingActionButtonClickListener.onClick((FloatingActionButton) v, (int) v.getTag());
        }

    }

    public void setOnFloatingActionButtonClickListener(OnFloatingActionButtonClickListener onFloatingActionButtonClickListener) {
        mOnFloatingActionButtonClickListener = onFloatingActionButtonClickListener;
    }
}
