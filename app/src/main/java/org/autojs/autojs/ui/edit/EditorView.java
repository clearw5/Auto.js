package org.autojs.autojs.ui.edit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.pio.PFiles;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.model.autocomplete.AutoCompletion;
import org.autojs.autojs.model.autocomplete.CodeCompletion;
import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.ui.doc.ManualDialog;
import org.autojs.autojs.model.autocomplete.CodeCompletions;
import org.autojs.autojs.ui.edit.completion.CodeCompletionBar;
import org.autojs.autojs.model.autocomplete.Symbols;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardHelper;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardView;
import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.edit.theme.Themes;
import org.autojs.autojs.ui.log.LogActivity_;
import org.autojs.autojs.ui.widget.EWebView;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;
import org.autojs.autojs.ui.widget.ToolbarMenuItem;
import com.stardust.util.BackPressedHandler;
import com.stardust.util.ViewUtils;
import com.stardust.widget.ViewSwitcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.autojs.autojs.model.script.Scripts.ACTION_ON_EXECUTION_FINISHED;

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
    CodeEditor mEditor;

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

    private String mName;
    private File mFile;
    private boolean mReadOnly = false;
    private ScriptExecution mScriptExecution;
    private AutoCompletion mAutoCompletion;
    private Theme mEditorTheme;
    private FunctionsKeyboardHelper mFunctionsKeyboardHelper;
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ON_EXECUTION_FINISHED.equals(intent.getAction())) {
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
    private String mRestoredText;

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

    public void setRestoredText(String text) {
        mRestoredText = text;
        mEditor.setText(text);
    }

    private void handleText(Intent intent) {
        String path = intent.getStringExtra(EXTRA_PATH);
        String content = intent.getStringExtra(EXTRA_CONTENT);
        if (content != null) {
            setInitialText(content);
        } else {
            mFile = new File(path);
            if (mName == null) {
                mName = mFile.getName();
            }
            loadFile(mFile);
        }
    }


    private void loadFile(final File file) {
        mEditor.setProgress(true);
        Observable.fromCallable(() -> PFiles.read(file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    setInitialText(s);
                    mEditor.setProgress(false);
                }, err -> {
                    err.printStackTrace();
                    Toast.makeText(getContext(), getContext().getString(R.string.text_cannot_read_file, file.getPath()),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setInitialText(String text) {
        if (mRestoredText != null) {
            mEditor.setText(mRestoredText);
            mRestoredText = null;
            return;
        }
        mEditor.setInitialText(text);
    }


    private void setMenuItemStatus(int id, boolean enabled) {
        findViewById(id).setEnabled(enabled);
    }


    @AfterViews
    void init() {
        setTheme(Theme.getDefault(getContext()));
        setUpEditor();
        setUpInputMethodEnhancedBar();
        setUpFunctionsKeyboard();
        setMenuItemStatus(R.id.save, false);
        mDocsWebView.getWebView().getSettings().setDisplayZoomControls(true);
        mDocsWebView.getWebView().loadUrl(Pref.getDocumentationUrl() + "index.html");
        Themes.getCurrent(getContext()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setTheme);
    }

    private void setUpFunctionsKeyboard() {
        mFunctionsKeyboardHelper = FunctionsKeyboardHelper.with((Activity) getContext())
                .setContent(mEditor)
                .setFunctionsTrigger(mShowFunctionsButton)
                .setFunctionsView(mFunctionsKeyboard)
                .setEditView(mEditor.getCodeEditText())
                .build();
        mFunctionsKeyboard.setClickCallback(this);
    }

    private void setUpInputMethodEnhancedBar() {
        mSymbolBar.setCodeCompletions(Symbols.getSymbols());
        mCodeCompletionBar.setOnHintClickListener(this);
        mSymbolBar.setOnHintClickListener(this);
        mAutoCompletion = new AutoCompletion(getContext(), mEditor.getCodeEditText());
        mAutoCompletion.setAutoCompleteCallback(mCodeCompletionBar::setCodeCompletions);
    }


    private void setUpEditor() {
        mEditor.getCodeEditText().addTextChangedListener(new SimpleTextWatcher(s -> {
            setMenuItemStatus(R.id.save, mEditor.isTextChanged());
            setMenuItemStatus(R.id.undo, mEditor.canUndo());
            setMenuItemStatus(R.id.redo, mEditor.canRedo());
        }));
        mEditor.setCursorChangeCallback(this::autoComplete);
        mEditor.getCodeEditText().setTextSize(Pref.getEditorTextSize((int) ViewUtils.pxToSp(getContext(), mEditor.getCodeEditText().getTextSize())));
    }

    private void autoComplete(String line, int cursor) {
        mAutoCompletion.onCursorChange(line, cursor);
    }

    public void setTheme(Theme theme) {
        mEditorTheme = theme;
        mEditor.setTheme(theme);
        mInputMethodEnhanceBar.setBackgroundColor(theme.getImeBarBackgroundColor());
        int textColor = theme.getImeBarForegroundColor();
        mCodeCompletionBar.setTextColor(textColor);
        mSymbolBar.setTextColor(textColor);
        mShowFunctionsButton.setColorFilter(textColor);
        invalidate();
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
        return Observable.just(mEditor.getText())
                .observeOn(Schedulers.io())
                .doOnNext(s -> PFiles.write(mFile, s))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    mEditor.markTextAsSaved();
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
        return mEditor.isTextChanged();
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
        mEditor.setProgress(true);
        Themes.getAllThemes(getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(themes -> {
                    mEditor.setProgress(false);
                    selectEditorTheme(themes);
                });
    }

    public void selectTextSize() {
        new TextSizeSettingDialogBuilder(getContext())
                .initialValue((int) ViewUtils.pxToSp(getContext(), mEditor.getCodeEditText().getTextSize()))
                .callback(this::setTextSize)
                .show();
    }

    public void setTextSize(int value) {
        Pref.setEditorTextSize(value);
        mEditor.getCodeEditText().setTextSize(value);
    }

    private void selectEditorTheme(List<Theme> themes) {
        int i = themes.indexOf(mEditorTheme);
        if (i < 0) {
            i = 0;
        }
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_editor_theme)
                .items(themes)
                .itemsCallbackSingleChoice(i, (dialog, itemView, which, text) -> {
                    setTheme(themes.get(which));
                    Themes.setCurrent(themes.get(which).getName());
                    return true;
                })
                .show();
    }

    public CodeEditor getEditor() {
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
        CodeCompletion completion = completions.get(pos);
        mEditor.insert(completion.getInsertText());
    }

    @Override
    public void onHintLongClick(CodeCompletions completions, int pos) {
        CodeCompletion completion = completions.get(pos);
        if (completion.getUrl() == null)
            return;
        showManual(completion.getUrl(), completion.getHint());
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
        String p = property.getKey();
        if (!property.isVariable()) {
            p = p + "()";
        }
        if (property.isGlobal()) {
            mEditor.insert(p);
        } else {
            mEditor.insert(m.getName() + "." + p);
        }
        if (!property.isVariable()) {
            mEditor.moveCursor(-1);
        }
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
