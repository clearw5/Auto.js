package com.stardust.scriptdroid.ui.main.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stardust.scriptdroid.R;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by Stardust on 2017/8/25.
 */

public class DrawerMenuGroup extends LinearLayout {

    private TextView mTitle;

    public DrawerMenuGroup(Context context) {
        super(context);
        init(null);
    }

    public DrawerMenuGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawerMenuGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawerMenuGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(VERTICAL);
        addTitleView();
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawerMenuItem);
        mTitle.setText(a.getString(R.styleable.DrawerMenuItem_title));
        a.recycle();
    }

    private void addTitleView() {
        mTitle = new TextView(getContext());
        int padding = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        mTitle.setPadding(padding, padding, padding, padding);
        mTitle.setTextColor(0xff999999);
        mTitle.setTextSize(12);
        addView(mTitle, 0);
    }
}
