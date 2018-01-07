package com.stardust.scriptdroid.ui.edit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.pio.PFiles;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.indices.Module;
import com.stardust.scriptdroid.model.indices.Property;
import com.stardust.scriptdroid.model.script.Scripts;
import com.stardust.scriptdroid.ui.doc.ManualDialog;
import com.stardust.scriptdroid.ui.edit.completion.CodeCompletions;
import com.stardust.scriptdroid.ui.edit.completion.CodeCompletionBar;
import com.stardust.scriptdroid.ui.edit.completion.InputMethodEnhancedBarColors;
import com.stardust.scriptdroid.ui.edit.completion.Symbols;
import com.stardust.scriptdroid.ui.edit.keyboard.FunctionsKeyboardHelper;
import com.stardust.scriptdroid.ui.edit.keyboard.FunctionsKeyboardView;
import com.stardust.scriptdroid.ui.log.LogActivity_;
import com.stardust.scriptdroid.ui.widget.EWebView;
import com.stardust.scriptdroid.ui.widget.ToolbarMenuItem;
import com.stardust.util.BackPressedHandler;
import com.stardust.widget.ViewSwitcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.stardust.scriptdroid.model.script.Scripts.ACTION_ON_EXECUTION_FINISHED;

/**
 * Created by Stardust on 2017/9/28.
 */
@EViewGroup(R.layout.editor_view)
public class EditorView extends FrameLayout implements CodeCompletionBar.OnHintClickListener, FunctionsKeyboardView.ClickCallback {

    public static final String EXTRA_PATH = "Still Love Eating 17.4.5";
    public static final String EXTRA_NAME = "Still love you 17.6.29 But....(ಥ_ಥ)";
    public static final String EXTRA_CONTENT = "It's hard...............";
    public static final String EXTRA_READ_ONLY = "Miss you more every day、、、";
    public static final String EXTRA_SAVE_ENABLED = "But you won't...but you won't...";
    public static final String EXTRA_RUN_ENABLED = "Love you with my life...really...17.9.28";

    @ViewById(R.id.editor)
    CodeMirrorEditor mEditor;

    @ViewById(R.id.code_completion_bar)
    CodeCompletionBar mCodeCompletionBar;

    @ViewById(R.id.toolbar_switcher)
    ViewSwitcher mToolbarSwitcher;

    @ViewById(R.id.replace)
    ToolbarMenuItem mReplaceMenuItem;

    @ViewById(R.id.input_method_enhance_bar)
    View mInputMethodEnhanceBar;

    @ViewById(R.id.symbol_bar)
    CodeCompletionBar mSymbolBar;

    @ViewById(R.id.functions)
    ImageView mShowFunctionsButton;

    @ViewById(R.id.functions_keyboard)
    FunctionsKeyboardView mFunctionsKeyboard;

    @ViewById(R.id.docs)
    EWebView mDocsWebView;

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private static final String KEY_EDITOR_THEME = "我...深爱着...你呀...17.9.28";

    private String mName;
    private File mFile;
    private boolean mReadOnly = false;

