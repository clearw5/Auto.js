package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/5/14.
 */

public class UI {

    private Context mContext;
    private JsLayoutInflater mJsLayoutInflater;
    private ExecutorService mExecutorService;

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

    public void runOnNonUiThread(Runnable action) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        mExecutorService.submit(action);
    }

    public View findViewByStringId(View view, String id) {
        return JsViewHelper.findViewByStringId(view, id);
    }
}
