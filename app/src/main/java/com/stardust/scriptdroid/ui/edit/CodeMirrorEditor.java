package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.stardust.pio.PFiles;
import com.stardust.scriptdroid.R;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Stardust on 2017/9/27.
 */

public class CodeMirrorEditor extends FrameLayout {

    private static final String LOG_TAG = "CodeMirrorEditor";


    public interface Callback {

        void onKeyUp(String line, int ch);

    }

    private static String[] sAvailableThemes;
    private String mTheme = "neo";
    private FrameLayout mProgressBarContainer;
    private WebView mWebView;
    private JavaScriptBridge mJavaScriptBridge = new JavaScriptBridge();
    private Callback mCallback;
    private Deferred<Void, Void, Void> mPageFinished = new DeferredObject<>();

    public CodeMirrorEditor(Context context) {
        super(context);
        init();
    }

    public CodeMirrorEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeMirrorEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CodeMirrorEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void beautifyCode() {
        evalJavaScript("editor.setValue(js_beautify(editor.getValue(), {indent_size: 2}));");
    }

    public String[] getAvailableThemes() {
        if (sAvailableThemes == null) {
            try {
                sAvailableThemes = getResources().getAssets().list("editor/codemirror/theme");
                for (int i = 0; i < sAvailableThemes.length; i++) {
                    sAvailableThemes[i] = PFiles.getNameWithoutExtension(sAvailableThemes[i]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return sAvailableThemes;
    }


    public String getTheme() {
        return mTheme;
    }

    public void setTheme(final String theme) {
        mPageFinished.promise().done(result -> {
            evalJavaScript(String.format(Locale.getDefault(),
                    "var e = document.createElement('link');e.setAttribute('rel', 'stylesheet');" +
                            "e.setAttribute('type', 'text/css');e.setAttribute('href', '%s');" +
                            "document.getElementsByTagName('head')[0].appendChild(e);",
                    "codemirror/theme/" + theme + ".css"));
            evalJavaScript(String.format(Locale.getDefault(), "editor.setOption('theme', '%s');", theme));
            mTheme = theme;
        });
    }


    private void init() {
        setupWebView();
        setupProgress();
        mWebView.loadUrl("file:///android_asset/editor/index.html");
    }

    private void setupProgress() {
        mProgressBarContainer = new FrameLayout(getContext());
        mProgressBarContainer.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mProgressBarContainer, params);
        MaterialProgressBar progressBar = new MaterialProgressBar(getContext());
        int dp50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(dp50, dp50);
        p.gravity = Gravity.CENTER;
        mProgressBarContainer.addView(progressBar, p);
    }

    private void setupWebView() {
        mWebView = new WebView(getContext()) {
            @Override
            public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection connection = super.onCreateInputConnection(outAttrs);
                return connection;// == null ? null : new MyInputConnection(connection);
            }
        };
        setupWebSettings();
        mWebView.addJavascriptInterface(mJavaScriptBridge, "__bridge__");
        addView(mWebView);
    }


    private void setupWebSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        //settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setNeedInitialFocus(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

    }


    @Override
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        return super.startActionModeForChild(originalView, new EditorActionModeCallback(callback, this));
    }

