package com.stardust.scriptdroid.ui.main.community;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;

/**
 * Created by Stardust on 2017/8/22.
 */

public class CommunityFragment extends Fragment implements ViewPagerFragment {
    @Override
    public void setUpWithFab(ViewPager pager, FloatingActionButton fab) {
        if (fab.getVisibility() != View.VISIBLE) {
            fab.show();
            return;
        }
        fab.animate()
                .rotation(0)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }
}
