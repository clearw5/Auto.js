package com.stardust.autojs.core.web;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.stardust.autojs.runtime.exception.ScriptInterruptedException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Stardust on 2017/4/1.
 */

public class InjectableWebClient extends WebViewClient {

    private static final String TAG = "InjectableWebClient";

    private Queue<Pair<String, ValueCallback<String>>> mToInjectJavaScripts = new LinkedList<>();
    private final ValueCallback<String> defaultCallback = new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
            Log.i(TAG, "onReceiveValue: " + value);
        }
    };
    private WebView mWebView;
    private Context mContext;
    private Scriptable mScriptable;
    private ScriptBridge mScriptBridge = new ScriptBridge();

    public InjectableWebClient(Context context, Scriptable scriptable) {
        mContext = context;
        mScriptable = scriptable;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mWebView = view;
        setUpWebView(view);
        while (!mToInjectJavaScripts.isEmpty()) {
            Pair<String, ValueCallback<String>> pair = mToInjectJavaScripts.poll();
            inject(view, pair.first, pair.second);
        }
        super.onPageFinished(view, url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView(WebView view) {
        view.addJavascriptInterface(mScriptBridge, "rhino");
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
    }

    private void inject(WebView view, String script, ValueCallback<String> callback) {
        view.evaluateJavascript(script, callback);
    }

    public void inject(String script, ValueCallback<String> callback) {
        if (mWebView != null) {
            inject(mWebView, script, callback);
            return;
        }
        mToInjectJavaScripts.offer(new Pair<>(script, callback));
    }

    public void inject(String script) {
        inject(script, defaultCallback);
    }

    public String injectAndWait(String script) {
        InjectReturnCallback callback = new InjectReturnCallback();
        inject(script, callback);
        return callback.waitResult();
    }


    private class ScriptBridge {

        private Object result;

        @JavascriptInterface
        public String eval(final String script) {
            result = null;
            mWebView.post(() -> {
                Log.v(TAG, "ScriptBridge.eval: " + script);
                result = mContext.evaluateString(mScriptable, script, "<eval-local>", 1, null);
                Log.v(TAG, "ScriptBridge.eval = " + result);
                synchronized (ScriptBridge.this) {
                    ScriptBridge.this.notify();
                }
            });
            synchronized (ScriptBridge.this) {
                try {
                    ScriptBridge.this.wait();
                } catch (InterruptedException e) {
                    throw new ScriptInterruptedException();
                }
            }
            return result.toString();
        }
    }

    private static class InjectReturnCallback implements ValueCallback<String> {

        private String result;

        @Override
        public void onReceiveValue(String value) {
            result = value;
            synchronized (this) {
                this.notify();
            }
        }

        String waitResult() {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new ScriptInterruptedException();
                }
            }
            return result;
        }
    }

}
