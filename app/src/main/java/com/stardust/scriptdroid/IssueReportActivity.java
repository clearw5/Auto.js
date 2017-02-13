package com.stardust.scriptdroid;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.heinrichreimersoftware.androidissuereporter.IssueReporterActivity;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubTarget;
import com.stardust.scriptdroid.R;

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
        Toolbar toolbar = (Toolbar) this.findViewById(com.heinrichreimersoftware.androidissuereporter.R.id.air_toolbar);
        toolbar.setTitle(R.string.text_report_bug);
    }

    private void handleIntent() {
        final String errorDetail = getIntent().getStringExtra("error");
        ((EditText) findViewById(R.id.air_inputDescription)).setText(errorDetail);
    }

    @Override
    protected GithubTarget getTarget() {
        return new GithubTarget("hyb1996", "NoRootScriptDroid");
    }

    @Override
    protected String getGuestToken() {
        return "7860bbb3897c8b2bb29a49a0820dd626bd638b1c";
    }

}
