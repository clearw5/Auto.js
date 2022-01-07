package com.tony;

import android.webkit.JavascriptInterface;

public class WebViewBridge {

    private BridgeHandler handler;

    public WebViewBridge(BridgeHandler handler) {
        this.handler = handler;
    }

    @JavascriptInterface
    public void postMessage(String params) {
        handler.exec(params);
    }
}
