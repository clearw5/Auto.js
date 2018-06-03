package org.autojs.autojs.ui.edit.keyboard;

/**
 * Created by Stardust on 2017/12/9.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.stardust.util.BackPressedHandler;

/**
 * https://github.com/dss886/Android-FunctionsInputDetector
 */
public class FunctionsKeyboardHelper implements BackPressedHandler {

    private static final String SHARE_PREFERENCE_NAME = "FunctionsKeyboardHelper";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";
    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences mPreferences;
    private View mFunctionsLayout;
    private View mEditView;
    private View mContentView;

    private FunctionsKeyboardHelper(Activity activity) {
        mActivity = activity;
        mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mPreferences = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static FunctionsKeyboardHelper with(Activity activity) {
        return new FunctionsKeyboardHelper(activity);
    }

    public FunctionsKeyboardHelper setContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    public void onSoftKeyboardShown() {
        if (mFunctionsLayout.isShown()) {
            lockContentHeight();
            hideFunctionsLayout(false);
            mEditView.postDelayed(FunctionsKeyboardHelper.this::unlockContentHeightDelayed, 200L);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public FunctionsKeyboardHelper setEditView(View editView) {
        mEditView = editView;
        mEditView.requestFocus();
        editView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                onSoftKeyboardShown();
            return false;
        });
        return this;
    }


    public FunctionsKeyboardHelper setFunctionsTrigger(View triggerButton) {
        triggerButton.setOnClickListener(v -> {
            if (mFunctionsLayout.isShown()) {
                lockContentHeight();
                hideFunctionsLayout(true);
                unlockContentHeightDelayed();
            } else {
                if (isSoftInputShown()) {
                    lockContentHeight();
                    showFunctionsLayout();
                    unlockContentHeightDelayed();
                } else {
                    showFunctionsLayout();//两者都没显示，直接显示表情布局
                }
            }
        });
        return this;
    }

    public FunctionsKeyboardHelper setFunctionsView(View FunctionsView) {
        mFunctionsLayout = FunctionsView;
        return this;
    }

    public FunctionsKeyboardHelper build() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        return this;
    }

    private void showFunctionsLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mPreferences.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
        }
        hideSoftInput();
        mFunctionsLayout.getLayoutParams().height = softInputHeight;
        mFunctionsLayout.setVisibility(View.VISIBLE);
    }


    public void hideFunctionsLayout(boolean showSoftInput) {
        if (mFunctionsLayout.isShown()) {
            mFunctionsLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }


    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }


    private void unlockContentHeightDelayed() {
        mEditView.postDelayed(() -> ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F, 200L);
    }

    private void showSoftInput() {
        mEditView.requestFocus();
        mEditView.post(() -> mInputManager.showSoftInput(mEditView, InputMethodManager.SHOW_FORCED));
    }


    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditView.getWindowToken(), 0);
    }


    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftKeyButtonsHeight();
        }
        if (softInputHeight > 0) {
            mPreferences.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftKeyButtonsHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    public int getKeyBoardHeight() {
        return mPreferences.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
    }

    @Override
    public boolean onBackPressed(Activity activity) {
        if (mFunctionsLayout.isShown()) {
            hideFunctionsLayout(false);
            return true;
        }
        return false;
    }
}