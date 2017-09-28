package com.stardust.scriptdroid.ui.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.ui.edit.completion.CodeCompletions;
import com.stardust.scriptdroid.ui.edit.completion.InputMethodEnhanceBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.stardust.scriptdroid.script.Scripts.ACTION_ON_EXECUTION_FINISHED;

/**
 * Created by Stardust on 2017/9/28.
 */
@EViewGroup(R.layout.editor_view)
public class EditorView extends FrameLayout {

    public static final String EXTRA_PATH = "Still Love Eating 17.4.5";
    public static final String EXTRA_NAME = "Still love you 17.6.29 But....(ಥ_ಥ)";
    public static final String EXTRA_CONTENT = "It's hard...............";
    public static final String EXTRA_READ_ONLY = "Miss you more every day、、、";
    public static final String EXTRA_SAVE_ENABLED = "But you won't...but you won't...";
    public static final String EXTRA_RUN_ENABLED = "Love you with my life...really...17.9.28";

    @ViewById(R.id.editor)
    CodeMirrorEditor mEditor;

    @ViewById(R.id.input_method_enhance_bar)
    InputMethodEnhanceBar mInputMethodEnhanceBar;

    private static final String KEY_EDITOR_THEME = "我...深爱着...你呀...17.9.28";

    private String mName;
    private File mFile;
    private boolean mReadOnly = false;

    private ScriptExecution mScriptExecution;
    private boolean mTextChanged = false;
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ON_EXECUTION_FINISHED)) {
                mScriptExecution = null;
                setMenuItemStatus(R.id.run, true);
                String msg = intent.getStringExtra(Scripts.EXTRA_EXCEPTION_MESSAGE);
                if (msg != null) {
                    Snackbar.make(EditorView.this, getResources().getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    private void setMenuItemStatus(int id, boolean enabled) {
        findViewById(id).setEnabled(enabled);
    }


    public EditorView(Context context) {
        super(context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(mOnRunFinishedReceiver, new IntentFilter(ACTION_ON_EXECUTION_FINISHED));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mOnRunFinishedReceiver);
    }

    public void handleIntent(Intent intent) {
        mName = intent.getStringExtra(EXTRA_NAME);
        handleText(intent);
        mReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, false);
        boolean saveEnabled = intent.getBooleanExtra(EXTRA_SAVE_ENABLED, true);
        if (mReadOnly || !saveEnabled) {
            findViewById(R.id.save).setVisibility(View.GONE);
        }
        if (!intent.getBooleanExtra(EXTRA_RUN_ENABLED, true)) {
            findViewById(R.id.run).setVisibility(GONE);
        }

    }

    private void handleText(Intent intent) {
        String path = intent.getStringExtra(EXTRA_PATH);
        String content = intent.getStringExtra(EXTRA_CONTENT);
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

    @AfterViews
    void init() {
        setUpEditor();
    }

    private void setUpEditor() {
        mEditor.setTheme(PreferenceManager.getDefaultSharedPreferences(getContext())
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


    @Click(R.id.run)
    public void runAndSaveFileIfNeeded() {
        save().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        run();
                    }
                });
    }

    public void run() {
        Snackbar.make(this, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        mScriptExecution = Scripts.runWithBroadcastSender(new JavaScriptFileSource(mName, mFile), mFile.getParent());
        setMenuItemStatus(R.id.run, false);
    }


    @Click(R.id.undo)
    public void undo() {
        mEditor.undo();
    }

    @Click(R.id.redo)
    public void redo() {
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

    public void forceStop() {
        if (mScriptExecution != null) {
            mScriptExecution.getEngine().forceStop();
        }
    }

    @Click(R.id.save)
    public void saveFile() {
        save().subscribe();
    }

    public String getName() {
        return mName;
    }

    public boolean isTextChanged() {
        return mTextChanged;
    }

    public void showConsole() {
        if (mScriptExecution != null) {
            ((JavaScriptEngine) mScriptExecution.getEngine()).getRuntime().console.show();
        }
    }

    public void openByOtherApps() {
        if (mFile != null)
            Scripts.openByOtherApps(mFile);
    }

    public void beautifyCode() {
        mEditor.beautifyCode();
    }

    public void selectEditorTheme() {
        String[] themes = mEditor.getAvailableThemes();
        int i = Arrays.asList(themes).indexOf(mEditor.getTheme());
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_editor_theme)
                .items((CharSequence[]) themes)
                .itemsCallbackSingleChoice(i, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                                .putString(KEY_EDITOR_THEME, text.toString())
                                .apply();
                        mEditor.setTheme(text.toString());
                        return true;
                    }
                })
                .show();
    }

    public CodeMirrorEditor getEditor() {
        return mEditor;
    }
}
