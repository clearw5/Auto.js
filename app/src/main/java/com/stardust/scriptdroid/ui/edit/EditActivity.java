package com.stardust.scriptdroid.ui.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.edit.completion.CodeCompletions;
import com.stardust.scriptdroid.ui.edit.completion.InputMethodEnhanceBar;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/1/29.
 */
@EActivity(R.layout.activity_edit)
public class EditActivity extends AppCompatActivity implements OnActivityResultDelegate.DelegateHost {

    public static final String EXTRA_PATH = "Still Love Eating 17.4.5";
    private static final String EXTRA_NAME = "Still love you 17.6.29 But....(ಥ_ಥ)";
    private static final String KEY_EDITOR_THEME = "I really really really really love you dee";


    @ViewById(R.id.content_view)
    View mView;

    @ViewById(R.id.editor)
    CodeMirrorEditor mEditor;

    @ViewById(R.id.input_method_enhance_bar)
    InputMethodEnhanceBar mInputMethodEnhanceBar;

    private String mName;
    private File mFile;
    private boolean mReadOnly = false;
    private OnActivityResultDelegate.Mediator mActivityResultMediator = new OnActivityResultDelegate.Mediator();
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Scripts.ACTION_ON_EXECUTION_FINISHED)) {
                mScriptExecution = null;
                setMenuItemStatus(R.id.run, true);
                String msg = intent.getStringExtra(Scripts.EXTRA_EXCEPTION_MESSAGE);
                if (msg != null) {
                    Snackbar.make(mView, getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };


    private ScriptExecution mScriptExecution;
    private boolean mTextChanged = false;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        registerReceiver(mOnRunFinishedReceiver, new IntentFilter(Scripts.ACTION_ON_EXECUTION_FINISHED));
    }

    public static void editFile(Context context, String path) {
        editFile(context, null, path);
    }

    public static void editFile(Context context, String name, String path) {
        context.startActivity(new Intent(context, EditActivity_.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NAME, name));
    }

    public static void editFile(Context context, ScriptFile file) {
        editFile(context, file.getSimplifiedName(), file.getPath());
    }

    @AfterViews
    void setUpViews() {
        ThemeColorManager.addActivityStatusBar(this);
        setUpToolbar();
        setUpEditor();
        handleIntent(getIntent());
        setMenuItemStatus(R.id.save, false);
    }

    private void handleIntent(Intent intent) {
        String path = intent.getStringExtra(EXTRA_PATH);
        mName = intent.getStringExtra(EXTRA_NAME);
        mReadOnly = intent.getBooleanExtra("readOnly", false);
        boolean saveEnabled = intent.getBooleanExtra("saveEnabled", true);
        if (mReadOnly || !saveEnabled) {
            findViewById(R.id.save).setVisibility(View.GONE);
        }
        String content = intent.getStringExtra("content");
        if (content != null) {
            mEditor.setText(content);
        } else {
            mFile = new File(path);
            if (mName == null) {
                mName = mFile.getName();
            }
            mEditor.setText(PFile.read(mFile));
        }
    }

    private void setUpEditor() {
        mEditor.setTheme(PreferenceManager.getDefaultSharedPreferences(EditActivity.this)
                .getString(KEY_EDITOR_THEME, mEditor.getTheme()));
        mEditor.setCallback(new CodeMirrorEditor.Callback() {
            @Override
            public void onChange() {
                mTextChanged = true;
                setMenuItemStatus(R.id.save, true);
            }

            @Override
            public void updateCodeCompletion(int fromLine, int fromCh, int toLine, int toCh, final String[] list) {
                mInputMethodEnhanceBar.setCodeCompletions(new CodeCompletions(
                        new CodeCompletions.Pos(fromLine, fromCh),
                        new CodeCompletions.Pos(toLine, toCh),
                        Arrays.asList(list)
                ));
            }
        });
        mInputMethodEnhanceBar.setOnHintClickListener(new InputMethodEnhanceBar.OnHintClickListener() {
            @Override
            public void onHintClick(CodeCompletions completions, int pos) {
                mEditor.replace(completions.getHints().get(pos), completions.getFrom().line, completions.getFrom().ch,
                        completions.getTo().line, completions.getTo().ch);
            }
        });

    }

    private void setUpToolbar() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mName);
    }

    @Click(R.id.run)
    void runAndSaveFileIFNeeded() {
        save().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        run();
                    }
                });
    }

    private void run() {
        Snackbar.make(mView, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        mScriptExecution = Scripts.runWithBroadcastSender(new JavaScriptFileSource(mName, mFile), mFile.getParent());
        setMenuItemStatus(R.id.run, false);
    }


    @Click(R.id.undo)
    void undo() {
        mEditor.undo();
    }

    @Click(R.id.redo)
    void redo() {
        mEditor.redo();
    }

    public Observable<String> save() {
        return mEditor.getText()
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        PFile.write(mFile, s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        mTextChanged = false;
                        setMenuItemStatus(R.id.save, false);
                    }
                });
    }


    @Click(R.id.save)
    void saveFile() {
        save().subscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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
            case R.id.action_editor_theme:
                selectEditorTheme();
                return true;
            case R.id.action_beautify:
                beautifyCode();
                return true;
            case R.id.action_open_by_other_apps:
                openByOtherApps();
                return true;
            case R.id.action_force_stop:
                forceStop();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMenuItemStatus(int id, boolean enabled) {
        findViewById(id).setEnabled(enabled);
    }

    private void showLog() {
        AutoJs.getInstance().getScriptEngineService().getGlobalConsole().show();
    }

    private void showConsole() {
        if (mScriptExecution != null) {
            ((JavaScriptEngine) mScriptExecution.getEngine()).getRuntime().console.show();
        }
    }


    private void selectEditorTheme() {
        String[] themes = mEditor.getAvailableThemes();
        int i = Arrays.asList(themes).indexOf(mEditor.getTheme());
        new MaterialDialog.Builder(this)
                .items((CharSequence[]) themes)
                .itemsCallbackSingleChoice(i, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        PreferenceManager.getDefaultSharedPreferences(EditActivity.this).edit()
                                .putString(KEY_EDITOR_THEME, text.toString())
                                .apply();
                        mEditor.setTheme(text.toString());
                        return true;
                    }
                })
                .show();
    }


    private void forceStop() {
        if (mScriptExecution != null) {
            mScriptExecution.getEngine().forceStop();
        }
    }

    private void openByOtherApps() {
        if (mFile != null)
            Scripts.openByOtherApps(mFile);
    }

    private void beautifyCode() {
        mEditor.beautifyCode();
    }

    @Override
    public void finish() {
        if (mTextChanged) {
            showExitConfirmDialog();
            return;
        }
        super.finish();
    }

    private void showExitConfirmDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_alert)
                .content(R.string.edit_exit_without_save_warn)
                .positiveText(R.string.text_cancel)
                .negativeText(R.string.text_save_and_exit)
                .neutralText(R.string.text_exit_directly)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        saveFile();
                        EditActivity.super.finish();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditActivity.super.finish();
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityResultMediator.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mActivityResultMediator;
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
}
