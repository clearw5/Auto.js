package com.stardust.scriptdroid.ui.main.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/8/25.
 */
public class DrawerMenuItem extends FrameLayout {

    private SwitchCompat mSwitchCompat;

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
        ImageView icon = (ImageView) findViewById(R.id.icon);
        TextView title = (TextView) findViewById(R.id.title);
        mSwitchCompat = (SwitchCompat) findViewById(R.id.sw);
        if (attrs == null)
            return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawerMenuItem);
        icon.setImageResource(a.getResourceId(R.styleable.DrawerMenuItem_icon, 0));
        title.setText(a.getString(R.styleable.DrawerMenuItem_title));
        if (a.getBoolean(R.styleable.DrawerMenuItem_with_switch, false)) {
            mSwitchCompat.setVisibility(VISIBLE);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwitchCompat.toggle();
                }
            });
        }
        a.recycle();
    }

    public SwitchCompat getSwitchCompat() {
        return mSwitchCompat;
    }

}
