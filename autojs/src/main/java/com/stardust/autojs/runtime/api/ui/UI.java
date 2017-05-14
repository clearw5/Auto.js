package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Stardust on 2017/5/14.
 */

public class UI {

    private Context mContext;
    private JsLayoutInflater mJsLayoutInflater;

    public UI(Context context, JsLayoutInflater layoutInflater) {
        mContext = context;
        mJsLayoutInflater = layoutInflater;
    }


    public UI(Context context) {
        this(context, new ConvertLayoutInflater());
    }

    public JsLayoutInflater getLayoutInflater() {
        return mJsLayoutInflater;
    }

    public View inflate(Context context, String xml) {
        return mJsLayoutInflater.inflate(context, xml);
    }
}
