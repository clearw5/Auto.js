package com.stardust.scriptdroid.ui.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.script.sample.Sample;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.AssetsCache;
import com.stardust.util.SparseArrayEntries;
import com.stardust.widget.ToolbarMenuItem;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.stardust.scriptdroid.script.Scripts.ACTION_ON_EXECUTION_FINISHED;
import static com.stardust.scriptdroid.script.Scripts.EXTRA_EXCEPTION_MESSAGE;


/**
 * Created by Stardust on 2017/4/29.
 */
public class ViewSampleActivity extends AppCompatActivity implements OnActivityResultDelegate.DelegateHost {


    public static void view(Context context, Sample sample) {
        context.startActivity(new Intent(context, ViewSampleActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("sample", sample));
    }

    private View mView;
    private Sample mSample;
    private ScriptExecution mScriptExecution;
    private SparseArray<ToolbarMenuItem> mMenuMap;
    private OnActivityResultDelegate.Mediator mMediator = new OnActivityResultDelegate.Mediator();
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ON_EXECUTION_FINISHED)) {
                mScriptExecution = null;
                String msg = intent.getStringExtra(EXTRA_EXCEPTION_MESSAGE);
                if (msg != null) {
                    Snackbar.make(mView, getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    public void onCreate(Bundle b) {
        super.onCreate(b);
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
    }

    private void setUpUI() {
        ThemeColorManager.addActivityStatusBar(this);
        setUpToolbar();
        initMenuItem();
        ButterKnife.bind(this);
    }

    private void setUpEditor() {

    }

    private void setUpToolbar() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mSample.name);
    }

    @OnClick(R.id.run)
    void run() {
        Snackbar.make(mView, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        //mScriptExecution = Scripts.runWithBroadcastSender(new StringScriptSource(mSample.name, mEditorDelegate.getText()));
    }

    @OnClick(R.id.edit)
    void edit() {
        new ScriptOperations(this, mView)
                .importSample(mSample)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String path) throws Exception {
                        EditActivity.editFile(ViewSampleActivity.this, path);
                        finish();
                    }
                });
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
                new ScriptOperations(this, mView)
                        .importSample(mSample)
                        .subscribe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showLog() {
        AutoJs.getInstance().getScriptEngineService().getGlobalConsole().show();
    }

    private void showConsole() {
        if (mScriptExecution != null) {
            ((JavaScriptEngine) mScriptExecution.getEngine()).getRuntime().console.show();
        }
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