    private ScriptExecution mScriptExecution;
    private boolean mTextChanged = false;
    private FunctionsKeyboardHelper mFunctionsKeyboardHelper;
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ON_EXECUTION_FINISHED)) {
                mScriptExecution = null;
                setMenuItemStatus(R.id.run, true);
                String msg = intent.getStringExtra(Scripts.EXTRA_EXCEPTION_MESSAGE);
                int line = intent.getIntExtra(Scripts.EXTRA_EXCEPTION_LINE_NUMBER, -1);
                int col = intent.getIntExtra(Scripts.EXTRA_EXCEPTION_COLUMN_NUMBER, 0);
                if (line >= 1) {
                    mEditor.jumpTo(line - 1, col);
                }
                if (msg != null) {
                    showErrorMessage(msg);
                }
            }
        }
    };

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
        if (getContext() instanceof BackPressedHandler.HostActivity) {
            ((BackPressedHandler.HostActivity) getContext()).getBackPressedObserver().registerHandler(mFunctionsKeyboardHelper);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mOnRunFinishedReceiver);
        if (getContext() instanceof BackPressedHandler.HostActivity) {
            ((BackPressedHandler.HostActivity) getContext()).getBackPressedObserver().unregisterHandler(mFunctionsKeyboardHelper);
        }
    }

    public File getFile() {
        return mFile;
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
        if (mReadOnly) {
            mEditor.setReadOnly(true);
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
            mEditor.loadFile(mFile);
        }
    }


    private void setMenuItemStatus(int id, boolean enabled) {
        findViewById(id).setEnabled(enabled);
    }


    @AfterViews
    void init() {
        setUpEditor();
        setUpInputMethodEnhancedBar();
        setUpFunctionsKeyboard();
        setMenuItemStatus(R.id.save, false);
        mDocsWebView.getWebView().getSettings().setDisplayZoomControls(true);
        mDocsWebView.getWebView().loadUrl(Pref.getDocumentationUrl() + "index.html");

    }

    private void setUpFunctionsKeyboard() {
        mFunctionsKeyboardHelper = FunctionsKeyboardHelper.with((Activity) getContext())
                .setContent(mEditor)
                .setFunctionsTrigger(mShowFunctionsButton)
                .setFunctionsView(mFunctionsKeyboard)
                .setEditView(mEditor.getWebView())
                .build();
        mFunctionsKeyboard.setClickCallback(this);
    }

    private void setUpInputMethodEnhancedBar() {
        mSymbolBar.setCodeCompletions(Symbols.getSymbols());
        mCodeCompletionBar.setOnHintClickListener(this);
        mSymbolBar.setOnHintClickListener(this);
    }


    private void setUpEditor() {
        setTheme(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(KEY_EDITOR_THEME, mEditor.getTheme()));
        mEditor.setCallback(new CodeMirrorEditor.Callback() {
            @Override
            public void onChange() {
                mTextChanged = true;
                setMenuItemStatus(R.id.save, true);
            }


            @Override
            public void updateCodeCompletion(int fromLine, int fromCh, int toLine, int toCh, final String[] list, final String[] urls) {
                mCodeCompletionBar.setCodeCompletions(new CodeCompletions(
                        new CodeCompletions.Pos(fromLine, fromCh),
                        new CodeCompletions.Pos(toLine, toCh),
                        Arrays.asList(list),
                        Arrays.asList(urls)
                ));
            }
        });


    }

    public void setTheme(String theme) {
        mEditor.setTheme(theme);
        mInputMethodEnhanceBar.setBackgroundColor(InputMethodEnhancedBarColors.getBackgroundColor(theme));
        int textColor = InputMethodEnhancedBarColors.getTextColor(theme);
        mCodeCompletionBar.setTextColor(textColor);
        mSymbolBar.setTextColor(textColor);
        mShowFunctionsButton.setColorFilter(textColor);
    }

    public boolean onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            if (mDocsWebView.getWebView().canGoBack()) {
                mDocsWebView.getWebView().goBack();
            } else {
                mDrawerLayout.closeDrawer(Gravity.START);
            }
            return true;
        }
        return false;
    }


    @Click(R.id.run)
    public void runAndSaveFileIfNeeded() {
        save().observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> run());
    }

    public void run() {
        Snackbar.make(this, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        mScriptExecution = Scripts.runWithBroadcastSender(mFile);
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
                .doOnNext(s -> PFiles.write(mFile, s))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    mTextChanged = false;
                    setMenuItemStatus(R.id.save, false);
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

    @Click(R.id.find_next)
    void findNext() {
        mEditor.findNext();
    }

    @Click(R.id.find_prev)
    void findPrev() {
        mEditor.findPrev();
    }

    @Click(R.id.cancel)
    void cancelSearch() {
        mToolbarSwitcher.showFirst();
    }

    @Click(R.id.replace)
    void replace() {
        mEditor.replaceSelection();
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
                .itemsCallbackSingleChoice(i, (dialog, itemView, which, text) -> {
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                            .putString(KEY_EDITOR_THEME, text.toString())
                            .apply();
                    setTheme(text.toString());
                    return true;
                })
                .show();
    }

    public CodeMirrorEditor getEditor() {
        return mEditor;
    }

    public void find(String keywords, boolean usingRegex) {
        mEditor.find(keywords, usingRegex);
        mReplaceMenuItem.setVisibility(GONE);
        mToolbarSwitcher.showSecond();
    }

    public void replace(String keywords, String replacement, boolean usingRegex) {
        mEditor.replace(keywords, replacement, usingRegex);
        mReplaceMenuItem.setVisibility(VISIBLE);
        mToolbarSwitcher.showSecond();
    }

    public void replaceAll(String keywords, String replacement, boolean usingRegex) {
        mEditor.replaceAll(keywords, replacement, usingRegex);
    }


    private void showErrorMessage(String msg) {
        Snackbar.make(EditorView.this, getResources().getString(R.string.text_error) + ": " + msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.text_detail, v -> LogActivity_.intent(getContext()).start())
                .show();
    }

    @Override
    public void onHintClick(CodeCompletions completions, int pos) {
        if (completions.shouldBeInserted()) {
            mEditor.insert(completions.getHints().get(pos));
            return;
        }
        mEditor.replace(completions.getHints().get(pos), completions.getFrom().line, completions.getFrom().ch,
                completions.getTo().line, completions.getTo().ch);
    }

    @Override
    public void onHintLongClick(CodeCompletions completions, int pos) {
        String url = completions.getUrltAt(pos);
        if (url == null)
            return;
        showManual(url, completions.getHints().get(pos));

    }

    private void showManual(String url, String title) {
        String absUrl = Pref.getDocumentationUrl() + url;
        new ManualDialog(getContext())
                .title(title)
                .url(absUrl)
                .pinToLeft(v -> {
                    mDocsWebView.getWebView().loadUrl(absUrl);
                    mDrawerLayout.openDrawer(Gravity.START);
                })
                .show();
    }

    @Override
    public void onModuleLongClick(Module module) {
        showManual(module.getUrl(), module.getName());
    }

    @Override
    public void onPropertyClick(Module m, Property property) {
        if (property.isGlobal()) {
            mEditor.insert(property.getKey() + "()");
        } else {
            mEditor.insert(m.getName() + "." + property.getKey() + "()");
        }
        mEditor.moveCursor(0, -1);
        mFunctionsKeyboardHelper.hideFunctionsLayout(true);
    }

    @Override
    public void onPropertyLongClick(Module m, Property property) {
        if (TextUtils.isEmpty(property.getUrl())) {
            showManual(m.getUrl(), property.getKey());
        } else {
            showManual(property.getUrl(), property.getKey());
        }
    }
}
