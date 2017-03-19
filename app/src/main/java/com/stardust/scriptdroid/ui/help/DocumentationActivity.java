package com.stardust.scriptdroid.ui.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.widget.CommonMarkdownView;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static void openDocumentation(Context context, String title, int rawResId) {
        context.startActivity(new Intent(context, DocumentationActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("title", title)
                .putExtra("resId", rawResId));
    }

    private String mDocumentation;
    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        setUpUI();
    }

    private void handleIntent(Intent intent) {
        mTitle = intent.getStringExtra("title");
        String path = intent.getStringExtra("path");
        int rawId = intent.getIntExtra("resId", 0);
        if (path != null) {
            try {
                mDocumentation = FileUtils.readString(getAssets().open("help/" + path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (rawId != 0) {
            mDocumentation = FileUtils.readString(getResources().openRawResource(rawId));
        }
    }

    private void setUpUI() {
        setContentView(R.layout.activity_document);
        setToolbarAsBack(mTitle);
        loadDocument();
    }

    private void loadDocument() {
        mCommonMarkdownView = $(R.id.markdown);
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
