package com.stardust.scriptdroid.ui.edit.sidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/4/18.
 */

public class HelpSideMenuFragment extends Fragment {


    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_side_menu, container, false);
    }
}
