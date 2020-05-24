package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Stardust on 2017/11/29.
 */

public class JsWebView extends WebView {

    public JsWebView(Context context) {
        super(context);
        init();
    }

    public JsWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JsWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        WebSettings settings = getSettings();
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setDisplayZoomControls(false);
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
    }

}
