package org.autojs.autojs.ui.edit;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.MenuItem;

import com.stardust.pio.PFiles;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.build.BuildActivity;
import org.autojs.autojs.ui.build.BuildActivity_;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.autojs.autojs.ui.log.LogActivity_;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/9/28.
 */

public class EditorMenu {

    private EditorView mEditorView;
    private Context mContext;
    private CodeEditor mEditor;

    public EditorMenu(EditorView editorView) {
        mEditorView = editorView;
        mContext = editorView.getContext();
        mEditor = editorView.getEditor();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                showLog();
                return true;
            case R.id.action_force_stop:
                forceStop();
                return true;
            default:
                if (onEditOptionsSelected(item)) {
                    return true;
                }
                if (onJumpOptionsSelected(item)) {
                    return true;
                }
                if (onMoreOptionsSelected(item)) {
                    return true;
                }
        }
        return false;
    }

    private boolean onJumpOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_jump_to_line:
                jumpToLine();
                return true;
            case R.id.action_jump_to_start:
                mEditor.jumpToStart();
                return true;
            case R.id.action_jump_to_end:
                mEditor.jumpToEnd();
                return true;
            case R.id.action_jump_to_line_start:
                mEditor.jumpToLineStart();
                return true;
            case R.id.action_jump_to_line_end:
                mEditor.jumpToLineEnd();
                return true;
        }
        return false;
    }


    private boolean onMoreOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_console:
                showConsole();
                return true;
            case R.id.action_editor_text_size:
                mEditorView.selectTextSize();
                return true;
            case R.id.action_editor_theme:
                mEditorView.selectEditorTheme();
                return true;
            case R.id.action_open_by_other_apps:
                openByOtherApps();
                return true;
            case R.id.action_info:
                showInfo();
                return true;
            case R.id.action_build_apk:
                startBuildApkActivity();
                return true;

        }
        return false;
    }

    private void startBuildApkActivity() {
        BuildActivity_.intent(mContext)
                .extra(BuildActivity.EXTRA_SOURCE_FILE, mEditorView.getFile().getPath())
                .start();
    }


    private boolean onEditOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_or_replace:
                findOrReplace();
                return true;
            case R.id.action_copy_all:
                copyAll();
                return true;
            case R.id.action_copy_line:
                copyLine();
                return true;
            case R.id.action_delete_line:
                deleteLine();
                return true;
            case R.id.action_clear:
                mEditor.setText("");
                return true;
            case R.id.action_beautify:
                beautifyCode();
                return true;
        }
        return false;
    }

    private void jumpToLine() {
        mEditor.getLineCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showJumpDialog);
    }

    private void showJumpDialog(final int lineCount) {
        String hint = "1 ~ " + lineCount;
        new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_jump_to_line)
                .input(hint, "", (dialog, input) -> {
                    int line = Integer.parseInt(input.toString());
                    mEditor.jumpTo(line - 1, 0);
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .show();
    }

    private void showInfo() {
        Observable.zip(Observable.just(mEditor.getText()), mEditor.getLineCount(), (text, lineCount) -> {
            String size = PFiles.getHumanReadableSize(text.length());
            return String.format(Locale.getDefault(), mContext.getString(R.string.format_editor_info),
                    text.length(), lineCount, size);
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showInfo);

    }

    private void showInfo(String info) {
        new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_info)
                .content(info)
                .show();
    }

    private void copyLine() {
        mEditor.copyLine();
    }


    private void deleteLine() {
        mEditor.deleteLine();
    }

    private void paste() {
        mEditor.insert(ClipboardUtil.getClip(mContext).toString());
    }

    private void findOrReplace() {
        mEditor.getSelection()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        new FindOrReplaceDialogBuilder(mContext, mEditorView)
                                .setQueryIfNotEmpty(s)
                                .show()
                );

    }

    private void copyAll() {
        ClipboardUtil.setClip(mContext, mEditor.getText());
        Snackbar.make(mEditorView, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show();
    }


    private void showLog() {
        LogActivity_.intent(mContext).start();
    }

    private void showConsole() {
        mEditorView.showConsole();
    }

    private void forceStop() {
        mEditorView.forceStop();
    }

    private void openByOtherApps() {
        mEditorView.openByOtherApps();
    }

    private void beautifyCode() {
        mEditorView.beautifyCode();
    }

}
