package com.stardust.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.scriptdroid.tool.ViewTool;

/**
 * Created by Stardust on 2017/1/30.
 */

public abstract class Fragment extends android.support.v4.app.Fragment {

    private View mView;

    public View getView() {
        return mView;
    }

    public <T extends View> T $(int id) {
        return ViewTool.$(mView, id);
    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    public View getActivityContentView() {
        return getActivity().getWindow().getDecorView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = createView(inflater, container, savedInstanceState);
        return mView;
    }

    @Nullable
    public abstract View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

}
