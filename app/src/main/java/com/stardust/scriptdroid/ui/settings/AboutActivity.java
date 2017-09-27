package com.stardust.scriptdroid.ui.settings;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.IntentUtil;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    @ViewById(R.id.version)
    TextView mVersion;

    private int mLolClickCount = 0;


    @AfterViews
    void setUpViews() {
        setVersionName();
        setToolbarAsBack(getString(R.string.text_about));
    }

    @SuppressLint("SetTextI18n")
    private void setVersionName() {
        mVersion.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Click(R.id.github)
    void openGitHub() {
        IntentTool.browse(this, getString(R.string.my_github));
    }

    @Click(R.id.qq)
    void openQQToChatWithMe() {
        String qq = getString(R.string.qq);
        if (!IntentUtil.chatWithQQ(this, qq)) {
            Toast.makeText(this, R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.email)
    void openEmailToSendMe() {
        String email = getString(R.string.email);
        IntentUtil.sendMailTo(this, email);
    }


    @Click(R.id.donate)
    void showDonateMeDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_donate)
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

    @Click(R.id.share)
    void share() {
        IntentUtil.shareText(this, getString(R.string.share_app));
    }

    @Click(R.id.icon)
    void lol() {
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

    @Click(R.id.developer)
    void hhh() {
        Toast.makeText(this, R.string.text_it_is_the_developer_of_app, Toast.LENGTH_LONG).show();
    }


}
