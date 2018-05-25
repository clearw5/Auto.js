package org.autojs.autojs.ui.log;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stardust.autojs.core.console.ConsoleView;
import com.stardust.autojs.core.console.StardustConsole;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.ui.BaseActivity;

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
        mConsoleView.findViewById(R.id.input_container).setVisibility(View.GONE);
    }

    @Click(R.id.fab)
    void clearConsole() {
        mStardustConsole.clear();
    }
}
