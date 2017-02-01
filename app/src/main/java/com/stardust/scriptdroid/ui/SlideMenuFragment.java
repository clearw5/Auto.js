package com.stardust.scriptdroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.DocumentActivity;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by Stardust on 2017/1/30.
 */

public class SlideMenuFragment extends Fragment {


    public static void init(AppCompatActivity activity, int viewId) {
        SlideMenuFragment fragment = new SlideMenuFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(viewId, fragment).commit();
    }

    private SwitchCompat mAutoOperateServiceSwitch;

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slide_menu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAutoOperateServiceSwitch == null) {
            setUpSwitchCompat();
            ViewBinder.bind(this);
        }
        syncSwitchStatus();
    }

    private void syncSwitchStatus() {
        mAutoOperateServiceSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAutoOperateServiceSwitch.setChecked(ActionPerformService.isEnable());
            }
        }, 450);
    }

    private void setUpSwitchCompat() {
        mAutoOperateServiceSwitch = $(R.id.sw_auto_operate_service);
        mAutoOperateServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !ActionPerformService.isEnable()) {
                    AccessibilityServiceUtils.goToPermissionSetting(getContext());
                } else if (!isChecked && ActionPerformService.isEnable()) {
                    ActionPerformService.disable();
                }
            }
        });
    }

    @ViewBinding.Click(R.id.licenses)
    private void showLicenseDialog() {
        new LicensesDialog.Builder(getActivity())
                .setNotices(R.raw.licenses)
                .setIncludeOwnLicense(true)
                .build()
                .showAppCompat();
    }

    @ViewBinding.Click(R.id.stop_all_running_scripts)
    private void stopAllRunningScripts() {
        int n = Droid.getInstance().stopAll();
        if (n > 0)
            Snackbar.make(getActivityContentView(), "已停止" + n + "个正在运行的脚本", Snackbar.LENGTH_SHORT).show();
        else
            Snackbar.make(getActivityContentView(), "没有正在运行的脚本", Snackbar.LENGTH_SHORT).show();
    }

    @ViewBinding.Click(R.id.syntax_and_api)
    private void startSyntaxHelpActivity() {
        startActivity(new Intent(getContext(), DocumentActivity.class));
    }

    @ViewBinding.Click(R.id.about_app)
    private void startAboutActivity() {
        // TODO: 2017/1/30 startAboutActivity
        Toast.makeText(getContext(), "暂无", Toast.LENGTH_LONG).show();
    }

    @ViewBinding.Click(R.id.auto_operate_service)
    private void clickAutoOperateServiceSwitch() {
        mAutoOperateServiceSwitch.setChecked(!mAutoOperateServiceSwitch.isChecked());
    }
}
