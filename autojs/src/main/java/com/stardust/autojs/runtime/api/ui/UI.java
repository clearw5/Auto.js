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
        this(context, new ConvertLayoutInflater(context));
    }

    public JsLayoutInflater getLayoutInflater() {
        return mJsLayoutInflater;
    }

    public View inflate(String xml) {
        return mJsLayoutInflater.inflate(xml);
    }
}
