package com.stardust.scriptdroid.ui.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;

import java.io.IOException;

/**
 * Created by Stardust on 2017/3/14.
 */

public class LocalWebViewActivity extends BaseActivity {

    // TODO: 2017/3/14 缓存asset

    private String mTitle;
    private String mHtml;
    private WebView mWebView;

    public static void openAssetsHtml(Context context, String title, String path) {
        context.startActivity(new Intent(context, LocalWebViewActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("title", title)
                .putExtra("path", path));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        setUpUI();
    }

    private void handleIntent(Intent intent) {
        mTitle = intent.getStringExtra("title");
        String path = intent.getStringExtra("path");
        if (path != null)
            try {
                mHtml = PFile.read(getAssets().open("help/" + path));
            } catch (IOException e) {
                e.printStackTrace();
                mHtml = getString(R.string.text_load_failed);
            }
    }

    private void setUpUI() {
        setContentView(R.layout.activity_local_webview);
        setToolbarAsBack(mTitle);
        loadHtml();
    }

    private void loadHtml() {
        mWebView = $(R.id.webView);
        try {
            mWebView.loadDataWithBaseURL(null, mHtml, "text/html", "utf-8", null);
        } catch (Exception e) {
            e.printStackTrace();
            mWebView.loadDataWithBaseURL(null, getString(R.string.text_load_failed), "text/html", "utf-8", null);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
