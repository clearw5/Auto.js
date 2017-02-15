package com.stardust.scriptdroid.ui.settings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

/**
 * Created by Stardust on 2017/2/2.
 */

public class AboutActivity extends BaseActivity {

    private int mLolClickCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_about);
        setVersionName();
        setUpToolbar();
        ViewBinder.bind(this);
    }

    @SuppressLint("SetTextI18n")
    private void setVersionName() {
        TextView version = $(R.id.version);
        version.setText("Version " + BuildConfig.VERSION_NAME);
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @ViewBinding.Click(R.id.github)
    private void openGitHub() {
        IntentTool.goToLink(this, getString(R.string.my_github));
    }

    @ViewBinding.Click(R.id.qq)
    private void openQQToChatWithMe() {
        Toast.makeText(this, R.string.text_qq_group_id_copied, Toast.LENGTH_SHORT).show();
        String qq = getString(R.string.qq);
        IntentTool.goToQQ(this, qq);
    }

    @ViewBinding.Click(R.id.email)
    private void openEmailToSendMe() {
        String email = getString(R.string.email);
        IntentTool.goToMail(this, email);
    }


    @ViewBinding.Click(R.id.donate)
    private void showDonateMeDialog() {
        new MaterialDialog.Builder(this)
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
        IntentTool.shareText(this, getString(R.string.share_app));
    }

    @ViewBinding.Click(R.id.icon)
    private void lol() {
        mLolClickCount++;
        Toast.makeText(this, R.string.text_lll, Toast.LENGTH_LONG).show();
        if (mLolClickCount >= 5) {
            new MaterialDialog.Builder(this)
                    .title("Crash Test")
                    .positiveText("Crash")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            throw new RuntimeException("Crash Test");
                        }
                    }).show();
        }
    }

    @ViewBinding.Click(R.id.developer)
    private void hhh() {
        Toast.makeText(this, R.string.text_it_is_the_developer_of_app, Toast.LENGTH_LONG).show();
    }

    private void setClipText(CharSequence label, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
        Toast.makeText(this, label + getString(R.string.text_already_copy_to_clip), Toast.LENGTH_SHORT).show();
    }

}
