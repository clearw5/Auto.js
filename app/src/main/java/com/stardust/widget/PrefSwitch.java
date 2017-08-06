package com.stardust.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.stardust.scriptdroid.R;

import java.util.Objects;

/**
 * Created by Stardust on 2017/8/6.
 */

public class PrefSwitch extends SwitchCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String mPrefKey;
    private SharedPreferences mSharedPreferences;

    public PrefSwitch(Context context) {
        super(context);
        init(null);
    }

    public PrefSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PrefSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrefSwitch);
            mPrefKey = a.getString(R.styleable.PrefSwitch_key);
            boolean defaultValue = a.getBoolean(R.styleable.PrefSwitch_defaultValue, false);
            if (mPrefKey != null)
                setChecked(mSharedPreferences.getBoolean(mPrefKey, defaultValue), false);
            else
                setChecked(defaultValue, false);
            a.recycle();
        }
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    private void notifyPrefChanged(boolean isChecked) {
        if (mPrefKey == null)
            return;
        mSharedPreferences.edit()
                .putBoolean(mPrefKey, isChecked)
                .apply();
    }


    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, true);

    }

    public void setChecked(boolean checked, boolean notifyChange) {
        super.setChecked(checked);
        if (notifyChange) {
            notifyPrefChanged(checked);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mPrefKey != null && mPrefKey.equals(key)) {
            setChecked(mSharedPreferences.getBoolean(mPrefKey, isChecked()), false);
        }
    }
}

