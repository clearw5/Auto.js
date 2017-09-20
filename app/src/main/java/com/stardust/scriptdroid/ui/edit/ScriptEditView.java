package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.core.widget.TextView;
import com.jecelyin.editor.v2.ui.EditorDelegate;
import com.jecelyin.editor.v2.view.EditorView;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.edit.completion.InputMethodEnhanceBar;
import com.stardust.scriptdroid.ui.edit.editor920.Editor920Utils;

import org.androidannotations.annotations.EViewGroup;

import java.io.File;

/**
 * Created by Stardust on 2017/8/21.
 */
@EViewGroup(R.layout.script_edit_view)
public class ScriptEditView extends FrameLayout {

    private class InputMethodEnhanceBarBridge implements InputMethodEnhanceBar.EditTextBridge {

        private TextView mTextView;

        public InputMethodEnhanceBarBridge(TextView textView) {
            mTextView = textView;
        }

        @Override
        public void appendText(CharSequence text) {
            insertText(text);
        }

        @Override
        public void backspace(int count) {

        }

        @Override
        public TextView getEditText() {
            return mTextView;
        }


    }

    private EditorDelegate mEditorDelegate;
    private File mFile;
    private boolean mReadOnly = false;

    public ScriptEditView(@NonNull Context context) {
        super(context);
    }

    public ScriptEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScriptEditView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isReadOnly() {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        mReadOnly = readOnly;
    }

    public void setFile(File file) {
        mFile = file;
        mEditorDelegate = new EditorDelegate(0, mFile, 0, "utf-8");
        setUpEditor();
    }

    public void setContent(String name, String str) {
        mEditorDelegate = new EditorDelegate(0, name, str);

    }

    public void insertText(CharSequence text) {
        Command c = new Command(Command.CommandEnum.INSERT_TEXT);
        c.object = text;
        mEditorDelegate.doCommand(c);
    }

    private void setUpEditor() {
        final EditorView editorView = (EditorView) findViewById(R.id.editor);
        mEditorDelegate.setEditorView(editorView);
        if (mFile == null)
            Editor920Utils.setLang(mEditorDelegate, "JavaScript");
        editorView.getEditText().setReadOnly(mReadOnly);
        editorView.getEditText().setHorizontallyScrolling(true);
        setUpInputMethodEnhanceBar(editorView);
    }


    private void setUpInputMethodEnhanceBar(final EditorView editorView) {
        InputMethodEnhanceBar inputMethodEnhanceBar = (InputMethodEnhanceBar) findViewById(R.id.input_method_enhance_bar);
        if (mReadOnly) {
            inputMethodEnhanceBar.setVisibility(View.GONE);
        } else {
            inputMethodEnhanceBar.setEditTextBridge(new InputMethodEnhanceBarBridge(editorView.getEditText()));
        }
    }


}
