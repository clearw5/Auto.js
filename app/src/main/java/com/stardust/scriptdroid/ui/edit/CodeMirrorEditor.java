package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.http.GET;

/**
 * Created by Stardust on 2017/9/27.
 */

public class CodeMirrorEditor extends FrameLayout {

    private static final String LOG_TAG = "CodeMirrorEditor";

    public interface Callback {
        void onChange();

        void updateCodeCompletion(int fromLine, int fromCh, int toLine, int toCh, String[] list);
    }

    private static String[] sAvailableThemes;
    private String mTheme = "neo";
    private FrameLayout mProgressBarContainer;
    private WebView mWebView;
    private JavaScriptBridge mJavaScriptBridge = new JavaScriptBridge();
    private Callback mCallback;
    private Deferred<Void, Void, Void> mPageFinished = new DeferredObject<>();

    private PublishSubject<String> mStringFromJs;
    private PublishSubject<Integer> mIntFromJs;
    private String mTextFromAndroid;


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

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void beautifyCode() {
        evalJavaScript("editor.setValue(js_beautify(editor.getValue()));");
    }

    public String[] getAvailableThemes() {
        if (sAvailableThemes == null) {
            try {
                sAvailableThemes = getResources().getAssets().list("editor/codemirror/theme");
                for (int i = 0; i < sAvailableThemes.length; i++) {
                    sAvailableThemes[i] = PFile.getNameWithoutExtension(sAvailableThemes[i]);
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
        mPageFinished.promise().done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void result) {
                evalJavaScript(String.format(Locale.getDefault(),
                        "var e = document.createElement('link');e.setAttribute('rel', 'stylesheet');" +
                                "e.setAttribute('type', 'text/css');e.setAttribute('href', '%s');" +
                                "document.getElementsByTagName('head')[0].appendChild(e);",
                        "codemirror/theme/" + theme + ".css"));
                evalJavaScript(String.format(Locale.getDefault(), "editor.setOption('theme', '%s');", theme));
                mTheme = theme;
            }
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
        mWebView = new WebView(getContext());
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
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    public void setProgress(boolean onProgress) {
        mProgressBarContainer.setVisibility(onProgress ? VISIBLE : GONE);
    }

    public void setText(final String text) {
        mTextFromAndroid = text;
        mPageFinished.promise().done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void result) {
                evalJavaScript("editor.setValue(__bridge__.getStringFromAndroid());");
            }
        });
    }

    public void insert(String text) {
        mTextFromAndroid = text;
        mPageFinished.promise().done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void result) {
                evalJavaScript("editor.replaceSelection(__bridge__.getStringFromAndroid());");
            }
        });
    }

    public void loadFile(final File file) {
        setProgress(true);
        // TODO: 2017/9/29 handle error
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return PFile.read(file);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        setText(s);
                        setProgress(false);
                    }
                });
    }

    public Observable<String> getText() {
        mStringFromJs = PublishSubject.create();
        evalJavaScript("__bridge__.setStringFromJs(editor.getValue());");
        return mStringFromJs;
    }

    public Observable<String> getLine() {
        mStringFromJs = PublishSubject.create();
        evalJavaScript("__bridge__.setStringFromJs(editor.getLine(editor.getCursor().line))");
        return mStringFromJs;
    }

    public void deleteLine() {
        evalJavaScript("editor.replaceRange('', {line: editor.getCursor().line, ch: 0}," +
                " {line: editor.getCursor().line + 1, ch: 0});");
    }

    public Observable<Integer> getLineCount() {
        mIntFromJs = PublishSubject.create();
        evalJavaScript("__bridge__.setIntFromJs(editor.lineCount())");
        return mIntFromJs;
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
        mStringFromJs = PublishSubject.create();
        evalJavaScript("__bridge__.setStringFromJs(editor.getSelection());");
        return mStringFromJs;
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
        public void setStringFromJs(String text) {
            if (mStringFromJs == null) {
                return;
            }
            mStringFromJs.onNext(text);
            mStringFromJs.onComplete();
            mStringFromJs = null;
        }

        @JavascriptInterface
        public void setIntFromJs(int i) {
            if (mIntFromJs == null) {
                return;
            }
            mIntFromJs.onNext(i);
            mIntFromJs.onComplete();
            mIntFromJs = null;
        }

        @JavascriptInterface
        public String getStringFromAndroid() {
            String t = mTextFromAndroid;
            mTextFromAndroid = null;
            return t;
        }

        @JavascriptInterface
        public void onTextChange() {
            if (mCallback == null) {
                return;
            }
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onChange();
                }
            });
        }

        @JavascriptInterface
        public void updateCodeCompletion(final int fromLine, final int fromCh, final int toLine, final int toCh, final String[] list) {
            if (mCallback == null) {
                return;
            }
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.updateCodeCompletion(fromLine, fromCh, toLine, toCh, list);
                }
            });
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
                    .input("", defaultValue, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@android.support.annotation.NonNull MaterialDialog dialog, CharSequence input) {
                            result.confirm(input.toString());
                        }
                    })
                    .show();
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new ThemeColorMaterialDialogBuilder(getContext())
                    .title(R.string.text_alert)
                    .content(message)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                            result.confirm();
                        }
                    })
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    })
                    .positiveText(R.string.ok)
                    .show();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress != 100);
        }
    }
}
