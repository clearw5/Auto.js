package com.stardust.scriptdroid.external.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.widget.ToolbarMenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/4/5.
 */
@EActivity(R.layout.activity_tasker_script_edit)
public class TaskerScriptEditActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = "Love you. Can we go back?".hashCode() >> 16;
    public static final String EXTRA_CONTENT = "Still Love Eating 17.4.5";

    public static void edit(Activity activity, String title, String summary, String content) {
        activity.startActivityForResult(new Intent(activity, TaskerScriptEditActivity_.class)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra("summary", summary)
                .putExtra("title", title), REQUEST_CODE);
    }

    private String mTitle, mSummary;
    @ViewById(R.id.redo)
    ToolbarMenuItem mRedo;
    @ViewById(R.id.undo)
    ToolbarMenuItem mUndo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTheme(R.style.EditorTheme);
        handleIntent(getIntent());
    }

    @AfterViews
    void setUpViews() {
        ((TextView) findViewById(R.id.summary)).setText(mSummary);
        ThemeColorManager.addActivityStatusBar(this);
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mTitle);
        setUpEditor();
    }

    private void handleIntent(Intent intent) {
        mTitle = intent.getStringExtra("title");
        mSummary = intent.getStringExtra("summary");
        String content = intent.getStringExtra(EXTRA_CONTENT);
    }

    private void setUpEditor() {

    }

    @Override
    public void finish() {
        super.finish();
    }

    public void setMenuStatus(int menuResId, int status) {

    }
}
