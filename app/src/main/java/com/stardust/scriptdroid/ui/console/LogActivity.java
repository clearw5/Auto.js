package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.stardust.scriptdroid.ui.BaseActivity;
import com.jraska.console.Console;
import com.stardust.scriptdroid.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Stardust on 2017/2/12.
 */
@EActivity(R.layout.activity_console)
public class LogActivity extends BaseActivity {

    @AfterViews
    void setUpUI() {
        setToolbarAsBack(getString(R.string.text_log));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_console, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Console.clear();
        return super.onOptionsItemSelected(item);
    }

}
