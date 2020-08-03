package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Collections;

/**
 * Created by Stardust on 2017/3/5.
 */

public class CommonMarkdownView extends WebView {

    public interface OnPageFinishedListener {
        void onPageFinished(WebView view, String url);
    }

    private Parser mParser = Parser.builder().build();
    private HtmlRenderer mHtmlRender = HtmlRenderer.builder()
            .extensions(Collections.singleton(new HeadingAnchorExtension.Builder().build()))
            .build();

    private String mMarkdownHtml;
    private String mPadding = "16px";
    private OnPageFinishedListener mOnPageFinishedListener;

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

    public void setPadding(String padding) {
        mPadding = padding;
    }

    private void init() {
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:document.body.style.margin=\"" + mPadding + "\"; void 0");
                if (mOnPageFinishedListener != null) {
                    mOnPageFinishedListener.onPageFinished(view, url);
                }
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

    public void setOnPageFinishedListener(OnPageFinishedListener onPageFinishedListener) {
        mOnPageFinishedListener = onPageFinishedListener;
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

    public static class DialogBuilder extends ThemeColorMaterialDialogBuilder {

        private CommonMarkdownView mMarkdownView;
        private FrameLayout mContainer;

        public DialogBuilder(@NonNull Context context) {
            super(context);
            mContainer = new FrameLayout(context);
            mMarkdownView = new CommonMarkdownView(context);
            mContainer.addView(mMarkdownView);
            mContainer.setClipToPadding(true);
            customView(mContainer, false);
        }

        public DialogBuilder padding(int l, int t, int r, int b) {
            mContainer.setPadding(l, t, r, b);
            return this;
        }

        public DialogBuilder markdown(String md) {
            mMarkdownView.loadMarkdown(md);
            return this;
        }

    }
}