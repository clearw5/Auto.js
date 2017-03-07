package com.stardust.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Created by Stardust on 2017/3/5.
 */

public class CommonMarkdownView extends WebView {

    private Parser mParser = Parser.builder().build();
    private HtmlRenderer mHtmlRender = HtmlRenderer.builder().build();

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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonMarkdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void loadMarkdown(String markdown) {
        String html = renderMarkdown(markdown);
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
}
