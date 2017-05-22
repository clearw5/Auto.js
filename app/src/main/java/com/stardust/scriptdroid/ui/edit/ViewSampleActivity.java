package com.stardust.scriptdroid.ui.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.ui.EditorDelegate;
import com.jecelyin.editor.v2.view.EditorView;
import com.jecelyin.editor.v2.view.menu.MenuDef;
import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.sample.Sample;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.edit.editor920.Editor920Activity;
import com.stardust.scriptdroid.ui.edit.editor920.Editor920Utils;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.AssetsCache;
import com.stardust.util.SparseArrayEntries;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;
import com.stardust.widget.ToolbarMenuItem;


import static com.stardust.scriptdroid.script.Scripts.ACTION_ON_EXECUTION_FINISHED;
import static com.stardust.scriptdroid.script.Scripts.EXTRA_EXCEPTION_MESSAGE;


/**
 * Created by Stardust on 2017/4/29.
 */

public class ViewSampleActivity extends Editor920Activity implements OnActivityResultDelegate.DelegateHost {


    public static void view(Context context, Sample sample) {
        context.startActivity(new Intent(context, ViewSampleActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("sample", sample));
    }

    private View mView;
    private Sample mSample;
    private ScriptExecution mScriptExecution;
    private EditorDelegate mEditorDelegate;
    private SparseArray<ToolbarMenuItem> mMenuMap;
    private OnActivityResultDelegate.Mediator mMediator = new OnActivityResultDelegate.Mediator();
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ON_EXECUTION_FINISHED)) {
                mScriptExecution = null;
                setMenuStatus(R.id.run, MenuDef.STATUS_NORMAL);
                String msg = intent.getStringExtra(EXTRA_EXCEPTION_MESSAGE);
                if (msg != null) {
                    Snackbar.make(mView, getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setTheme(R.style.EditorTheme);
        mView = View.inflate(this, R.layout.activity_view_sample, null);
        setContentView(mView);
        handleIntent(getIntent());
        setUpUI();
        setUpEditor();
        registerReceiver(mOnRunFinishedReceiver, new IntentFilter(ACTION_ON_EXECUTION_FINISHED));
    }

    private void handleIntent(Intent intent) {
        mSample = (Sample) intent.getSerializableExtra("sample");
        String content = AssetsCache.get(this, mSample.path);
        mEditorDelegate = new EditorDelegate(0, mSample.name, content);
    }

    private void setUpUI() {
        ThemeColorManager.addActivityStatusBar(this);
        setUpToolbar();
        initMenuItem();
        ViewBinder.bind(this);
    }

    private void setUpEditor() {
        final EditorView editorView = (EditorView) findViewById(R.id.editor);
        mEditorDelegate.setEditorView(editorView);
        Editor920Utils.setLang(mEditorDelegate, "JavaScript");
        editorView.getEditText().setReadOnly(true);
        editorView.getEditText().setHorizontallyScrolling(true);
    }

    private void setUpToolbar() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mSample.name);
    }

    @ViewBinding.Click(R.id.run)
    private void run() {
        Snackbar.make(mView, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        setMenuStatus(R.id.run, MenuDef.STATUS_DISABLED);
        mScriptExecution = Scripts.runWithBroadcastSender(new StringScriptSource(mSample.name, mEditorDelegate.getText()));
    }

    private void initMenuItem() {
        mMenuMap = new SparseArrayEntries<ToolbarMenuItem>()
                .entry(R.id.run, (ToolbarMenuItem) findViewById(R.id.run))
                .sparseArray();
    }

    public void setMenuStatus(int menuResId, int status) {
        ToolbarMenuItem menuItem = mMenuMap.get(menuResId);
        if (menuItem == null)
            return;
        boolean disabled = status == MenuDef.STATUS_DISABLED;
        menuItem.setEnabled(!disabled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_console:
                showConsole();
                return true;
            case R.id.action_log:
                showLog();
                return true;
            case R.id.action_help:
                HelpCatalogueActivity.showMainCatalogue(this);
                return true;
            case R.id.action_import:
                MainActivity.importSample(this, mSample);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showLog() {
        AutoJs.getInstance().getScriptEngineService().getGlobalConsole().show();
    }

    private void showConsole() {
        if (mScriptExecution != null) {
            mScriptExecution.getRuntime().console.show();
        }
    }

    @Override
    public void doCommand(Command command) {
        mEditorDelegate.doCommand(command);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOnRunFinishedReceiver);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (RuntimeException e) {
            // FIXME: 2017/3/20
            e.printStackTrace();
        }
    }

    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mMediator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mMediator.onActivityResult(requestCode, resultCode, data);
    }
}
