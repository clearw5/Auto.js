package com.stardust.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Stardust on 2017/3/5.
 */

public class CommonMarkdownView extends WebView {

    private Parser mParser = Parser.builder().build();
    private HtmlRenderer mHtmlRender = HtmlRenderer.builder()
            .extensions(Collections.singleton(new HeadingAnchorExtension.Builder().build()))
            .build();

    private String mMarkdownHtml;

    public CommonMarkdownView(Context context) {
        super(context);
        init();
    }

    public CommonMarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommonMarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:document.body.style.margin=\"12%\"; void 0");
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(request.getUrl()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            }

        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonMarkdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void loadMarkdown(String markdown) {
        mMarkdownHtml = renderMarkdown(markdown);
        loadHtml(mMarkdownHtml);
    }

    private void loadHtml(String html) {
        loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    private String renderMarkdown(String markdown) {
        Node document = mParser.parse(markdown);
        return mHtmlRender.render(document);
    }

    public void setText(int resId) {
        setText(getContext().getString(resId));
    }

    private void setText(String text) {
        loadDataWithBaseURL(null, text, "text/plain", "utf-8", null);
    }

    public void goBack() {
        super.goBack();
        if (!canGoBack() && mMarkdownHtml != null) {
            loadHtml(mMarkdownHtml);
        }
    }
}