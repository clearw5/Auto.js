package org.autojs.autojs.ui.error;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/2/2.
 */

public class ErrorReportActivity extends BaseActivity {

    private static final String TAG = "ErrorReportActivity";
    private static final SparseIntArray CRASH_COUNT = new SparseIntArray();
    private static final String KEY_CRASH_COUNT = "crashCount";

    static {
        CRASH_COUNT.put(2, R.string.text_again);
        CRASH_COUNT.put(3, R.string.text_again_and_again);
        CRASH_COUNT.put(4, R.string.text_again_and_again_again);
        CRASH_COUNT.put(5, R.string.text_again_and_again_again_again);
    }

    private String mTitle;

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            mTitle = getCrashCountText() + getString(R.string.text_crash);
            setUpUI();
            handleIntent();
        } catch (Throwable throwable) {
            Log.e(TAG, "", throwable);
            exit();
        }

    }

    private String getCrashCountText() {
        int i = PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_CRASH_COUNT, 0);
        i++;
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_CRASH_COUNT, i).apply();
        if (i < 2)
            return "";
        if (i > 5)
            i = 5;
        return getString(CRASH_COUNT.get(i));
    }


    private void handleIntent() {
        String message = getIntent().getStringExtra("message");
        final String errorDetail = getIntent().getStringExtra("error");
        showErrorMessageByDialog(message, errorDetail);
        //showErrorMessage(message, errorDetail);
    }

    private void showErrorMessageByDialog(String message, final String errorDetail) {
        new ThemeColorMaterialDialogBuilder(this)
                .title(mTitle)
                .content(R.string.crash_feedback)
                .positiveText(R.string.text_exit)
                .negativeText(R.string.text_copy_debug_info)
                .onPositive((dialog, which) -> exit())
                .onNegative((dialog, which) -> {
                    copyToClip(getDeviceMessage() + message + "\n" + errorDetail);
                    exitAfter(1000);
                })
                .cancelable(false)
                .show();
    }

    private String getDeviceMessage() {
        return String.format(Locale.getDefault(), "Version: %s\nAndroid: %d\n", BuildConfig.VERSION_CODE, Build.VERSION.SDK_INT);
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


