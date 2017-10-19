package com.stardust.scriptdroid.ui.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.stardust.pio.PFiles;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.util.UnderuseExecutors;
import com.stardust.widget.CommonMarkdownView;
import com.stardust.scriptdroid.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

/**
 * Created by Stardust on 2017/2/1.
 */


public class DocumentationActivity extends BaseActivity {

    private CommonMarkdownView mCommonMarkdownView;

    public static void openDocumentation(Context context, String title, String assetPath) {
        context.startActivity(new Intent(context, DocumentationActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("title", title)
                .putExtra("path", assetPath));
    }

    private volatile String mDocumentation;
    private String mTitle;
    private AVLoadingIndicatorView mLoadingIndicatorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
        handleIntent(getIntent());
        setToolbarAsBack(mTitle);
    }

    private void handleIntent(Intent intent) {
        mTitle = intent.getStringExtra("title");
        final String path = intent.getStringExtra("path");
        if (path == null)
            return;
        UnderuseExecutors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDocumentation = PFiles.read(getAssets().open("help/" + path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadDocument();
                    }
                });
            }
        });

    }

    private void setUpUI() {
        setContentView(R.layout.activity_document);
        setUpLoadingView();
    }

    private void setUpLoadingView() {
        mLoadingIndicatorView = $(R.id.loading);
        mLoadingIndicatorView.setIndicatorColor(ThemeColorManagerCompat.getColorPrimary());
    }

    private void loadDocument() {
        mCommonMarkdownView = $(R.id.markdown);
        mCommonMarkdownView.setOnPageFinishedListener(new CommonMarkdownView.OnPageFinishedListener() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mLoadingIndicatorView.hide();
            }
        });
        try {
            mCommonMarkdownView.loadMarkdown(mDocumentation);
        } catch (Exception e) {
            e.printStackTrace();
            mCommonMarkdownView.setText(R.string.text_load_failed);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCommonMarkdownView.canGoBack()) {
            mCommonMarkdownView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