    @Override
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback, int type) {
        return super.startActionModeForChild(originalView, new EditorActionModeCallback(callback, this), type);
    }

    public void setReadOnly(boolean readOnly) {
        evalJavaScript(String.format("editor.setOption('readOnly', %b);", readOnly));
    }

    public void setProgress(boolean onProgress) {
        mProgressBarContainer.setVisibility(onProgress ? VISIBLE : GONE);
    }

    public void setText(final String text) {
        int id = SharedVariables.put(text);
        mPageFinished.promise().done(result -> evalJavaScript(String.format(Locale.getDefault(),
                "editor.setValue(__bridge__.getStringFromAndroid(%d));", id)));
    }


    public void insert(String text) {
        int id = SharedVariables.put(text);
        mPageFinished.promise().done(result -> evalJavaScript(String.format(Locale.getDefault(),
                "editor.replaceSelection(__bridge__.getStringFromAndroid(%d));", id)));
    }


    public Observable<String> getText() {
        return evalString("editor.getValue()");
    }

    public Observable<String> getLine() {
        return evalString("editor.getLine(editor.getCursor().line)");
    }

    public void deleteLine() {
        evalJavaScript("editor.replaceRange('', {line: editor.getCursor().line, ch: 0}," +
                " {line: editor.getCursor().line + 1, ch: 0});");
    }

    public void moveCursor(int dLine, int dCh) {
        evalJavaScript(String.format(Locale.getDefault(),
                "editor.setCursor({line: editor.getCursor().line + (%d), ch: editor.getCursor().ch + (%d)});",
                dLine, dCh));
    }

    public void copyLine() {
        getLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    ClipboardUtil.setClip(getContext(), s);
                    Snackbar.make(mWebView, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show();
                });
    }

    public void cut() {
        getSelection().observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    ClipboardUtil.setClip(getContext(), s);
                    evalJavaScript("editor.replaceSelection('');");
                });
    }


    public void copy() {
        getSelection().observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> ClipboardUtil.setClip(getContext(), s));
    }

    public void paste() {
        CharSequence clip = ClipboardUtil.getClip(getContext());
        if (clip == null) {
            return;
        }
        evalJavaScript(String.format("editor.replaceSelection('%s');", clip.toString().replace("'", "\\'")));
    }


    public void selectAll() {
        evalJavaScript("editor.execCommand('selectAll');");
    }

    public Observable<Integer> getLineCount() {
        PublishSubject<Integer> publishSubject = PublishSubject.create();
        int id = SharedVariables.put(publishSubject);
        evalJavaScript(String.format(Locale.getDefault(), "__bridge__.setIntFromJs(%d, editor.lineCount())", id));
        return publishSubject;
    }


    public void undo() {
        evalJavaScript("editor.undo();");
    }

    public void redo() {
        evalJavaScript("editor.redo();");
    }

    public void jumpTo(int line, int col) {
        evalJavaScript("editor.setCursor({line: " + line + ", ch: " + col + "});");
    }

    public void find(String keywords, boolean usingRegex) {
        setQuery(keywords, usingRegex);
        findNext();
    }

    private void setQuery(String keywords, boolean usingRegex) {
        keywords = keywords.replace("'", "\'");
        if (usingRegex) {
            keywords = "/" + keywords + "/";
        }
        evalJavaScript("editor.execCommand('clearSearch'); editor.state.search.query = '" +
                keywords + "';");
    }

    public void findNext() {
        evalJavaScript("editor.execCommand('findNext');");
    }

    public void findPrev() {
        evalJavaScript("editor.execCommand('findPrev');");
    }

    public void replaceAll(String keywords, String replacement, boolean usingRegex) {
        setQuery(keywords, usingRegex);
        replacement = replacement.replace("'", "\'");
        evalJavaScript("editor.state.search.text = '" + replacement + "';");
        evalJavaScript("editor.execCommand('replaceAll');");
    }

    public void jumpToDef() {
        evalJavaScript("editor.tern.jumpToDef();");
    }


    public void jumpToLineStart() {
        evalJavaScript("editor.execCommand('goLineStart');");
    }

    public void jumpToLineEnd() {
        evalJavaScript("editor.execCommand('goLineEnd')");
    }

    public void jumpToStart() {
        evalJavaScript("editor.execCommand('goDocStart');");
    }

    public void jumpToEnd() {
        evalJavaScript("editor.execCommand('goDocEnd');");
    }


    public void showType() {
        evalJavaScript("editor.tern.showType();");
    }

    public void rename() {
        evalJavaScript("editor.tern.rename();");
    }

    public void selectName() {
        evalJavaScript("editor.tern.selectName();");
    }


    public void replace(String keywords, String replacement, boolean usingRegex) {
        setQuery(keywords, usingRegex);
        replacement = replacement.replace("'", "\'");
        evalJavaScript("editor.state.search.text = '" + replacement + "';");
        findNext();
    }

    public Observable<String> getSelection() {
        return evalString("editor.getSelection()");
    }

    private Observable<String> evalString(String expr) {
        PublishSubject<String> publishSubject = PublishSubject.create();
        int id = SharedVariables.put(publishSubject);
        evalJavaScript(String.format(Locale.getDefault(), "__bridge__.setStringFromJs(%d, %s);", id, expr));
        return publishSubject;
    }

    public void replaceSelection() {
        evalJavaScript("editor.execCommand('replace');");
    }

    private void evalJavaScript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl("javascript:" + script);
        }
    }

    public void replace(String text, int fromLine, int fromCh, int toLine, int toCh) {
        String js = "editor.replaceRange('%s', {line: %d, ch: %d}, {line: %d, ch: %d})";
        evalJavaScript(String.format(Locale.getDefault(), js, text, fromLine, fromCh, toLine, toCh));
    }

    public class JavaScriptBridge {

        @JavascriptInterface
        public void setStringFromJs(int id, String text) {
            PublishSubject<String> publishSubject = SharedVariables.remove(id);
            if (publishSubject == null)
                return;
            publishSubject.onNext(text);
            publishSubject.onComplete();
        }

        @JavascriptInterface
        public void setIntFromJs(int id, int i) {
            PublishSubject<Integer> publishSubject = SharedVariables.remove(id);
            if (publishSubject == null)
                return;
            publishSubject.onNext(i);
            publishSubject.onComplete();
        }

        @JavascriptInterface
        public String getStringFromAndroid(int id) {
            return SharedVariables.remove(id);
        }

        @JavascriptInterface
        public void onKeyUp(String line, int cursor) {
            if (mCallback == null) {
                return;
            }
            mWebView.post(() -> mCallback.onKeyUp(line, cursor));
        }

    }

    private class MyWebViewClient extends WebViewClient {

        private final String INIT_SCRIPT = "editor.on('change', function(){__bridge__.onTextChange();});";

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mPageFinished.resolve(null);
            evalJavaScript(INIT_SCRIPT);
            setProgress(false);
        }

    }

    private class MyWebChromeClient extends WebChromeClient {

        private final Pattern RENAME = Pattern.compile("New name for ([a-zA-Z]+)");

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            Matcher m = RENAME.matcher(message);
            if (m.matches() && m.groupCount() >= 1) {
                showRenamePrompt(m.group(1), defaultValue, result);
                return true;
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        private void showRenamePrompt(String name, String defaultValue, final JsPromptResult result) {
            new ThemeColorMaterialDialogBuilder(getContext())
                    .title(getResources().getString(R.string.text_rename) + name)
                    .input("", defaultValue, (dialog, input) -> result.confirm(input.toString()))
                    .show();
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new ThemeColorMaterialDialogBuilder(getContext())
                    .title(R.string.text_alert)
                    .content(message)
                    .onPositive((dialog, which) -> result.confirm())
                    .cancelListener(dialog -> result.cancel())
                    .positiveText(R.string.ok)
                    .show();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress != 100);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage m) {
            switch (m.messageLevel()) {
                case LOG:
                    Log.i(LOG_TAG, String.format("%s (%s:%d)", m.message(), m.sourceId(), m.lineNumber()));
                    return true;
                case TIP:
                    Log.v(LOG_TAG, String.format("%s (%s:%d)", m.message(), m.sourceId(), m.lineNumber()));
                    return true;
                case DEBUG:
                    Log.d(LOG_TAG, String.format("%s (%s:%d)", m.message(), m.sourceId(), m.lineNumber()));
                    return true;
                case WARNING:
                    Log.w(LOG_TAG, String.format("%s (%s:%d)", m.message(), m.sourceId(), m.lineNumber()));
                    return true;
                case ERROR:
                    Log.e(LOG_TAG, String.format("%s (%s:%d)", m.message(), m.sourceId(), m.lineNumber()));
                    return true;
            }
            return false;
        }
    }

}
