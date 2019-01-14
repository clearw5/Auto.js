package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorHelper;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorMutable;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/8/6.
 */

public class PrefSwitch extends SwitchCompat implements SharedPreferences.OnSharedPreferenceChangeListener, ThemeColorMutable {

    private String mPrefKey;
    private SharedPreferences mSharedPreferences;
    private boolean mDefaultChecked;

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
        if (attrs == null)
            return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrefSwitch);
        mPrefKey = a.getString(R.styleable.PrefSwitch_key);
        mDefaultChecked = a.getBoolean(R.styleable.PrefSwitch_defaultValue, false);
        if (mPrefKey != null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
            readInitialState();
        } else {
            setChecked(mDefaultChecked, false);
        }
        a.recycle();
        ThemeColorManager.add(this);
    }

    public void setThemeColor(ThemeColor color) {
        ThemeColorHelper.setColorPrimary(this, color.colorPrimary);
    }

    private void readInitialState() {
        if (mPrefKey == null || mSharedPreferences == null)
            return;
        setChecked(mSharedPreferences.getBoolean(mPrefKey, mDefaultChecked), false);
    }

    private void notifyPrefChanged(boolean isChecked) {
        if (mPrefKey == null)
            return;
        mSharedPreferences.edit()
                .putBoolean(mPrefKey, isChecked)
                .apply();
    }

    public void setPrefKey(String prefKey) {
        mPrefKey = prefKey;
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
        if(mPrefKey != null){
            setChecked(mSharedPreferences.getBoolean(mPrefKey, mDefaultChecked), false);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        notifyPrefChanged(checked);
    }

    public void setChecked(boolean checked, boolean notifyChange) {
        super.setChecked(checked, notifyChange);
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

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE) {
            readInitialState();
        }

    }
}

