package com.stardust.scriptdroid.ui.edit.sidemenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.external.floating_window.FloatingWindowManger;
import com.stardust.view.ViewBinding;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.console.ConsoleActivity;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.view.ViewBinder;

/**
 * Created by Stardust on 2017/2/4.
 */

public class EditSideMenuFragment extends Fragment {


    private FunctionListRecyclerView.OnFunctionClickListener mOnFunctionClickListener;

    public static EditSideMenuFragment setFragment(AppCompatActivity activity, int viewId) {
        EditSideMenuFragment fragment = new EditSideMenuFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(viewId, fragment).commit();
        return fragment;
    }

    private SwitchCompat mFloatingWindowSwitch;

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_side_menu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFloatingWindowSwitch == null) {
            setUpUI();
        }
        syncSwitchState();
    }

    private void setUpUI() {
        setUpSwitchCompat();
        setUpFunctionList();
        ViewBinder.bind(this);
    }


    private void setUpFunctionList() {
        FunctionListRecyclerView functionListRecyclerView = $(R.id.function_list);
        functionListRecyclerView.setOnFunctionClickListener(mOnFunctionClickListener);

    }

    private void syncSwitchState() {
        mFloatingWindowSwitch.setChecked(FloatingWindowManger.isFloatingWindowShowing());
    }

    private void setUpSwitchCompat() {
        mFloatingWindowSwitch = $(R.id.sw_floating_window);
    }

    @ViewBinding.Click(R.id.syntax_and_api)
    private void startSyntaxHelpActivity() {
        HelpCatalogueActivity.showCatalogue(getActivity());
    }

    @ViewBinding.Click(R.id.console)
    private void startConsoleActivity() {
        startActivity(new Intent(getContext(), ConsoleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ViewBinding.Check(R.id.sw_floating_window)
    private void setFloatingWindowEnable(boolean enable) {
        if (enable && !FloatingWindowManger.isFloatingWindowShowing()) {
            FloatingWindowManger.showFloatingWindow();
        } else if (!enable && FloatingWindowManger.isFloatingWindowShowing()) {
            FloatingWindowManger.hideFloatingWindow();
        }
    }

    @ViewBinding.Click(R.id.floating_window)
    private void toggleAssistServiceSwitch() {
        mFloatingWindowSwitch.toggle();
    }

    public EditSideMenuFragment setOnFunctionClickListener(FunctionListRecyclerView.OnFunctionClickListener onFunctionClickListener) {
        mOnFunctionClickListener = onFunctionClickListener;
        return this;
    }

}
