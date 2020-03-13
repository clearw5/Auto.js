package org.autojs.autojs.ui.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.pio.PFiles;
import com.stardust.util.BackPressedHandler;
import com.stardust.util.Callback;
import com.stardust.util.ViewUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.model.autocomplete.AutoCompletion;
import org.autojs.autojs.model.autocomplete.CodeCompletion;
import org.autojs.autojs.model.autocomplete.CodeCompletions;
import org.autojs.autojs.model.autocomplete.Symbols;
import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.doc.ManualDialog;
import org.autojs.autojs.ui.edit.completion.CodeCompletionBar;
import org.autojs.autojs.ui.edit.debug.DebugBar;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardHelper;
import org.autojs.autojs.ui.edit.keyboard.FunctionsKeyboardView;
import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.edit.theme.Themes;
import org.autojs.autojs.ui.edit.toolbar.DebugToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.DebugToolbarFragment_;
import org.autojs.autojs.ui.edit.toolbar.NormalToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.NormalToolbarFragment_;
import org.autojs.autojs.ui.edit.toolbar.SearchToolbarFragment;
import org.autojs.autojs.ui.edit.toolbar.SearchToolbarFragment_;
import org.autojs.autojs.ui.edit.toolbar.ToolbarFragment;
import org.autojs.autojs.ui.log.LogActivity_;
import org.autojs.autojs.ui.widget.EWebView;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.autojs.autojs.model.script.Scripts.ACTION_ON_EXECUTION_FINISHED;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_COLUMN_NUMBER;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_LINE_NUMBER;
import static org.autojs.autojs.model.script.Scripts.EXTRA_EXCEPTION_MESSAGE;

/**
 * Created by Stardust on 2017/9/28.
 */
@EViewGroup(R.layout.editor_view)
public class EditorView extends FrameLayout implements CodeCompletionBar.OnHintClickListener, FunctionsKeyboardView.ClickCallback, ToolbarFragment.OnMenuItemClickListener {

    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_READ_ONLY = "readOnly";
    public static final String EXTRA_SAVE_ENABLED = "saveEnabled";
    public static final String EXTRA_RUN_ENABLED = "runEnabled";

    @ViewById(R.id.editor)
    CodeEditor mEditor;

    @ViewById(R.id.code_completion_bar)
    CodeCompletionBar mCodeCompletionBar;

    @ViewById(R.id.input_method_enhance_bar)
    View mInputMethodEnhanceBar;

    @ViewById(R.id.symbol_bar)
    CodeCompletionBar mSymbolBar;

    @ViewById(R.id.functions)
    ImageView mShowFunctionsButton;

    @ViewById(R.id.functions_keyboard)
    FunctionsKeyboardView mFunctionsKeyboard;

    @ViewById(R.id.debug_bar)
    DebugBar mDebugBar;

