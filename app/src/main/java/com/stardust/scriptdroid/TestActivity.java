package com.stardust.scriptdroid;

import android.os.Bundle;

import com.jecelyin.editor.v2.Pref;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.ui.EditorDelegate;
import com.jecelyin.editor.v2.ui.IActivity;
import com.jecelyin.editor.v2.ui.TabManager;
import com.jecelyin.editor.v2.view.EditorView;

/**
 * Created by Stardust on 2017/1/29.
 */

public class TestActivity extends BaseActivity implements IActivity{

    public void onCreate(Bundle b) {
        super.onCreate(b);
        Pref.getInstance(this).setTheme(0);
        setContentView(R.layout.edit_layout);
        EditorDelegate editorDelegate = new EditorDelegate(0, "Hello 920 Editor", "Hello 920");
        EditorView editorView = $(R.id.editor);
        editorDelegate.setEditorView(editorView);
    }

    @Override
    public void startPickPathActivity(String s, String s1) {

    }

    @Override
    public void doNextCommand() {

    }

    @Override
    public TabManager getTabManager() {
        return null;
    }

    @Override
    public void setMenuStatus(int i, int i1) {

    }

    @Override
    public void onDocumentChanged(int i) {

    }

    @Override
    public void doCommand(Command command) {

    }

    @Override
    public void openFile(String s, String s1, int i) {

    }
}
