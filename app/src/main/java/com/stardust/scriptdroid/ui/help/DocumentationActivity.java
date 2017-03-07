package com.stardust.scriptdroid.ui.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.widget.CommonMarkdownView;

import java.io.IOException;

/**
 * Created by Stardust on 2017/2/1.
 */

public class DocumentationActivity extends BaseActivity {

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
        handleIntent();
        setUpUI();
    }

    private void handleIntent() {
        mTitle = getIntent().getStringExtra("title");
        String path = getIntent().getStringExtra("path");
        if (path == null) {
            int rawId = getIntent().getIntExtra("resId", 0);
            mDocumentation = FileUtils.readString(getResources().openRawResource(rawId));
        } else {
            try {
                mDocumentation = FileUtils.readString(getAssets().open("help/documentation/" + path + ".md"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpUI() {
        setContentView(R.layout.activity_document);
        setToolbarAsBack(mTitle);
        loadDocument();
    }

    private void loadDocument() {
        CommonMarkdownView markdownView = $(R.id.markdown);
        try {
            markdownView.loadMarkdown(mDocumentation);
        } catch (Exception e) {
            e.printStackTrace();
            markdownView.setText(R.string.text_load_failed);
        }
    }

}
