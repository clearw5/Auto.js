package com.stardust.scriptdroid.ui.log;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stardust.autojs.core.console.ConsoleView;
import com.stardust.autojs.core.console.StardustConsole;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.ui.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_log)
public class LogActivity extends BaseActivity {

    @ViewById(R.id.console)
    ConsoleView mConsoleView;

    private StardustConsole mStardustConsole;

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_log));
        mStardustConsole = (StardustConsole) AutoJs.getInstance().getGlobalConsole();
        mConsoleView.setConsole(mStardustConsole);
    }

    @Click(R.id.fab)
    void clearConsole() {
        mStardustConsole.clear();
    }
}
