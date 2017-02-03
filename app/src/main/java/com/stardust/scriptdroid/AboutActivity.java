package com.stardust.scriptdroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

/**
 * Created by Stardust on 2017/2/2.
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_about);
        setUpToolbar();
        ViewBinder.bind(this);
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

    }

    @ViewBinding.Click(R.id.share)
    private void share() {
        IntentTool.shareText(this, getString(R.string.share_app));
    }

    @ViewBinding.Click(R.id.icon)
    private void lol() {
        Toast.makeText(this, "略略略", Toast.LENGTH_LONG).show();
    }

    @ViewBinding.Click(R.id.developer)
    private void hhh() {
        Toast.makeText(this, "这是软件开发者(。・・)ノ", Toast.LENGTH_LONG).show();
    }

    private void setClipText(CharSequence label, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
        Toast.makeText(this, label + getString(R.string.text_already_copy_to_clip), Toast.LENGTH_SHORT).show();
    }

}
