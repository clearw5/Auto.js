package com.stardust.scriptdroid.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.external.floating_window.FloatingWindowManger;
import com.stardust.scriptdroid.external.floating_window.HoverMenuService;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.ui.console.ConsoleActivity;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by Stardust on 2017/1/30.
 */

public class SlideMenuFragment extends Fragment {


    public static void setFragment(AppCompatActivity activity, int viewId) {
        SlideMenuFragment fragment = new SlideMenuFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(viewId, fragment).commit();
    }

    private SwitchCompat mAutoOperateServiceSwitch, mFloatingWindowSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slide_menu, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUpSwitchCompat();
        ViewBinder.bind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        syncSwitchState();
    }


    private void syncSwitchState() {
        mAutoOperateServiceSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAutoOperateServiceSwitch.setChecked(AccessibilityWatchDogService.isEnable());
            }
        }, 450);
        mFloatingWindowSwitch.setChecked(FloatingWindowManger.isFloatingWindowShowing());
    }

    private void setUpSwitchCompat() {
        mAutoOperateServiceSwitch = $(R.id.sw_auto_operate_service);
        mFloatingWindowSwitch = $(R.id.sw_floating_window);
    }


    @ViewBinding.Click(R.id.console)
    private void startConsoleActivity() {
        startActivity(new Intent(getContext(), ConsoleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ViewBinding.Click(R.id.syntax_and_api)
    private void startSyntaxHelpActivity() {
        HelpCatalogueActivity.showMainCatalogue(getActivity());
    }

    @ViewBinding.Click(R.id.auto_operate_service)
    private void clickAutoOperateServiceSwitch() {
        mAutoOperateServiceSwitch.toggle();
    }

    @ViewBinding.Check(R.id.sw_auto_operate_service)
    private void setAutoOperateServiceEnable(boolean enable) {
        if (enable && !AccessibilityWatchDogService.isEnable()) {
            AccessibilityServiceTool.enableAccessibilityService();
        } else if (!enable && AccessibilityWatchDogService.isEnable()) {
            AccessibilityWatchDogService.disable();
        }
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


    @ViewBinding.Click(R.id.stop_all_running_scripts)
    private void stopAllRunningScripts() {
        int n = Droid.getInstance().stopAll();
        if (n > 0)
            Snackbar.make(getView(), String.format(getString(R.string.text_already_stop_n_scripts), n), Snackbar.LENGTH_SHORT).show();
        else
            Snackbar.make(getView(), R.string.text_no_running_script, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onHoverMenuServiceStateChanged(HoverMenuService.ServiceStateChangedEvent event) {
        mFloatingWindowSwitch.setChecked(event.state);
    }

}