    @ViewById(R.id.docs)
    EWebView mDocsWebView;

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private String mName;
    private Uri mUri;
    private boolean mReadOnly = false;
    private int mScriptExecutionId;
    private AutoCompletion mAutoCompletion;
    private Theme mEditorTheme;
    private FunctionsKeyboardHelper mFunctionsKeyboardHelper;
    private BroadcastReceiver mOnRunFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ON_EXECUTION_FINISHED.equals(intent.getAction())) {
                mScriptExecutionId = ScriptExecution.NO_ID;
                if (mDebugging) {
                    exitDebugging();
                }
                setMenuItemStatus(R.id.run, true);
                String msg = intent.getStringExtra(EXTRA_EXCEPTION_MESSAGE);
                int line = intent.getIntExtra(EXTRA_EXCEPTION_LINE_NUMBER, -1);
                int col = intent.getIntExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, 0);
                if (line >= 1) {
                    mEditor.jumpTo(line - 1, col);
                }
                if (msg != null) {
                    showErrorMessage(msg);
                }
            }
        }
    };

    private SparseBooleanArray mMenuItemStatus = new SparseBooleanArray();
    private String mRestoredText;
    private NormalToolbarFragment mNormalToolbar = new NormalToolbarFragment_();
    private boolean mDebugging = false;

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

    public Uri getUri() {
        return mUri;
    }

    public Observable<String> handleIntent(Intent intent) {
        mName = intent.getStringExtra(EXTRA_NAME);
        return handleText(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(str -> {
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
                });
    }

    public void setRestoredText(String text) {
        mRestoredText = text;
        mEditor.setText(text);
    }

    private Observable<String> handleText(Intent intent) {
        String path = intent.getStringExtra(EXTRA_PATH);
        String content = intent.getStringExtra(EXTRA_CONTENT);
        if (content != null) {
            setInitialText(content);
            return Observable.just(content);
        } else {
            if (path == null) {
                if (intent.getData() == null) {
                    return Observable.error(new IllegalArgumentException("path and content is empty"));
                } else {
                    mUri = intent.getData();
                }
            } else {
                mUri = Uri.fromFile(new File(path));
            }
            if (mName == null) {
                mName = PFiles.getNameWithoutExtension(mUri.getPath());
            }
            return loadUri(mUri);
        }
    }


    @SuppressLint("CheckResult")
    private Observable<String> loadUri(final Uri uri) {
        mEditor.setProgress(true);
        return Observable.fromCallable(() -> PFiles.read(getContext().getContentResolver().openInputStream(uri)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    setInitialText(s);
                    mEditor.setProgress(false);
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
        mMenuItemStatus.put(id, enabled);
        ToolbarFragment fragment = (ToolbarFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.toolbar_menu);
        if (fragment == null) {
            mNormalToolbar.setMenuItemStatus(id, enabled);
        } else {
            fragment.setMenuItemStatus(id, enabled);
        }
    }

    public boolean getMenuItemStatus(int id, boolean defValue) {
        return mMenuItemStatus.get(id, defValue);
    }

    @AfterViews
    void init() {
        //setTheme(Theme.getDefault(getContext()));
        setUpEditor();
        setUpInputMethodEnhancedBar();
        setUpFunctionsKeyboard();
        setMenuItemStatus(R.id.save, false);
        mDocsWebView.getWebView().getSettings().setDisplayZoomControls(true);
        mDocsWebView.getWebView().loadUrl(Pref.getDocumentationUrl() + "index.html");
        Themes.getCurrent(getContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setTheme);
        initNormalToolbar();
    }

    private void initNormalToolbar() {
        mNormalToolbar.setOnMenuItemClickListener(this);
        mNormalToolbar.setOnMenuItemLongClickListener(id -> {
            if (id == R.id.run) {
                debug();
                return true;
            }
            return false;
        });
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.toolbar_menu);
        if (fragment == null) {
            showNormalToolbar();
        }
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
        mEditor.addCursorChangeCallback(this::autoComplete);
        mEditor.getCodeEditText().setTextSize(Pref.getEditorTextSize((int) ViewUtils.pxToSp(getContext(), mEditor.getCodeEditText().getTextSize())));
    }

    private void autoComplete(String line, int cursor) {
        mAutoCompletion.onCursorChange(line, cursor);
    }

    public DebugBar getDebugBar() {
        return mDebugBar;
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (mDocsWebView.getWebView().canGoBack()) {
                mDocsWebView.getWebView().goBack();
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onToolbarMenuItemClick(int id) {
        switch (id) {
            case R.id.run:
                runAndSaveFileIfNeeded();
                break;
            case R.id.save:
                saveFile();
                break;
            case R.id.undo:
                undo();
                break;
            case R.id.redo:
                redo();
                break;
            case R.id.replace:
                replace();
                break;
            case R.id.find_next:
                findNext();
                break;
            case R.id.find_prev:
                findPrev();
                break;
            case R.id.cancel_search:
                cancelSearch();
                break;
        }
    }

    @SuppressLint("CheckResult")
    public void runAndSaveFileIfNeeded() {
        save().observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> run(true), Observers.toastMessage());
    }

    public ScriptExecution run(boolean showMessage) {
        if (showMessage) {
            Snackbar.make(this, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        }
        // TODO: 2018/10/24
        ScriptExecution execution = Scripts.INSTANCE.runWithBroadcastSender(new File(mUri.getPath()));
        if (execution == null) {
            return null;
        }
        mScriptExecutionId = execution.getId();
        setMenuItemStatus(R.id.run, false);
        return execution;
    }


    public void undo() {
        mEditor.undo();
    }

    public void redo() {
        mEditor.redo();
    }

    public Observable<String> save() {
        String path = mUri.getPath();
        PFiles.move(path, path + ".bak");
        return Observable.just(mEditor.getText())
                .observeOn(Schedulers.io())
                .doOnNext(s -> PFiles.write(getContext().getContentResolver().openOutputStream(mUri), s))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    mEditor.markTextAsSaved();
                    setMenuItemStatus(R.id.save, false);
                });
    }

    public void forceStop() {
        doWithCurrentEngine(ScriptEngine::forceStop);
    }

    private void doWithCurrentEngine(Callback<ScriptEngine> callback) {
        ScriptExecution execution = AutoJs.getInstance().getScriptEngineService().getScriptExecution(mScriptExecutionId);
        if (execution != null) {
            ScriptEngine engine = execution.getEngine();
            if (engine != null) {
                callback.call(engine);
            }
        }
    }

    @SuppressLint("CheckResult")
    public void saveFile() {
        save()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(), e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    void findNext() {
        mEditor.findNext();
    }

    void findPrev() {
        mEditor.findPrev();
    }

    void cancelSearch() {
        showNormalToolbar();
    }

    private void showNormalToolbar() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, mNormalToolbar)
                .commitAllowingStateLoss();
    }

    FragmentActivity getActivity() {
        Context context = getContext();
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (FragmentActivity) context;
    }

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
        doWithCurrentEngine(engine -> ((JavaScriptEngine) engine).getRuntime().console.show());
    }

    public void openByOtherApps() {
        if (mUri != null) {
            Scripts.INSTANCE.openByOtherApps(mUri);
        }
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

    public void find(String keywords, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        mEditor.find(keywords, usingRegex);
        showSearchToolbar(false);
    }

    private void showSearchToolbar(boolean showReplaceItem) {
        SearchToolbarFragment searchToolbarFragment = SearchToolbarFragment_.builder()
                .arg(SearchToolbarFragment.ARGUMENT_SHOW_REPLACE_ITEM, showReplaceItem)
                .build();
        searchToolbarFragment.setOnMenuItemClickListener(this);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, searchToolbarFragment)
                .commit();
    }

    public void replace(String keywords, String replacement, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        mEditor.replace(keywords, replacement, usingRegex);
        showSearchToolbar(true);
    }

    public void replaceAll(String keywords, String replacement, boolean usingRegex) throws CodeEditor.CheckedPatternSyntaxException {
        mEditor.replaceAll(keywords, replacement, usingRegex);
    }


    public void debug() {
        DebugToolbarFragment debugToolbarFragment = DebugToolbarFragment_.builder()
                .build();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_menu, debugToolbarFragment)
                .commit();
        mDebugBar.setVisibility(VISIBLE);
        mInputMethodEnhanceBar.setVisibility(GONE);
        mDebugging = true;
    }

    public void exitDebugging() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.toolbar_menu);
        if (fragment instanceof DebugToolbarFragment) {
            ((DebugToolbarFragment) fragment).detachDebugger();
        }
        showNormalToolbar();
        mEditor.setDebuggingLine(-1);
        mDebugBar.setVisibility(GONE);
        mInputMethodEnhanceBar.setVisibility(VISIBLE);
        mDebugging = false;
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
                    mDrawerLayout.openDrawer(GravityCompat.START);
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

    public int getScriptExecutionId() {
        return mScriptExecutionId;
    }

    @Nullable
    public ScriptExecution getScriptExecution() {
        return AutoJs.getInstance().getScriptEngineService().getScriptExecution(mScriptExecutionId);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable superData = super.onSaveInstanceState();
        bundle.putParcelable("super_data", superData);
        bundle.putInt("script_execution_id", mScriptExecutionId);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super_data");
        mScriptExecutionId = bundle.getInt("script_execution_id", ScriptExecution.NO_ID);
        super.onRestoreInstanceState(superData);
        setMenuItemStatus(R.id.run, mScriptExecutionId == ScriptExecution.NO_ID);
    }

    public void destroy() {
        mEditor.destroy();
        mAutoCompletion.shutdown();
    }
}
