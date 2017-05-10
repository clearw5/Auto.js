package com.stardust.scriptdroid.ui.settings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.automator.UiObject;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.IntentUtil;
import com.stardust.view.ViewBinding;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.view.ViewBinder;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

/**
 * Created by Stardust on 2017/2/2.
 */

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    private int mLolClickCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_about);
        setVersionName();
        setToolbarAsBack(getString(R.string.text_about));
        ViewBinder.bind(this);
    }

    @SuppressLint("SetTextI18n")
    private void setVersionName() {
        TextView version = $(R.id.version);
        version.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @ViewBinding.Click(R.id.github)
    private void openGitHub() {
        IntentTool.browse(this, getString(R.string.my_github));
    }

    @ViewBinding.Click(R.id.qq)
    private void openQQToChatWithMe() {
        String qq = getString(R.string.qq);
        if (!IntentUtil.chatWithQQ(this, qq)) {
            Toast.makeText(this, R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @ViewBinding.Click(R.id.email)
    private void openEmailToSendMe() {
        String email = getString(R.string.email);
        IntentUtil.sendMailTo(this, email);
    }


    @ViewBinding.Click(R.id.donate)
    private void showDonateMeDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.donate)
                .items("支付宝")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            if (AlipayZeroSdk.hasInstalledAlipayClient(AboutActivity.this)) {
                                AlipayZeroSdk.startAlipayClient(AboutActivity.this, "aex04370fwjf8angrv1te9e");
                            } else {
                                Toast.makeText(AboutActivity.this, "未安装支付宝", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .show();
    }

    @ViewBinding.Click(R.id.share)
    private void share() {
        IntentUtil.shareText(this, getString(R.string.share_app));
    }

    @ViewBinding.Click(R.id.icon)
    private void lol() {
        mLolClickCount++;
        Toast.makeText(this, R.string.text_lll, Toast.LENGTH_LONG).show();
        if (mLolClickCount >= 5) {
            crashTest();
        }
    }

    private void showEasterEgg() {
        new MaterialDialog.Builder(this)
                .customView(R.layout.paint_layout, false)
                .show();
    }

    private void uiObjectCreateTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    AccessibilityNodeInfo root = AccessibilityWatchDogService.getInstance().getRootInActiveWindow();
                    if (root != null) {
                        UiObject uiObject = UiObject.createRoot(root);
                        UiObject child = uiObject.child(0);
                        if (i % 1000 == 0) {
                            Log.v(TAG, String.valueOf(i));
                            Log.v(TAG, Runtime.getRuntime().totalMemory() + "/" + Runtime.getRuntime().maxMemory());
                            if (child != null)
                                Log.v(TAG, String.valueOf(child.getChildCount()));
                        }
                        if (child != null)
                            child.recycle();
                        i++;
                    }
                }
            }
        }).start();

    }

    private void crashTest() {
        new ThemeColorMaterialDialogBuilder(this)
                .title("Crash Test")
                .positiveText("Crash")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        throw new RuntimeException("Crash Test");
                    }
                }).show();
    }

    @ViewBinding.Click(R.id.developer)
    private void hhh() {
        Toast.makeText(this, R.string.text_it_is_the_developer_of_app, Toast.LENGTH_LONG).show();
    }


}
