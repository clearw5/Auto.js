package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.mozilla.javascript.Scriptable;

/**
 * Created by Stardust on 2017/4/1.
 */

public class InjectableWebView extends WebView {

    private InjectableWebClient mInjectableWebClient;

    public InjectableWebView(Context context, org.mozilla.javascript.Context jsCtx, Scriptable scriptable) {
        super(context);
        init(jsCtx, scriptable);
    }

    private void init(org.mozilla.javascript.Context jsCtx, Scriptable scriptable) {
        mInjectableWebClient = new InjectableWebClient(jsCtx, scriptable);
        setWebViewClient(mInjectableWebClient);
    }

    public void inject(String script, ValueCallback<String> callback) {
        mInjectableWebClient.inject(script, callback);
    }

    public void inject(String script) {
        mInjectableWebClient.inject(script);
    }
}
