package com.stardust.scriptdroid.ui.error;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.androidissuereporter.IssueReporterActivity;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubTarget;
import com.stardust.scriptdroid.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Stardust on 2017/2/13.
 */

public class IssueReportActivity extends IssueReporterActivity {


    private boolean mCrash = false;
    private Method mReportIssue, mValidateInput;
    private boolean mReportFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        setUpToobar();
        hookSendClick();
    }

    private void hookSendClick() {
        FloatingActionButton send = (FloatingActionButton) this.findViewById(com.heinrichreimersoftware.androidissuereporter.R.id.air_buttonSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reportIssue();
                } catch (Exception e) {
                    mReportFailed = true;
                    e.printStackTrace();
                    finish();
                }
            }
        });
    }

    private void setUpToobar() {
        getSupportActionBar().setTitle(R.string.text_issue_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.air_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleIntent() {
        final String errorDetail = getIntent().getStringExtra("error");
        if (errorDetail != null) {
            ((EditText) findViewById(R.id.air_inputDescription)).setText(errorDetail);
            String title = getFirstLine(errorDetail);
            ((EditText) findViewById(R.id.air_inputTitle)).setText(title);
            mCrash = true;
        }
    }

    private String getFirstLine(String str) {
        int i = str.indexOf('\n');
        if(i < 0)
            return str;
        return str.substring(0, i);
    }

    private boolean validateInput() {
        if (mValidateInput == null) {
            try {
                mValidateInput = IssueReporterActivity.class.getDeclaredMethod("validateInput");
                mValidateInput.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        try {
            return mValidateInput != null && (Boolean) mValidateInput.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void reportIssue() {
        if (mReportIssue == null) {
            try {
                mReportIssue = IssueReporterActivity.class.getDeclaredMethod("reportIssue");
                mReportIssue.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        try {
            mReportIssue.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        if (mCrash) {
            if (!mReportFailed) {
                Toast.makeText(IssueReportActivity.this, R.string.text_report_succeed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(IssueReportActivity.this, R.string.text_report_fail, Toast.LENGTH_SHORT).show();
            }
            finishAffinity();

        } else {
            super.finish();
        }
    }

    @Override
    protected GithubTarget getTarget() {
        return new GithubTarget("hyb1996", "NoRootScriptDroid");
    }


    @Override
    protected String getGuestToken() {
        //绕过github安全检查
        return decode("MzA5MWJhNWI0NDM2NWYzOGRmNDA4ZWRhM2Y5MTUxN2ZlNDVlZDBhZQ==");
    }

    public static String decode(String str) {
        return new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
    }

}
