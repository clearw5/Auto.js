package com.stardust.scriptdroid.ui.main.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.R;
import com.stardust.widget.PrefSwitch;
import com.stardust.widget.SwitchCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Stardust on 2017/8/25.
 */
public class DrawerMenuItem extends FrameLayout {

    private static final long CLICK_TIMEOUT = 1000;

    @BindView(R.id.sw)
    PrefSwitch mSwitchCompat;

    @BindView(R.id.progress_bar)
    MaterialProgressBar mProgressBar;

    @BindView(R.id.icon)
    ImageView mIcon;

    @BindView(R.id.title)
    TextView mTitle;


    private OnClickListener mOnClickListener;
    private boolean mAntiShake = false;
    private long mLastClickMillis;

    public DrawerMenuItem(Context context) {
        super(context);
        init(null);
    }

    public DrawerMenuItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawerMenuItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawerMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.drawer_menu_item, this);
        ButterKnife.bind(this, this);
        if (attrs == null)
            return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawerMenuItem);
        mIcon.setImageResource(a.getResourceId(R.styleable.DrawerMenuItem_icon, 0));
        mTitle.setText(a.getString(R.styleable.DrawerMenuItem_title));
        mAntiShake = a.getBoolean(R.styleable.DrawerMenuItem_anti_shake, false);
        if (a.getBoolean(R.styleable.DrawerMenuItem_with_switch, false)) {
            enableSwitch(a.getString(R.styleable.DrawerMenuItem_pref_key));
        }
        a.recycle();
    }

    private void enableSwitch(String prefKey) {
        mSwitchCompat.setVisibility(VISIBLE);
        if (prefKey != null) {
            mSwitchCompat.setPrefKey(prefKey);
        }
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onClick();
            }
        });
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchCompat.toggle();
            }
        });
    }

    private void onClick() {
        if (mAntiShake && (System.currentTimeMillis() - mLastClickMillis < CLICK_TIMEOUT)) {
            Toast.makeText(getContext(), R.string.text_click_too_frequently, Toast.LENGTH_SHORT).show();
            mSwitchCompat.setChecked(!mSwitchCompat.isChecked(), false);
            return;
        }
        mLastClickMillis = System.currentTimeMillis();
        if (mOnClickListener != null) {
            mOnClickListener.onClick(DrawerMenuItem.this);
        }
    }

    public void setProgress(boolean onProgress) {
        mProgressBar.setVisibility(onProgress ? VISIBLE : GONE);
        mIcon.setVisibility(onProgress ? GONE : VISIBLE);
        mSwitchCompat.setEnabled(!onProgress);
        setEnabled(!onProgress);

    }

    public SwitchCompat getSwitchCompat() {
        return mSwitchCompat;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        if (mSwitchCompat.getVisibility() == VISIBLE) {
            mOnClickListener = onClickListener;
        } else {
            super.setOnClickListener(onClickListener);
        }
    }
}
