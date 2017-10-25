package com.stardust.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Created by Stardust on 2017/9/18.
 */

public class SwitchCompat extends android.support.v7.widget.SwitchCompat {

    private boolean mIgnoreCheckedChange = false;

    public SwitchCompat(Context context) {
        super(context);
    }

    public SwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setOnCheckedChangeListener(final OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mIgnoreCheckedChange) {
                    return;
                }
                listener.onCheckedChanged(buttonView, isChecked);
            }
        });
    }

    public void setChecked(boolean checked, boolean notify) {
        mIgnoreCheckedChange = !notify;
        setChecked(checked);
        mIgnoreCheckedChange = false;
    }

    public void toggle(boolean notify) {
        setChecked(!isChecked(), notify);
    }
}
