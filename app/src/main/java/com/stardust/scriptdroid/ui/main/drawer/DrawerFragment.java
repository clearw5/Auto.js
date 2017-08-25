package com.stardust.scriptdroid.ui.main.drawer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.HoverMenuManger;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.ui.common.ProgressDialog;
import com.stardust.theme.ThemeColorManager;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.sublime.SublimePluginClient;
import com.stardust.scriptdroid.sublime.SublimePluginService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.tool.WifiTool;
import com.stardust.util.IntentUtil;
import com.stardust.util.UnderuseExecutors;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executor;


/**
 * Created by Stardust on 2017/1/30.
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends android.support.v4.app.Fragment {

    SwitchCompat mAccessibilityServiceSwitch;
    SwitchCompat mFloatingWindowSwitch;
    SwitchCompat mDebugSwitch;
    @ViewById(R.id.header)
    View mHeaderView;
    private Executor mExecutor = UnderuseExecutors.getExecutor();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //syncSwitchState();
    }

    @AfterViews
    void setUpViews(){
        ThemeColorManager.addViewBackground(mHeaderView);
    }


    private void syncSwitchState() {
        mAccessibilityServiceSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mAccessibilityServiceSwitch == null) {
                            return;
                        }
                        final boolean checked = AccessibilityServiceTool.isAccessibilityServiceEnabled(App.getApp());
                        mAccessibilityServiceSwitch.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mAccessibilityServiceSwitch == null) {
                                    return;
                                }
                                mAccessibilityServiceSwitch.setChecked(checked);
                            }
                        });
                    }
                });
            }
        }, 450);
        mFloatingWindowSwitch.setChecked(HoverMenuManger.isHoverMenuShowing());
    }

    void setAutoOperateServiceEnable(CompoundButton button, boolean enable) {
        boolean isAccessibilityServiceEnabled = AccessibilityServiceTool.isAccessibilityServiceEnabled(App.getApp());
        if (enable && !isAccessibilityServiceEnabled) {
            enableAccessibilityService();
        } else if (!enable && isAccessibilityServiceEnabled) {
            if (!AccessibilityService.disable()) {
                AccessibilityServiceTool.goToAccessibilitySetting();
            }
        }
    }

    private void enableAccessibilityService() {
        if (!Pref.enableAccessibilityServiceByRoot()) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            return;
        }
        enableAccessibilityServiceByRoot();

    }

    private void enableAccessibilityServiceByRoot() {
        final ProgressDialog progress = new ProgressDialog(getContext(), R.string.text_enable_accessibility_service_by_root_ing);
        UnderuseExecutors.execute(new Runnable() {
            @Override
            public void run() {
                final boolean succeed = AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(4000);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!succeed) {
                            Toast.makeText(getContext(), R.string.text_enable_accessibitliy_service_by_root_failed, Toast.LENGTH_SHORT).show();
                            AccessibilityServiceTool.goToAccessibilitySetting();
                        }
                        progress.dismiss();
                    }
                });
            }
        });
    }

    void setFloatingWindowEnable(CompoundButton button, boolean enable) {
        if (enable && !HoverMenuManger.isHoverMenuShowing()) {
            HoverMenuManger.showHoverMenu();
            if (Pref.enableAccessibilityServiceByRoot()) {
                enableAccessibilityServiceByRoot();
            }
        } else if (!enable && HoverMenuManger.isHoverMenuShowing()) {
            HoverMenuManger.hideHoverMenu();
        }
    }

    void setDebugEnabled(CompoundButton button, boolean enabled) {
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
        mDebugSwitch.setChecked(event.isConnected());
        App.getApp().getUiHandler().toast(event.isConnected() ? R.string.text_connected : R.string.text_disconnected);
    }

}
