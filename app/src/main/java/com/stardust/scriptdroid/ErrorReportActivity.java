package com.stardust.scriptdroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/2/2.
 */

public class ErrorReportActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setUpUI();
            handleIntent();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handleIntent() {
        String message = getIntent().getStringExtra("message");
        final String errorDetail = getIntent().getStringExtra("error");
        new MaterialDialog.Builder(this)
                .content(message)
                .positiveText("退出")
                .negativeText("复制调试信息")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        exit();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        copyToClip(getDeviceMessage() + errorDetail);
                        exitAfter(1000);
                    }
                })
                .cancelable(false)
                .show();
    }

    private String getDeviceMessage() {
        return "Android: " + Build.VERSION.SDK_INT + "\n";
    }

    private void exitAfter(long millis) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                exit();
            }
        }, millis);
    }

    private void copyToClip(String text) {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText("Debug", text));
        Toast.makeText(ErrorReportActivity.this, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_error_report);
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_error_report));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void exit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }

    }

}


