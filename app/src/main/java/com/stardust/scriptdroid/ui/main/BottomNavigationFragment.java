package com.stardust.scriptdroid.ui.main;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.stardust.app.Fragment;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorMutable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/23.
 */

public abstract class BottomNavigationFragment extends Fragment {

    public static class Builder implements BottomNavigationBar.OnTabSelectedListener, ThemeColorMutable {
        private int mFragmentId;
        private BottomNavigationBar mBottomNavigationBar;
        private FragmentManager mFragmentManager;
        private List<BottomNavigationFragment> mFragments = new ArrayList<>();

        public Builder(int fragmentId, BottomNavigationBar bottomNavigationBar, FragmentManager fragmentManager) {
            mFragmentId = fragmentId;
            mBottomNavigationBar = bottomNavigationBar;
            mFragmentManager = fragmentManager;
            mBottomNavigationBar.setTabSelectedListener(this);
            ThemeColorManager.add(this);
        }

        public Builder add(BottomNavigationFragment fragment) {
            mFragments.add(fragment);
            mBottomNavigationBar.addItem(new BottomNavigationItem(fragment.getIconResId(), fragment.getTitle()));
            return this;
        }

        public void build() {
            mBottomNavigationBar.setFirstSelectedPosition(0);
            mBottomNavigationBar.initialise();
            mFragmentManager.beginTransaction().replace(mFragmentId, mFragments.get(0)).commit();
        }


        @Override
        public void onTabSelected(int position) {
            BottomNavigationFragment fragment = mFragments.get(position);
            mFragmentManager.beginTransaction().replace(mFragmentId, fragment).commit();
        }

        @Override
        public void onTabUnselected(int position) {

        }

        @Override
        public void onTabReselected(int position) {

        }

        @Override
        public void setThemeColor(ThemeColor themeColor) {
            mBottomNavigationBar.setActiveColor("#" + Integer.toHexString(themeColor.colorPrimary).substring(2));
        }
    }

    protected abstract String getTitle();

    protected abstract int getIconResId();


}
