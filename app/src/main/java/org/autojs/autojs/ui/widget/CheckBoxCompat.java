package org.autojs.autojs.ui.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Created by Stardust on 2017/10/19.
 */

public class CheckBoxCompat extends AppCompatCheckBox {
    private boolean mIgnoreCheckedChange;

    public CheckBoxCompat(Context context) {
        super(context);
    }

    public CheckBoxCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxCompat(Context context, AttributeSet attrs, int defStyleAttr) {
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
