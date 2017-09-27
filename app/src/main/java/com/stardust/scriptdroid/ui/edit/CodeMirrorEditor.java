package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.stardust.pio.PFile;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/9/27.
 */

public class CodeMirrorEditor extends FrameLayout {


    private static String[] sAvailableThemes;
    private String mTheme = "neo";

    public interface Callback {
        void onChange();

        void updateCodeCompletion(int fromLine, int fromCh, int toLine, int toCh, String[] list);
    }

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
        mWebView.loadUrl("file:///android_asset/editor/index.html");
    }

    private void setupWebView() {
        mWebView = new WebView(getContext());
        setupWebSettings();
        mWebView.addJavascriptInterface(mJavaScriptBridge, "__bridge__");
        addView(mWebView);
    }

    private void setupWebSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setNeedInitialFocus(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    public void setText(final String text) {
        mPageFinished.promise().done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void result) {
                String escapedString = StringEscapeUtils.escapeEcmaScript(text);
                evalJavaScript("editor.setValue('" + escapedString + "');");
            }
        });
    }

    public Observable<String> getText() {
        mJavaScriptBridge.mText = PublishSubject.create();
        evalJavaScript("__bridge__.setText(editor.getValue());");
        return mJavaScriptBridge.mText;
    }


    public void undo() {
        evalJavaScript("editor.undo();");
    }

    public void redo() {
        evalJavaScript("editor.redo();");
    }

    private void evalJavaScript(String script) {
        mWebView.loadUrl("javascript:" + script);
    }

    public void replace(String text, int fromLine, int fromCh, int toLine, int toCh) {
        String js = "editor.replaceRange('%s', {line: %d, ch: %d}, {line: %d, ch: %d})";
        evalJavaScript(String.format(Locale.getDefault(), js, text, fromLine, fromCh, toLine, toCh));
    }

    public class JavaScriptBridge {

        private PublishSubject<String> mText;

        @JavascriptInterface
        public void setText(String text) {
            if (mText != null) {
                mText.onNext(text);
                mText.onComplete();
                mText = null;
            }
        }

        @JavascriptInterface
        public void onChange() {
            if (mCallback != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onChange();
                    }
                });
            }
        }

        @JavascriptInterface
        public void updateCodeCompletion(final int fromLine, final int fromCh, final int toLine, final int toCh, final String[] list) {
            if (mCallback != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.updateCodeCompletion(fromLine, fromCh, toLine, toCh, list);
                    }
                });
            }
        }


    }

    private class MyWebViewClient extends WebViewClient {

        private final String INIT_SCRIPT = "editor.on('change', function(){__bridge__.onChange();});";

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mPageFinished.resolve(null);
            evalJavaScript(INIT_SCRIPT);
           // evalJavaScript("editor.setSize(" + mWebView.getMeasuredWidth() + ", " + mWebView.getMeasuredHeight() + ");");
        }
    }
}
