package com.stardust.scriptdroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.androidissuereporter.IssueReporterActivity;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubTarget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Stardust on 2017/2/13.
 */

public class IssueReportActivity extends IssueReporterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        findViewById(com.heinrichreimersoftware.androidissuereporter.R.id.air_optionAnonymous).performClick();
        ((EditText) findViewById(R.id.air_inputTitle)).setText(R.string.text_bug_report);
        getSupportActionBar().setTitle(R.string.text_report_bug);
        FloatingActionButton send = (FloatingActionButton) this.findViewById(com.heinrichreimersoftware.androidissuereporter.R.id.air_buttonSend);
        try {
            final Method reportIssue = IssueReporterActivity.class.getDeclaredMethod("reportIssue");
            reportIssue.setAccessible(true);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        reportIssue.invoke(IssueReportActivity.this);
                        Toast.makeText(IssueReportActivity.this, R.string.text_report_succeed, Toast.LENGTH_SHORT).show();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        Toast.makeText(IssueReportActivity.this, R.string.text_report_fail, Toast.LENGTH_SHORT).show();
                    }
                    exit();
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }

    private void handleIntent() {
        final String errorDetail = getIntent().getStringExtra("error");
        ((EditText) findViewById(R.id.air_inputDescription)).setText(errorDetail);
    }

    private void exit() {
        finishAffinity();
    }

    @Override
    protected GithubTarget getTarget() {
        return new GithubTarget("hyb1996", "NoRootScriptDroid");
    }

    @Override
    protected String getGuestToken() {
        return "cd403d68a9f3a3590a14408d055c55180e7af7d3";
    }

}
