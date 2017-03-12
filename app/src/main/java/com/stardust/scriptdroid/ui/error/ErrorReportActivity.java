package com.stardust.scriptdroid.ui.error;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.scriptdroid.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/2/2.
 */

public class ErrorReportActivity extends BaseActivity {

    private static final String TAG = "ErrorReportActivity";

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setUpUI();
            handleIntent();
        } catch (Throwable throwable) {
            Log.e(TAG, "", throwable);
            exit();
        }
    }


    private void handleIntent() {
        String message = getIntent().getStringExtra("message");
        final String errorDetail = getIntent().getStringExtra("error");
        showErrorMessageByDialog(message, errorDetail);
        //showErrorMessage(message, errorDetail);
    }

    private void showErrorMessageByDialog(String message, final String errorDetail) {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_crash)
                .content(R.string.crash_feedback)
                .positiveText(R.string.text_exit)
                .neutralText(R.string.text_copy_debug_info)
                .negativeText(R.string.text_report_bug)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        exit();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        copyToClip(getDeviceMessage() + errorDetail);
                        exitAfter(1000);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startIssueReportActivity();
                        finish();
                    }
                })
                .cancelable(false)
                .show();
    }

    private void startIssueReportActivity() {
        Intent intent = new Intent(this, IssueReportActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    private void showErrorMessage(String message, String errorDetail) {
        ((TextView) findViewById(R.id.error)).setText(message + "\n" + errorDetail);
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
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_error_report));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        finishAffinity();
    }

}


