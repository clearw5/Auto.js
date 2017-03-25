package com.stardust.scriptdroid.ui.main;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/3/23.
 */

public abstract class AppbarWithTabFragment extends BottomNavigationFragment {


    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_bar_tab, container, false);
        FrameLayout contentContainer = (FrameLayout) view.findViewById(R.id.content);
        contentContainer.addView(createContentView(inflater, contentContainer));
        return view;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    protected abstract View createContentView(LayoutInflater inflater, @Nullable ViewGroup container);
}
