package com.stardust.scriptdroid.external.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.ui.EditorDelegate;
import com.jecelyin.editor.v2.view.EditorView;
import com.jecelyin.editor.v2.view.menu.MenuDef;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.scriptdroid.ui.edit.completion.InputMethodEnhanceBar;
import com.stardust.scriptdroid.ui.edit.editor920.Editor920Activity;
import com.stardust.scriptdroid.ui.edit.editor920.Editor920Utils;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.widget.ToolbarMenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/4/5.
 */
@EActivity(R.layout.activity_tasker_script_edit)
public class TaskerScriptEditActivity extends Editor920Activity {

    public static final int REQUEST_CODE = "Love you. Can we go back?".hashCode() >> 16;
    public static final String EXTRA_CONTENT = "Still Love Eating 17.4.5";

    public static void edit(Activity activity, String title, String summary, String content) {
        activity.startActivityForResult(new Intent(activity, TaskerScriptEditActivity_.class)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra("summary", summary)
                .putExtra("title", title), REQUEST_CODE);
    }

    private EditorDelegate mEditorDelegate;
    private String mTitle, mSummary;
    @ViewById(R.id.redo)
    ToolbarMenuItem mRedo;
    @ViewById(R.id.undo)
    ToolbarMenuItem mUndo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.EditorTheme);
        handleIntent(getIntent());
    }

    @AfterViews
    void setUpViews() {
        ((TextView) findViewById(R.id.summary)).setText(mSummary);
        ThemeColorManager.addActivityStatusBar(this);
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mTitle);
        setUpEditor();
    }

    private void handleIntent(Intent intent) {
        mTitle = intent.getStringExtra("title");
        mSummary = intent.getStringExtra("summary");
        String content = intent.getStringExtra(EXTRA_CONTENT);
        mEditorDelegate = new EditorDelegate(0, mTitle, content);
    }

    private void setUpEditor() {
        final EditorView editorView = (EditorView) findViewById(R.id.editor);
        mEditorDelegate.setEditorView(editorView);
        Editor920Utils.setLang(mEditorDelegate, "JavaScript");
        editorView.getEditText().setHorizontallyScrolling(true);
        setUpInputMethodEnhanceBar(editorView);
    }

    private void setUpInputMethodEnhanceBar(final EditorView editorView) {
        InputMethodEnhanceBar inputMethodEnhanceBar = (InputMethodEnhanceBar) findViewById(R.id.input_method_enhance_bar);
        inputMethodEnhanceBar.setEditTextBridge(new EditActivity.InputMethodEnhanceBarBridge(this, editorView.getEditText()));
    }

    @Click(R.id.undo)
    void undo() {
        Command command = new Command(Command.CommandEnum.UNDO);
        mEditorDelegate.doCommand(command);
    }

    @Click(R.id.redo)
    void redo() {
        Command command = new Command(Command.CommandEnum.REDO);
        mEditorDelegate.doCommand(command);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_CONTENT, mEditorDelegate.getText()));
        super.finish();
    }

    public void setMenuStatus(int menuResId, int status) {
        boolean disabled = status == MenuDef.STATUS_DISABLED;
        if (menuResId == com.jecelyin.editor.v2.R.id.m_redo) {
            mRedo.setEnabled(!disabled);
        } else if (menuResId == com.jecelyin.editor.v2.R.id.m_undo) {
            mUndo.setEnabled(!disabled);
        }
    }
}
