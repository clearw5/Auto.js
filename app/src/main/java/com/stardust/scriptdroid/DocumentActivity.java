package com.stardust.scriptdroid;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.view.MarkdownView;

/**
 * Created by Stardust on 2017/2/1.
 */

public class DocumentActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_document);
        setUpToolbar();
        loadDocument();
    }

    private void loadDocument() {
        MarkdownView markdownView = $(R.id.markdown);
        try {
            markdownView.loadMarkdown(FileUtils.readString(getResources().openRawResource(R.raw.document)));
        } catch (Exception e) {
            e.printStackTrace();
            markdownView.setText(R.string.text_load_failed);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(R.string.text_syntax_and_api);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
