package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_NAME;
import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_PATH;

/**
 * Created by Stardust on 2017/1/29.
 */
@EActivity(R.layout.activity_edit)
public class EditActivity extends BaseActivity {


    @ViewById(R.id.editor_view)
    EditorView mEditor;


    private EditorMenu mEditorMenu;

    public static void editFile(Context context, String path) {
        editFile(context, null, path);
    }

    public static void editFile(Context context, String name, String path) {
        context.startActivity(new Intent(context, EditActivity_.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NAME, name));
    }

    public static void editFile(Context context, ScriptFile file) {
        editFile(context, file.getSimplifiedName(), file.getPath());
    }

    @AfterViews
    void setUpViews() {
        mEditor.handleIntent(getIntent());
        mEditorMenu = new EditorMenu(mEditor);
        setUpToolbar();
    }

    private void setUpToolbar() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mEditor.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mEditorMenu.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if (mEditor.isTextChanged()) {
            showExitConfirmDialog();
            return;
        }
        super.finish();
    }

    private void showExitConfirmDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_alert)
                .content(R.string.edit_exit_without_save_warn)
                .positiveText(R.string.text_cancel)
                .negativeText(R.string.text_save_and_exit)
                .neutralText(R.string.text_exit_directly)
                .onNegative((dialog, which) -> {
                    mEditor.saveFile();
                    EditActivity.super.finish();
                })
                .onNeutral((dialog, which) -> EditActivity.super.finish())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
