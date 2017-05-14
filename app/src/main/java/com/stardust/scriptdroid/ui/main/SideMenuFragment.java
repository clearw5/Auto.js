package com.stardust.scriptdroid.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.Fragment;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.floating_window.FloatingWindowManger;
import com.stardust.scriptdroid.external.floating_window.menu.HoverMenuService;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.sublime_plugin_client.SublimePluginClient;
import com.stardust.scriptdroid.sublime_plugin_client.SublimePluginService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.tool.WifiTool;
import com.stardust.scriptdroid.ui.console.LogActivity;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.util.IntentUtil;
import com.stardust.util.UnderuseExecutors;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executor;


/**
 * Created by Stardust on 2017/1/30.
 */

public class SideMenuFragment extends Fragment {


    public static void setFragment(FragmentActivity activity, int viewId) {
        SideMenuFragment fragment = new SideMenuFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(viewId, fragment).commit();
    }

    private SwitchCompat mAccessibilityServiceSwitch, mFloatingWindowSwitch;
    private SwitchCompat mDebugSwith;
    private Executor mExecutor = UnderuseExecutors.getExecutor();

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
        mAccessibilityServiceSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final boolean checked = AccessibilityWatchDogService.isEnable();
                        mAccessibilityServiceSwitch.post(new Runnable() {
                            @Override
                            public void run() {
                                mAccessibilityServiceSwitch.setChecked(checked);
                            }
                        });
                    }
                });
            }
        }, 450);
        mFloatingWindowSwitch.setChecked(FloatingWindowManger.isHoverMenuShowing());
    }

    private void setUpSwitchCompat() {
        mAccessibilityServiceSwitch = $(R.id.sw_auto_operate_service);
        mFloatingWindowSwitch = $(R.id.sw_floating_window);
        mDebugSwith = $(R.id.sw_debug);
    }


    @ViewBinding.Click(R.id.console)
    private void startConsoleActivity() {
        startActivity(new Intent(getContext(), LogActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ViewBinding.Click(R.id.syntax_and_api)
    private void startSyntaxHelpActivity() {
        HelpCatalogueActivity.showMainCatalogue(getActivity());
    }

    @ViewBinding.Click(R.id.auto_operate_service)
    private void clickAutoOperateServiceSwitch() {
        mAccessibilityServiceSwitch.toggle();
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
        if (enable && !FloatingWindowManger.isHoverMenuShowing()) {
            FloatingWindowManger.showHoverMenu();
        } else if (!enable && FloatingWindowManger.isHoverMenuShowing()) {
            FloatingWindowManger.hideHoverMenu();
        }
    }

    @ViewBinding.Click(R.id.floating_window)
    private void toggleAssistServiceSwitch() {
        mFloatingWindowSwitch.toggle();
    }

    @ViewBinding.Click(R.id.debug)
    private void toggleDebugSwitch() {
        mDebugSwith.toggle();
    }

    @ViewBinding.Check(R.id.sw_debug)
    private void setDebugEnabled(boolean enabled) {
        if (enabled && !SublimePluginService.isConnected()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.text_server_address)
                    .input("", getServerAddress(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            Pref.saveServerAddress(input.toString());
                            SublimePluginService.connect(input.toString());
                        }
                    })
                    .neutralText(R.string.text_help)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            IntentUtil.browse(getActivity(), "https://github.com/hyb1996/AutoJs-Sublime-Plugin/blob/master/Readme.md");
                        }
                    })
                    .show();
        } else if (!enabled) {
            SublimePluginService.disconnectIfNeeded();
        }
    }

    private CharSequence getServerAddress() {
        return Pref.getServerAddressOrDefault(WifiTool.getWifiAddress(getActivity()));
    }

    @ViewBinding.Click(R.id.stop_all_running_scripts)
    private void stopAllRunningScripts() {
        int n = AutoJs.getInstance().getScriptEngineService().stopAll();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSublimeClientStateChange(SublimePluginClient.ConnectionStateChangeEvent event) {
        mDebugSwith.setChecked(event.isConnected());
        Toast.makeText(getActivity(), event.isConnected() ? R.string.text_connected : R.string.text_disconnected, Toast.LENGTH_SHORT).show();
    }

}
