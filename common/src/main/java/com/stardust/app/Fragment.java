package com.stardust.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.util.ViewUtil;

/**
 * Created by Stardust on 2017/1/30.
 */

public abstract class Fragment extends androidx.fragment.app.Fragment {

    private View mView;

    @NonNull
    public View getView() {
        return mView;
    }

    public <T extends View> T $(int id) {
        return ViewUtil.$(mView, id);
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
