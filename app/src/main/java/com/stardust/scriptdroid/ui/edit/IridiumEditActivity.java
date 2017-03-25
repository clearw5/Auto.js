package com.stardust.scriptdroid.ui.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.ui.BaseActivity;

import xyz.iridiumion.iridiumhighlightingeditor.editor.HighlightingDefinition;
import xyz.iridiumion.iridiumhighlightingeditor.editor.IridiumHighlightingEditorJ;
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.definitions.JavaScriptHighlightingDefinition;

/**
 * Created by Stardust on 2017/3/22.
 */

public class IridiumEditActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IridiumHighlightingEditorJ editorJ = new IridiumHighlightingEditorJ(this);
        editorJ.setText("1234567");
        editorJ.loadHighlightingDefinition(new JavaScriptHighlightingDefinition());
        setContentView(editorJ);
    }
}
